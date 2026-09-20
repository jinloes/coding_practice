package com.jinloes.practice_plugin.workspace;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.RunManager;
import com.intellij.execution.RunnerAndConfigurationSettings;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.PathManager;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.module.StdModuleTypes;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.JavaSdk;
import com.intellij.openapi.projectRoots.JavaSdkVersion;
import com.intellij.openapi.projectRoots.ProjectJdkTable;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.roots.CompilerModuleExtension;
import com.intellij.openapi.roots.LanguageLevelModuleExtension;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VfsUtilCore;
import com.intellij.pom.java.LanguageLevel;
import com.jinloes.practice_plugin.catalog.ExerciseCatalog;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service(Service.Level.PROJECT)
public final class PracticeModuleWorkspace implements Disposable {
    private static final String OWNED_SDK_PREFIX = "Algorithm Practice IDE JDK ";
    private static final String MARKER = ".algorithm-practice-generated";
    private static final String ATTEMPT_ID_PATTERN = "[a-z][a-z0-9-]*-[a-f0-9]{32}";
    private final Project project;
    private final Set<RunnerAndConfigurationSettings> temporaryConfigurations = new LinkedHashSet<>();
    private final Map<String, Module> modules = new LinkedHashMap<>();
    private final Set<Path> ownedDirectories = new LinkedHashSet<>();
    private Sdk sdk;
    private Sdk ownedFallbackSdk;

    public PracticeModuleWorkspace(Project project) {
        this.project = project;
    }

    public static PracticeModuleWorkspace get(Project project) {
        return project.getService(PracticeModuleWorkspace.class);
    }

    public synchronized Module open(ManagedPracticeWorkspace.Attempt attempt) throws ExecutionException {
        Module current = modules.get(attempt.id());
        if (current != null && !current.isDisposed()) {
            return current;
        }
        try {
            sdk = sdk == null ? selectSdk() : sdk;
            Path output = generatedBase("module-output").resolve(attempt.id()).normalize();
            createMarkedDirectory(output, "output", attempt.id());
            ApplicationManager.getApplication().runWriteAction(() -> {
                Module module = ModuleManager.getInstance(project).newNonPersistentModule(
                        moduleName(attempt.id()), StdModuleTypes.JAVA.getId());
                var model = ModuleRootManager.getInstance(module).getModifiableModel();
                var contentRoot = LocalFileSystem.getInstance().refreshAndFindFileByNioFile(attempt.directory());
                var sourceRoot = LocalFileSystem.getInstance().refreshAndFindFileByNioFile(
                        attempt.solution().getParent().getParent().getParent().getParent());
                if (contentRoot == null || sourceRoot == null) {
                    model.dispose();
                    ModuleManager.getInstance(project).disposeModule(module);
                    throw new IllegalArgumentException("Managed attempt roots are unavailable in the IDE.");
                }
                var entry = model.addContentEntry(contentRoot);
                entry.addSourceFolder(sourceRoot, false);
                model.setSdk(sdk);
                CompilerModuleExtension compiler = model.getModuleExtension(CompilerModuleExtension.class);
                compiler.inheritCompilerOutputPath(false);
                compiler.setCompilerOutputPath(VfsUtilCore.pathToUrl(output.toString()));
                compiler.setCompilerOutputPathForTests(VfsUtilCore.pathToUrl(output.resolve("test").toString()));
                LanguageLevelModuleExtension language = model.getModuleExtension(LanguageLevelModuleExtension.class);
                language.setLanguageLevel(LanguageLevel.JDK_17);
                model.commit();
                modules.put(attempt.id(), module);
            });
            VerificationWorkspace.cleanupAbandoned();
            cleanupGenerated("harnesses", false);
            cleanupGenerated("module-output", false);
            return modules.get(attempt.id());
        } catch (IOException | IllegalArgumentException exception) {
            throw new ExecutionException("Cannot prepare the managed Java module: " + exception.getMessage(), exception);
        }
    }

    public synchronized Path prepareExampleHarness(
            ManagedPracticeWorkspace.Attempt attempt,
            ExerciseCatalog.Exercise exercise
    ) throws ExecutionException {
        Module module = open(attempt);
        Path harness = generatedBase("harnesses").resolve(attempt.id()).normalize();
        try {
            if (Files.exists(harness, LinkOption.NOFOLLOW_LINKS)) {
                deleteMarkedTree(harness);
            }
            createMarkedDirectory(harness, "harness", attempt.id());
            Path sourceRoot = harness.resolve("src");
            Path runner = sourceRoot.resolve("com/jinloes/practice/ExampleRunner.java");
            Files.createDirectories(runner.getParent());
            Files.writeString(runner, ExerciseCatalog.resource(exercise, "ExampleRunner.java"),
                    StandardCharsets.UTF_8, StandardOpenOption.CREATE_NEW);
            ApplicationManager.getApplication().runWriteAction(() -> {
                var model = ModuleRootManager.getInstance(module).getModifiableModel();
                var root = LocalFileSystem.getInstance().refreshAndFindFileByNioFile(harness);
                var source = LocalFileSystem.getInstance().refreshAndFindFileByNioFile(sourceRoot);
                if (root == null || source == null) {
                    model.dispose();
                    throw new IllegalArgumentException("Generated example runner is unavailable in the IDE.");
                }
                var entry = model.addContentEntry(root);
                entry.addSourceFolder(source, false);
                model.commit();
            });
            return runner;
        } catch (IOException | IllegalArgumentException exception) {
            throw new ExecutionException("Cannot prepare the generated example runner: "
                    + exception.getMessage(), exception);
        }
    }

    public synchronized void clearExampleHarness(ManagedPracticeWorkspace.Attempt attempt) {
        Path harness = generatedBase("harnesses").resolve(attempt.id()).normalize();
        Module module = modules.get(attempt.id());
        if (module != null && !module.isDisposed()) {
            ApplicationManager.getApplication().runWriteAction(() -> {
                var model = ModuleRootManager.getInstance(module).getModifiableModel();
                for (var entry : model.getContentEntries()) {
                    if (VfsUtilCore.pathToUrl(harness.toString()).equals(entry.getUrl())) {
                        model.removeContentEntry(entry);
                    }
                }
                model.commit();
            });
        }
        try {
            if (Files.exists(harness, LinkOption.NOFOLLOW_LINKS)) {
                deleteMarkedTree(harness);
            }
        } catch (IOException ignored) {
            // Preserve paths whose ownership can no longer be proved.
        }
    }

    public void compileExamples(ManagedPracticeWorkspace.Attempt attempt, Path runner) throws ExecutionException {
        Path output = generatedBase("module-output").resolve(attempt.id()).normalize();
        Path compiler = javaHome().resolve("bin").resolve(
                System.getProperty("os.name").startsWith("Windows") ? "javac.exe" : "javac");
        Path diagnostics = output.resolve("compiler.log");
        Process process = null;
        try {
            process = new ProcessBuilder(
                    compiler.toString(),
                    "--release", "17",
                    "-encoding", "UTF-8",
                    "-g",
                    "-d", output.toString(),
                    attempt.solution().toString(),
                    runner.toString())
                    .redirectErrorStream(true)
                    .redirectOutput(diagnostics.toFile())
                    .start();
            if (!process.waitFor(30, TimeUnit.SECONDS)) {
                process.destroyForcibly();
                throw new ExecutionException("Example compilation exceeded 30 seconds.");
            }
            if (process.exitValue() != 0) {
                String outputText = Files.readString(diagnostics, StandardCharsets.UTF_8);
                throw new ExecutionException("Example compilation failed:\n"
                        + outputText.substring(0, Math.min(outputText.length(), 64 * 1024)));
            }
        } catch (IOException exception) {
            throw new ExecutionException("Could not compile examples: " + exception.getMessage(), exception);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new ExecutionException("Example compilation was interrupted.", exception);
        } finally {
            if (process != null && process.isAlive()) {
                process.destroyForcibly();
            }
        }
    }

    public synchronized Path javaHome() throws ExecutionException {
        if (sdk == null) {
            sdk = selectSdk();
        }
        if (sdk.getHomePath() == null) {
            throw new ExecutionException("No usable full Java 17 or newer JDK is available.");
        }
        return Path.of(sdk.getHomePath());
    }

    public synchronized void track(RunnerAndConfigurationSettings settings) {
        temporaryConfigurations.add(settings);
    }

    public synchronized void untrack(RunnerAndConfigurationSettings settings) {
        temporaryConfigurations.remove(settings);
    }

    public synchronized boolean cleanupGeneratedArtifacts() {
        if (project.getService(com.jinloes.practice_plugin.run.PracticeRunner.class).isRunning()) {
            return false;
        }
        ApplicationManager.getApplication().runWriteAction(() -> {
            RunManager manager = RunManager.getInstance(project);
            temporaryConfigurations.forEach(manager::removeConfiguration);
            temporaryConfigurations.clear();
            modules.values().stream().filter(module -> !module.isDisposed())
                    .forEach(module -> ModuleManager.getInstance(project).disposeModule(module));
            modules.clear();
            VerificationWorkspace.cleanupAbandoned();
            cleanupGenerated("harnesses", true);
            cleanupGenerated("module-output", true);
            removeAbandonedFallbackSdks();
        });
        return true;
    }

    @Override
    public synchronized void dispose() {
        ApplicationManager.getApplication().runWriteAction(() -> {
            RunManager manager = RunManager.getInstance(project);
            temporaryConfigurations.forEach(manager::removeConfiguration);
            temporaryConfigurations.clear();
            modules.values().stream().filter(module -> !module.isDisposed())
                    .forEach(module -> ModuleManager.getInstance(project).disposeModule(module));
            modules.clear();
            for (Path path : Set.copyOf(ownedDirectories)) {
                try {
                    deleteMarkedTree(path);
                } catch (IOException ignored) {
                    // Preserve anything whose ownership cannot be revalidated.
                }
            }
            ownedDirectories.clear();
            if (ownedFallbackSdk != null && ProjectJdkTable.getInstance().findJdk(ownedFallbackSdk.getName()) != null) {
                ProjectJdkTable.getInstance().removeJdk(ownedFallbackSdk);
            }
            sdk = null;
            ownedFallbackSdk = null;
        });
    }

    private Sdk selectSdk() throws ExecutionException {
        removeAbandonedFallbackSdks();
        Sdk projectSdk = ProjectRootManager.getInstance(project).getProjectSdk();
        if (isUsableJavaSdk(projectSdk)) {
            return projectSdk;
        }
        for (Sdk candidate : ProjectJdkTable.getInstance().getAllJdks()) {
            if (isUsableJavaSdk(candidate)) {
                return candidate;
            }
        }
        Path ideJdk = Path.of(System.getProperty("java.home"));
        if (!JavaSdk.getInstance().isValidSdkHome(ideJdk.toString())) {
            throw new ExecutionException("The IDE runtime is not a full Java 17 or newer JDK.");
        }
        Sdk fallback = JavaSdk.getInstance().createJdk(
                OWNED_SDK_PREFIX + ProcessHandle.current().pid() + "-"
                        + Integer.toUnsignedString(project.getLocationHash().hashCode()),
                ideJdk.toString(), false);
        if (!isUsableJavaSdk(fallback)) {
            throw new ExecutionException("The IDE runtime is not a full Java 17 or newer JDK.");
        }
        ApplicationManager.getApplication().runWriteAction(
                () -> ProjectJdkTable.getInstance().addJdk(fallback));
        ownedFallbackSdk = fallback;
        return fallback;
    }

    private void createMarkedDirectory(Path directory, String kind, String attemptId) throws IOException {
        String baseKind = switch (kind) {
            case "harness" -> "harnesses";
            case "output" -> "module-output";
            default -> throw new IOException("Unknown generated-directory kind.");
        };
        Path base = generatedBase(baseKind);
        if (!directory.startsWith(base) || directory.equals(base) || Files.isSymbolicLink(directory)) {
            throw new IOException("Generated path escapes the plugin-owned root.");
        }
        if (!attemptId.matches(ATTEMPT_ID_PATTERN)
                || !attemptId.equals(directory.getFileName().toString())) {
            throw new IOException("Generated path does not match its managed attempt.");
        }
        if (Files.exists(directory, LinkOption.NOFOLLOW_LINKS)) {
            Marker existing = readMarker(directory);
            if (!kind.equals(existing.kind()) || !attemptId.equals(existing.attemptId())
                    || !isInactive(existing.pid())) {
                throw new IOException("Existing generated directory is not a recoverable inactive attempt artifact.");
            }
            deleteMarkedTree(directory);
        }
        Files.createDirectories(directory);
        Files.writeString(directory.resolve(MARKER), """
                schema=1
                kind=%s
                attempt=%s
                pid=%s
                """.formatted(kind, attemptId, ProcessHandle.current().pid()), StandardCharsets.UTF_8,
                StandardOpenOption.CREATE_NEW);
        ownedDirectories.add(directory);
    }

    private void cleanupGenerated(String kind, boolean includeCurrentProcess) {
        Path base = generatedBase(kind);
        if (!Files.isDirectory(base, LinkOption.NOFOLLOW_LINKS) || Files.isSymbolicLink(base)) {
            return;
        }
        try (var entries = Files.list(base)) {
            for (Path candidate : entries.toList()) {
                try {
                    Marker marker = readMarker(candidate);
                    if ((includeCurrentProcess && marker.pid() == ProcessHandle.current().pid())
                            || isInactive(marker.pid())) {
                        deleteMarkedTree(candidate);
                    }
                } catch (IOException | SecurityException ignored) {
                    // Unmarked, malformed, active, and uncertain paths remain untouched.
                }
            }
        } catch (IOException ignored) {
            // A later explicit cleanup can retry.
        }
    }

    private void deleteMarkedTree(Path root) throws IOException {
        readMarker(root);
        try (var paths = Files.walk(root)) {
            for (Path path : paths.sorted(Comparator.reverseOrder()).toList()) {
                Files.delete(path);
            }
        }
        ownedDirectories.remove(root);
    }

    private Marker readMarker(Path root) throws IOException {
        Path actualBase = root.getParent().toRealPath();
        Path expectedHarness = generatedBase("harnesses");
        Path expectedOutput = generatedBase("module-output");
        if ((!actualBase.equals(expectedHarness) && !actualBase.equals(expectedOutput))
                || !Files.isDirectory(root, LinkOption.NOFOLLOW_LINKS) || Files.isSymbolicLink(root)
                || !Files.isRegularFile(root.resolve(MARKER), LinkOption.NOFOLLOW_LINKS)
                || Files.isSymbolicLink(root.resolve(MARKER))) {
            throw new IOException("Refusing to modify an unmarked generated directory.");
        }
        Properties properties = new Properties();
        try (var reader = Files.newBufferedReader(root.resolve(MARKER), StandardCharsets.UTF_8)) {
            properties.load(reader);
        }
        String kind = properties.getProperty("kind", "");
        String attemptId = properties.getProperty("attempt", "");
        String pidText = properties.getProperty("pid", "");
        String expectedKind = actualBase.equals(expectedHarness) ? "harness" : "output";
        if (!"1".equals(properties.getProperty("schema"))
                || !expectedKind.equals(kind)
                || !attemptId.matches(ATTEMPT_ID_PATTERN)
                || !attemptId.equals(root.getFileName().toString())
                || !pidText.matches("[1-9][0-9]*")) {
            throw new IOException("Invalid generated-directory marker.");
        }
        try {
            return new Marker(kind, attemptId, Long.parseLong(pidText));
        } catch (NumberFormatException exception) {
            throw new IOException("Invalid generated-directory marker.", exception);
        }
    }

    private static boolean isInactive(long pid) {
        return ProcessHandle.of(pid).map(handle -> !handle.isAlive()).orElse(true);
    }

    private Path generatedBase(String kind) {
        return Path.of(PathManager.getSystemPath(), "algorithm-practice", kind,
                Integer.toUnsignedString(project.getLocationHash().hashCode())).toAbsolutePath().normalize();
    }

    private String moduleName(String attemptId) {
        return "Algorithm Practice " + attemptId + " " + Integer.toUnsignedString(project.getLocationHash().hashCode());
    }

    private static void removeAbandonedFallbackSdks() {
        Runnable removal = () -> removeAbandonedFallbackSdksUnderWriteAction();
        if (ApplicationManager.getApplication().isWriteAccessAllowed()) {
            removal.run();
        } else {
            ApplicationManager.getApplication().runWriteAction(removal);
        }
    }

    private static void removeAbandonedFallbackSdksUnderWriteAction() {
        ProjectJdkTable table = ProjectJdkTable.getInstance();
        for (Sdk candidate : table.getAllJdks()) {
            if (!candidate.getName().startsWith(OWNED_SDK_PREFIX)) {
                continue;
            }
            String suffix = candidate.getName().substring(OWNED_SDK_PREFIX.length());
            int separator = suffix.indexOf('-');
            if (separator <= 0) {
                continue;
            }
            try {
                long pid = Long.parseLong(suffix.substring(0, separator));
                if (!ProcessHandle.of(pid).map(ProcessHandle::isAlive).orElse(false)) {
                    table.removeJdk(candidate);
                }
            } catch (NumberFormatException ignored) {
                // Unrecognized SDK names remain untouched.
            }
        }
    }

    private static boolean isUsableJavaSdk(Sdk candidate) {
        if (candidate == null || candidate.getHomePath() == null || candidate.getSdkType() != JavaSdk.getInstance()) {
            return false;
        }
        JavaSdkVersion version = JavaSdk.getInstance().getVersion(candidate);
        return version != null && version.isAtLeast(JavaSdkVersion.JDK_17);
    }

    private record Marker(String kind, String attemptId, long pid) {}
}
