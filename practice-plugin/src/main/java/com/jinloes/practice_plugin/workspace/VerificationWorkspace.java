package com.jinloes.practice_plugin.workspace;

import com.intellij.openapi.application.PathManager;
import com.jinloes.practice_plugin.catalog.ExampleLibraries;
import com.jinloes.practice_plugin.catalog.ExerciseCatalog;
import com.jinloes.plugin_common.platform.OwnedDirectory;
import com.jinloes.practice_plugin.platform.Os;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

public final class VerificationWorkspace {
    private static final String MARKER = ".algorithm-practice-verification";
    private static final String SCHEMA = "1";
    private static final String RUNNER_MAIN = "com.jinloes.practice_plugin.runner.VerificationMain";

    private final Path root;
    private final OwnedDirectory owned;
    private final Path resultDirectory;
    private final List<Path> libraries;

    @FunctionalInterface
    interface ResourceOpener {
        InputStream open(String path);
    }

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
        return createAt(base, attempt, exercise, fingerprint, VerificationWorkspace.class::getResourceAsStream);
    }

    static VerificationWorkspace createAt(
            Path base,
            ManagedPracticeWorkspace.Attempt attempt,
            ExerciseCatalog.Exercise exercise,
            String fingerprint,
            ResourceOpener opener
    ) throws IOException {
        Path normalizedBase = base.toAbsolutePath().normalize();
        OwnedDirectory owned = owned(normalizedBase);
        Path root = owned.resolve(UUID.randomUUID().toString());
        owned.create(root, markerText("created", -1, fingerprint));
        try {
            List<Path> libraries = writeProject(root, attempt, exercise, opener);
            return new VerificationWorkspace(normalizedBase, root, libraries);
        } catch (IOException | RuntimeException failure) {
            deleteOwnedTree(owned, root);
            throw failure;
        }
    }

    private VerificationWorkspace(Path base, Path root, List<Path> libraries) {
        this.root = root;
        this.owned = owned(base);
        this.resultDirectory = root.resolve(".practice-results");
        this.libraries = libraries;
    }

    public Path root() {
        return root;
    }

    public Path resultDirectory() {
        return resultDirectory;
    }

    public List<String> command(Path javaHome, int testSeconds, int heapMb) {
        String java = javaHome.resolve("bin").resolve(Os.executable("java")).toString();
        return List.of(
                java,
                "-Xmx128m",
                "-classpath", ExampleLibraries.classpath(libraries),
                RUNNER_MAIN,
                javaHome.toString(),
                Integer.toString(testSeconds),
                Integer.toString(heapMb));
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
        deleteOwnedTree(owned, root);
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

    private static List<Path> writeProject(
            Path root,
            ManagedPracticeWorkspace.Attempt attempt,
            ExerciseCatalog.Exercise exercise,
            ResourceOpener opener)
            throws IOException {
        Path source = root.resolve(ManagedPracticeWorkspace.SOLUTION_PATH);
        Path tests = root.resolve("src/test/java/com/jinloes/practice");
        Files.createDirectories(source.getParent());
        Files.createDirectories(tests);
        Files.copy(attempt.solution(), source);
        Files.writeString(source.resolveSibling("Workload.java"),
                readResource(opener, "/exercises/" + exercise.id() + "/Workload.java"), StandardCharsets.UTF_8);
        Files.writeString(source.resolveSibling("ComplexityProbe.java"),
                readResource(opener, "/harness/ComplexityProbe.java"), StandardCharsets.UTF_8);
        Files.writeString(tests.resolve("ExamplesTest.java"),
                readResource(opener, "/exercises/" + exercise.id() + "/ExamplesTest.java"), StandardCharsets.UTF_8);
        Files.writeString(tests.resolve("CorrectnessTest.java"),
                readResource(opener, "/exercises/" + exercise.id() + "/CorrectnessTest.java"), StandardCharsets.UTF_8);
        return ExampleLibraries.extractVerificationTo(root.resolve("libs"), opener::open);
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

    private static void deleteOwnedTree(OwnedDirectory owned, Path root) throws IOException {
        parse(owned.readMarker(root));
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

    private static String readResource(ResourceOpener opener, String path) throws IOException {
        try (InputStream input = opener.open(path)) {
            if (input == null) {
                throw new IOException("Plugin is missing verification resource: " + path);
            }
            return new String(input.readAllBytes(), StandardCharsets.UTF_8);
        }
    }
}
