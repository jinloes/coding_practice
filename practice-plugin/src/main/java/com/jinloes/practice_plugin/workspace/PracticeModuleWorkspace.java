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
import com.intellij.openapi.roots.ModifiableRootModel;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.roots.OrderRootType;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.roots.libraries.Library;
import com.intellij.openapi.roots.libraries.LibraryTable;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.JarFileSystem;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VfsUtilCore;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.pom.java.LanguageLevel;
import com.jinloes.practice_plugin.catalog.ExampleLibraries;
import com.jinloes.practice_plugin.catalog.ExerciseCatalog;
import com.jinloes.practice_plugin.platform.Os;
import com.jinloes.practice_plugin.platform.OwnedDirectory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service(Service.Level.PROJECT)
public final class PracticeModuleWorkspace implements Disposable {
    private static final String OWNED_SDK_PREFIX = "Algorithm Practice IDE JDK ";
    private static final String MARKER = ".algorithm-practice-generated";
    private static final String EXAMPLE_LIBRARY = "algorithm-practice-example-libraries";
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
            List<Path> libraries = ExampleLibraries.extractTo(harness.resolve("libs"));
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
                attachExampleLibraries(model, libraries);
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
                detachExampleLibraries(model);
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
        Path compiler = javaHome().resolve("bin").resolve(Os.IS_WINDOWS ? "javac.exe" : "javac");
        Path diagnostics = output.resolve("compiler.log");
        Process process = null;
        try {
            process = new ProcessBuilder(
                    compiler.toString(),
                    "--release", "17",
                    "-encoding", "UTF-8",
                    "-g",
                    "-classpath", ExampleLibraries.classpath(exampleLibraries(attempt)),
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

    /**
     * Removes inactive generated artifacts. The caller owns the busy-state decision: this must not
     * be invoked while a practice run is active, because the run owns the modules and temporary
     * configurations removed here.
     *
     * @return always {@code true}; cleanup is unconditional once the caller has decided to run it
     */
    public synchronized boolean cleanupGeneratedArtifacts() {
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

    private List<Path> exampleLibraries(ManagedPracticeWorkspace.Attempt attempt) throws ExecutionException {
        Path libs = generatedBase("harnesses").resolve(attempt.id()).resolve("libs").normalize();
        List<Path> jars = ExampleLibraries.names().stream().map(libs::resolve).toList();
        for (Path jar : jars) {
            if (!Files.isRegularFile(jar, LinkOption.NOFOLLOW_LINKS) || Files.isSymbolicLink(jar)) {
                throw new ExecutionException("Bundled example libraries are missing from the generated harness.");
            }
        }
        return jars;
    }

    private static void attachExampleLibraries(ModifiableRootModel model, List<Path> jars) {
        detachExampleLibraries(model);
        Library library = model.getModuleLibraryTable().createLibrary(EXAMPLE_LIBRARY);
        Library.ModifiableModel libraryModel = library.getModifiableModel();
        for (Path jar : jars) {
            LocalFileSystem.getInstance().refreshAndFindFileByNioFile(jar);
            libraryModel.addRoot(VirtualFileManager.constructUrl(JarFileSystem.PROTOCOL,
                    FileUtil.toSystemIndependentName(jar.toString()) + JarFileSystem.JAR_SEPARATOR),
                    OrderRootType.CLASSES);
        }
        libraryModel.commit();
    }

    private static void detachExampleLibraries(ModifiableRootModel model) {
        LibraryTable table = model.getModuleLibraryTable();
        for (Library library : table.getLibraries()) {
            if (EXAMPLE_LIBRARY.equals(library.getName())) {
                table.removeLibrary(library);
            }
        }
    }

    private void createMarkedDirectory(Path directory, String kind, String attemptId) throws IOException {
        OwnedDirectory owned = owned(kind);
        owned.requireOwned(directory);
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
        owned.create(directory, markerText(kind, attemptId));
        ownedDirectories.add(directory);
    }

    private static String markerText(String kind, String attemptId) {
        return """
                schema=1
                kind=%s
                attempt=%s
                pid=%s
                """.formatted(kind, attemptId, ProcessHandle.current().pid());
    }

    private void cleanupGenerated(String baseName, boolean includeCurrentProcess) {
        String kind = kindOf(baseName);
        OwnedDirectory.under(generatedBase(baseName), MARKER).cleanupAbandoned((candidate, properties) -> {
            Marker marker = parseMarker(candidate, properties, kind);
            return (includeCurrentProcess && marker.pid() == ProcessHandle.current().pid())
                    || isInactive(marker.pid());
        });
        ownedDirectories.removeIf(path -> !Files.exists(path, LinkOption.NOFOLLOW_LINKS));
    }

    private void deleteMarkedTree(Path root) throws IOException {
        Marker marker = readMarker(root);
        owned(marker.kind()).deleteTree(root);
        ownedDirectories.remove(root);
    }

    private Marker readMarker(Path root) throws IOException {
        Path parent = root.toAbsolutePath().normalize().getParent();
        String baseName;
        if (generatedBase("harnesses").equals(parent)) {
            baseName = "harnesses";
        } else if (generatedBase("module-output").equals(parent)) {
            baseName = "module-output";
        } else {
            throw new IOException("Refusing to modify an unmarked generated directory.");
        }
        OwnedDirectory owned = OwnedDirectory.under(generatedBase(baseName), MARKER);
        return parseMarker(root, owned.readMarker(root), kindOf(baseName));
    }

    private static Marker parseMarker(Path root, Properties properties, String expectedKind) throws IOException {
        String kind = properties.getProperty("kind", "");
        String attemptId = properties.getProperty("attempt", "");
        String pidText = properties.getProperty("pid", "");
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

    private OwnedDirectory owned(String kind) throws IOException {
        return OwnedDirectory.under(generatedBase(switch (kind) {
            case "harness" -> "harnesses";
            case "output" -> "module-output";
            default -> throw new IOException("Unknown generated-directory kind.");
        }), MARKER);
    }

    private static String kindOf(String baseName) {
        return "harnesses".equals(baseName) ? "harness" : "output";
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
