package com.jinloes.practice_plugin.workspace;

import com.intellij.openapi.application.PathManager;
import com.jinloes.practice_plugin.catalog.ExerciseCatalog;
import com.jinloes.practice_plugin.platform.Os;
import com.jinloes.practice_plugin.platform.OwnedDirectory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Properties;
import java.util.UUID;
import java.util.regex.Pattern;

public final class VerificationWorkspace {
    private static final String MARKER = ".algorithm-practice-verification";
    private static final String SCHEMA = "1";
    private static final String BUILD_SCRIPT = "/gradle/verification-build.gradle";
    private static final Pattern PLACEHOLDER = Pattern.compile("@[A-Z_]+@");
    private static final List<String> BOOTSTRAP = List.of(
            "gradlew", "gradlew.bat", "gradle/wrapper/gradle-wrapper.jar",
            "gradle/wrapper/gradle-wrapper.properties");

    private final Path base;
    private final Path root;
    private final OwnedDirectory owned;
    private final Path resultDirectory;

    public static VerificationWorkspace create(
            ManagedPracticeWorkspace.Attempt attempt,
            ExerciseCatalog.Exercise exercise,
            String fingerprint
    ) throws IOException {
        return createAt(Path.of(PathManager.getSystemPath()), attempt, exercise, fingerprint);
    }

    static VerificationWorkspace createAt(
            Path base,
            ManagedPracticeWorkspace.Attempt attempt,
            ExerciseCatalog.Exercise exercise,
            String fingerprint
    ) throws IOException {
        Path normalizedBase = base.toAbsolutePath().normalize();
        OwnedDirectory owned = owned(normalizedBase);
        Path root = owned.resolve(UUID.randomUUID().toString());
        owned.create(root, markerText("created", -1, fingerprint));
        VerificationWorkspace workspace = new VerificationWorkspace(normalizedBase, root);
        try {
            workspace.writeProject(attempt, exercise);
            return workspace;
        } catch (IOException | RuntimeException failure) {
            workspace.deleteOwnedTree();
            throw failure;
        }
    }

    private VerificationWorkspace(Path base, Path root) {
        this.base = base;
        this.root = root;
        this.owned = owned(base);
        this.resultDirectory = root.resolve(".practice-results");
    }

    public Path root() {
        return root;
    }

    public Path resultDirectory() {
        return resultDirectory;
    }

    public Path gradleHome() {
        return root.resolve("gradle-home");
    }

    public List<String> command(Path javaHome, int testSeconds, int heapMb) {
        String java = javaHome.resolve("bin").resolve(Os.IS_WINDOWS ? "java.exe" : "java").toString();
        return List.of(
                java,
                "-classpath", root.resolve("gradle/wrapper/gradle-wrapper.jar").toString(),
                "org.gradle.wrapper.GradleWrapperMain",
                "test",
                "probe",
                "--no-daemon",
                "--console=plain",
                "--rerun-tasks",
                "--no-build-cache",
                "--no-configuration-cache",
                "--gradle-user-home", gradleHome().toString(),
                "-Dorg.gradle.java.home=" + javaHome,
                "-PpracticeTestSeconds=" + testSeconds,
                "-PpracticeHeapMb=" + heapMb);
    }

    public void markProcess(long pid, String fingerprint) throws IOException {
        writeMarker("active", pid, fingerprint);
    }

    public void markFinished(String fingerprint) throws IOException {
        writeMarker("finished", -1, fingerprint);
    }

    public void cleanupAfterRun() throws IOException {
        Marker marker = marker(root);
        if (!"finished".equals(marker.state())) {
            throw new IOException("Verification workspace is still active or its process state is uncertain.");
        }
        deleteOwnedTree();
    }

    public static void cleanupAbandoned() {
        Path base = Path.of(PathManager.getSystemPath());
        cleanupAbandoned(base);
    }

    static void cleanupAbandoned(Path base) {
        owned(base.toAbsolutePath().normalize()).cleanupAbandoned((directory, properties) -> {
            Marker marker = parse(properties);
            return "finished".equals(marker.state())
                    || ("active".equals(marker.state()) && !isAlive(marker.pid()));
        });
    }

    private void writeProject(ManagedPracticeWorkspace.Attempt attempt, ExerciseCatalog.Exercise exercise)
            throws IOException {
        Path source = root.resolve(ManagedPracticeWorkspace.SOLUTION_PATH);
        Path tests = root.resolve("src/test/java/com/jinloes/practice");
        Files.createDirectories(source.getParent());
        Files.createDirectories(tests);
        Files.copy(attempt.solution(), source);
        Files.writeString(source.resolveSibling("Workload.java"),
                ExerciseCatalog.resource(exercise, "Workload.java"), StandardCharsets.UTF_8);
        Files.writeString(source.resolveSibling("ComplexityProbe.java"),
                ExerciseCatalog.harness("ComplexityProbe.java"), StandardCharsets.UTF_8);
        Files.writeString(tests.resolve("ExamplesTest.java"),
                ExerciseCatalog.resource(exercise, "ExamplesTest.java"), StandardCharsets.UTF_8);
        Files.writeString(tests.resolve("CorrectnessTest.java"),
                ExerciseCatalog.resource(exercise, "CorrectnessTest.java"), StandardCharsets.UTF_8);
        Files.writeString(root.resolve("settings.gradle"), "rootProject.name = 'algorithm-practice-verification'\n",
                StandardCharsets.UTF_8);
        Files.writeString(root.resolve("build.gradle"), buildScript(), StandardCharsets.UTF_8);
        for (String name : BOOTSTRAP) {
            Path destination = root.resolve(name);
            Files.createDirectories(destination.getParent());
            try (InputStream input = VerificationWorkspace.class.getResourceAsStream("/bootstrap/" + name)) {
                if (input == null) {
                    throw new IOException("Plugin is missing Gradle bootstrap resource: " + name);
                }
                Files.copy(input, destination);
            }
        }
        if (!root.resolve("gradlew").toFile().setExecutable(true) && !Os.IS_WINDOWS) {
            throw new IOException("Could not make Gradle wrapper executable.");
        }
    }

    private void writeMarker(String state, long pid, String fingerprint) throws IOException {
        marker(root);
        Files.writeString(root.resolve(MARKER), markerText(state, pid, fingerprint), StandardCharsets.UTF_8,
                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }

    private static String markerText(String state, long pid, String fingerprint) {
        return """
                schema=%s
                state=%s
                pid=%s
                fingerprint=%s
                """.formatted(SCHEMA, state, pid, fingerprint);
    }

    private void deleteOwnedTree() throws IOException {
        marker(root);
        owned.deleteTree(root);
    }

    private Marker marker(Path directory) throws IOException {
        return parse(owned.readMarker(directory));
    }

    private static Marker parse(Properties properties) throws IOException {
        if (!SCHEMA.equals(properties.getProperty("schema"))) {
            throw new IOException("Unsupported verification workspace marker.");
        }
        String state = properties.getProperty("state", "");
        long pid;
        try {
            pid = Long.parseLong(properties.getProperty("pid", ""));
        } catch (NumberFormatException exception) {
            throw new IOException("Invalid verification workspace process ID.", exception);
        }
        if (!List.of("created", "active", "finished").contains(state)
                || properties.getProperty("fingerprint", "").isBlank()) {
            throw new IOException("Invalid verification workspace marker.");
        }
        return new Marker(state, pid);
    }

    private static OwnedDirectory owned(Path base) {
        return OwnedDirectory.under(verificationDirectory(base), MARKER);
    }

    private static Path verificationDirectory(Path base) {
        return base.resolve("algorithm-practice/verification").normalize();
    }

    private static boolean isAlive(long pid) {
        return pid > 0 && ProcessHandle.of(pid).map(ProcessHandle::isAlive).orElse(false);
    }

    private record Marker(String state, long pid) {
    }

    private static String buildScript() throws IOException {
        try (InputStream input = VerificationWorkspace.class.getResourceAsStream(BUILD_SCRIPT)) {
            if (input == null) {
                throw new IOException("Plugin is missing Gradle verification script resource: " + BUILD_SCRIPT);
            }
            String script = new String(input.readAllBytes(), StandardCharsets.UTF_8);
            if (PLACEHOLDER.matcher(script).find()) {
                throw new IOException("Verification build script was packaged with unreplaced placeholders.");
            }
            return script;
        }
    }
}
