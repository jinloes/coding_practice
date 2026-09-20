package com.jinloes.practice_plugin.workspace;

import com.intellij.openapi.application.PathManager;
import com.jinloes.practice_plugin.catalog.ExerciseCatalog;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Comparator;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

public final class VerificationWorkspace {
    private static final String MARKER = ".algorithm-practice-verification";
    private static final String SCHEMA = "1";
    private static final List<String> BOOTSTRAP = List.of(
            "gradlew", "gradlew.bat", "gradle/wrapper/gradle-wrapper.jar",
            "gradle/wrapper/gradle-wrapper.properties");

    private final Path base;
    private final Path root;
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
        Path parent = verificationDirectory(normalizedBase);
        Files.createDirectories(parent);
        Path root = parent.resolve(UUID.randomUUID().toString());
        Files.createDirectory(root);
        VerificationWorkspace workspace = new VerificationWorkspace(normalizedBase, root);
        workspace.writeInitialMarker(fingerprint);
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
        String java = javaHome.resolve("bin").resolve(isWindows() ? "java.exe" : "java").toString();
        return List.of(
                java,
                "-classpath", root.resolve("gradle/wrapper/gradle-wrapper.jar").toString(),
                "org.gradle.wrapper.GradleWrapperMain",
                "test",
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
        Path parent = verificationDirectory(base.toAbsolutePath().normalize());
        if (!Files.isDirectory(parent, LinkOption.NOFOLLOW_LINKS)) {
            return;
        }
        try (var paths = Files.list(parent)) {
            for (Path candidate : paths.toList()) {
                try {
                    Marker marker = marker(candidate);
                    if ("finished".equals(marker.state())
                            || ("active".equals(marker.state()) && !isAlive(marker.pid()))) {
                        new VerificationWorkspace(base.toAbsolutePath().normalize(), candidate).deleteOwnedTree();
                    }
                } catch (IOException ignored) {
                    // Unmarked, malformed, and uncertain artifacts are preserved for later recovery.
                }
            }
        } catch (IOException ignored) {
            // A later startup can retry recovery without risking unverified paths.
        }
    }

    private void writeProject(ManagedPracticeWorkspace.Attempt attempt, ExerciseCatalog.Exercise exercise)
            throws IOException {
        Path source = root.resolve("src/main/java/com/jinloes/practice/Solution.java");
        Path tests = root.resolve("src/test/java/com/jinloes/practice");
        Files.createDirectories(source.getParent());
        Files.createDirectories(tests);
        Files.copy(attempt.solution(), source);
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
        if (!root.resolve("gradlew").toFile().setExecutable(true)
                && !System.getProperty("os.name").startsWith("Windows")) {
            throw new IOException("Could not make Gradle wrapper executable.");
        }
    }

    private void writeMarker(String state, long pid, String fingerprint) throws IOException {
        requireOwnedRoot();
        writeMarkerFile(state, pid, fingerprint, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }

    private void writeInitialMarker(String fingerprint) throws IOException {
        if (!root.getParent().equals(verificationDirectory(base))
                || Files.exists(root.resolve(MARKER), LinkOption.NOFOLLOW_LINKS)) {
            throw new IOException("Refusing to create a verification marker outside its owned workspace.");
        }
        writeMarkerFile("created", -1, fingerprint, StandardOpenOption.CREATE_NEW);
    }

    private void writeMarkerFile(String state, long pid, String fingerprint, StandardOpenOption... options)
            throws IOException {
        Files.writeString(root.resolve(MARKER), """
                schema=%s
                state=%s
                pid=%s
                fingerprint=%s
                """.formatted(SCHEMA, state, pid, fingerprint), StandardCharsets.UTF_8,
                options);
    }

    private void deleteOwnedTree() throws IOException {
        requireOwnedRoot();
        try (var paths = Files.walk(root)) {
            for (Path path : paths.sorted(Comparator.reverseOrder()).toList()) {
                Files.delete(path);
            }
        }
    }

    private void requireOwnedRoot() throws IOException {
        Path parent = verificationDirectory(base).toRealPath();
        Path actual = root.toRealPath();
        if (!actual.startsWith(parent) || actual.equals(parent) || !Files.isRegularFile(root.resolve(MARKER),
                LinkOption.NOFOLLOW_LINKS)) {
            throw new IOException("Refusing to modify an unmarked verification workspace.");
        }
        marker(root);
    }

    private static Marker marker(Path root) throws IOException {
        if (!Files.isDirectory(root, LinkOption.NOFOLLOW_LINKS)
                || !Files.isRegularFile(root.resolve(MARKER), LinkOption.NOFOLLOW_LINKS)) {
            throw new IOException("Verification workspace is not marked.");
        }
        Properties properties = new Properties();
        try (var reader = Files.newBufferedReader(root.resolve(MARKER), StandardCharsets.UTF_8)) {
            properties.load(reader);
        }
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

    private static Path verificationDirectory(Path base) {
        return base.resolve("algorithm-practice/verification").normalize();
    }

    private static boolean isAlive(long pid) {
        return pid > 0 && ProcessHandle.of(pid).map(ProcessHandle::isAlive).orElse(false);
    }

    private static boolean isWindows() {
        return System.getProperty("os.name").startsWith("Windows");
    }

    private record Marker(String state, long pid) {
    }

    private static String buildScript() {
        return """
                plugins {
                    id 'java'
                }

                repositories {
                    maven { url = uri('https://maven-central.storage-download.googleapis.com/maven2/') }
                    mavenCentral()
                }

                dependencies {
                    testImplementation platform('org.junit:junit-bom:5.11.4')
                    testImplementation 'org.junit.jupiter:junit-jupiter'
                    testImplementation 'org.assertj:assertj-core:3.26.3'
                    testRuntimeOnly 'org.junit.platform:junit-platform-launcher'
                }

                tasks.withType(JavaCompile).configureEach {
                    options.release = 17
                    options.encoding = 'UTF-8'
                    doFirst {
                        file('.practice-results').mkdirs()
                        file('.practice-results/phase').text = 'compiling'
                    }
                }

                tasks.withType(Test).configureEach {
                    useJUnitPlatform()
                    maxParallelForks = 1
                    maxHeapSize = providers.gradleProperty('practiceHeapMb').orElse('256').get() + 'm'
                    systemProperty 'junit.jupiter.execution.timeout.default',
                            providers.gradleProperty('practiceTestSeconds').orElse('5').get() + 's'
                    systemProperty 'junit.jupiter.execution.timeout.thread.mode.default', 'separate_thread'
                    reports.junitXml.outputLocation = layout.projectDirectory.dir('.practice-results')
                    reports.junitXml.includeSystemOutLog = false
                    reports.junitXml.includeSystemErrLog = false
                    doFirst {
                        file('.practice-results').mkdirs()
                        file('.practice-results/phase').text = 'testing'
                        file('.practice-results/started').text = System.currentTimeMillis().toString()
                    }
                }
                """;
    }
}
