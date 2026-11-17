package com.jinloes.practice_plugin.runner;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * Coordinates an isolated full check inside a verification workspace: compiles with
 * {@code javac --release 17}, executes the JUnit suite in a heap-bounded child JVM, and runs the
 * advisory complexity probe in a separate child JVM when all tests pass.
 */
public final class VerificationMain {
    static final String RESULTS_DIR = ".practice-results";
    private static final boolean IS_WINDOWS = System.getProperty("os.name", "").startsWith("Windows");

    private VerificationMain() {
    }

    public static void main(String[] args) throws Exception {
        System.exit(run(Path.of(".").toAbsolutePath().normalize(), args));
    }

    static int run(Path workspace, String... args) throws IOException, InterruptedException {
        if (args.length != 3) {
            throw new IllegalArgumentException("Expected <javaHome> <testSeconds> <heapMb>");
        }
        Path javaHome = Path.of(args[0]);
        int testSeconds = Integer.parseInt(args[1]);
        int heapMb = Integer.parseInt(args[2]);

        Path results = workspace.resolve(RESULTS_DIR);
        Files.createDirectories(results);
        String libsClasspath = jarsClasspath(workspace.resolve("libs"));
        Path classes = workspace.resolve("classes");
        Files.createDirectories(classes);

        writePhase(results, "compiling");
        int compileExit = launch(workspace, compileCommand(javaHome, libsClasspath, workspace, classes));
        if (compileExit != 0) {
            return compileExit;
        }

        writePhase(results, "testing");
        Files.writeString(results.resolve("started"),
                Long.toString(System.currentTimeMillis()), StandardCharsets.UTF_8);
        int testExit = launch(workspace, testCommand(javaHome, libsClasspath, classes, results, testSeconds, heapMb));
        if (testExit != 0) {
            return testExit;
        }

        writePhase(results, "probing");
        launch(workspace, probeCommand(javaHome, classes, results, heapMb));
        return 0;
    }

    static List<String> compileCommand(Path javaHome, String libsClasspath, Path workspace, Path classes) {
        Path mainPkg = workspace.resolve("src/main/java/com/jinloes/practice");
        Path testPkg = workspace.resolve("src/test/java/com/jinloes/practice");
        return List.of(
                executable(javaHome, "javac"),
                "--release", "17",
                "-encoding", "UTF-8",
                "-g",
                "-classpath", libsClasspath,
                "-d", classes.toString(),
                mainPkg.resolve("Solution.java").toString(),
                mainPkg.resolve("Workload.java").toString(),
                mainPkg.resolve("ComplexityProbe.java").toString(),
                testPkg.resolve("ExamplesTest.java").toString(),
                testPkg.resolve("CorrectnessTest.java").toString());
    }

    static List<String> testCommand(
            Path javaHome,
            String libsClasspath,
            Path classes,
            Path results,
            int testSeconds,
            int heapMb
    ) {
        return List.of(
                executable(javaHome, "java"),
                "-Xmx" + heapMb + "m",
                "-Djunit.jupiter.execution.timeout.default=" + testSeconds + "s",
                "-Djunit.jupiter.execution.timeout.thread.mode.default=separate_thread",
                "-classpath", classes + File.pathSeparator + libsClasspath,
                JUnitSuiteRunner.class.getName(),
                results.toString());
    }

    static List<String> probeCommand(Path javaHome, Path classes, Path results, int heapMb) {
        return List.of(
                executable(javaHome, "java"),
                "-Xmx" + heapMb + "m",
                "-classpath", classes.toString(),
                "com.jinloes.practice.ComplexityProbe",
                results.toString());
    }

    private static String jarsClasspath(Path libs) throws IOException {
        List<String> entries = new ArrayList<>();
        try (Stream<Path> files = Files.list(libs)) {
            files.filter(path -> path.getFileName().toString().endsWith(".jar"))
                    .sorted()
                    .map(Path::toString)
                    .forEach(entries::add);
        }
        return String.join(File.pathSeparator, entries);
    }

    private static int launch(Path workspace, List<String> command) throws IOException, InterruptedException {
        Process process = new ProcessBuilder(command)
                .directory(workspace.toFile())
                .inheritIO()
                .start();
        try {
            return process.waitFor();
        } finally {
            if (process.isAlive()) {
                process.destroyForcibly();
            }
        }
    }

    private static void writePhase(Path results, String phase) throws IOException {
        Files.writeString(results.resolve("phase"), phase, StandardCharsets.UTF_8);
    }

    private static String executable(Path javaHome, String tool) {
        return javaHome.resolve("bin").resolve(IS_WINDOWS ? tool + ".exe" : tool).toString();
    }
}
