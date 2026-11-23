package com.jinloes.practice_plugin.runner;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AccessDeniedException;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;
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
        writeAtomic(results.resolve("started"), Long.toString(System.currentTimeMillis()));
        int testExit = launch(workspace, testCommand(javaHome, libsClasspath, classes, results, testSeconds, heapMb));
        if (testExit != 0) {
            return testExit;
        }

        writePhase(results, "probing");
        launchProbe(workspace, probeCommand(javaHome, classes, results, heapMb), results);
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

    private static void launchProbe(Path workspace, List<String> command, Path results)
            throws IOException, InterruptedException {
        Process process = new ProcessBuilder(command).directory(workspace.toFile()).inheritIO().start();
        var descendants = new java.util.LinkedHashMap<Long, ProcessHandle>();
        long deadline = System.nanoTime() + java.util.concurrent.TimeUnit.SECONDS.toNanos(10);
        boolean finished = false;
        try {
            while (System.nanoTime() < deadline) {
                process.descendants().forEach(child -> descendants.put(child.pid(), child));
                long remaining = Math.max(1, deadline - System.nanoTime());
                if (process.waitFor(Math.min(remaining, java.util.concurrent.TimeUnit.MILLISECONDS.toNanos(100)),
                        java.util.concurrent.TimeUnit.NANOSECONDS)) {
                    finished = true;
                    break;
                }
            }
        } finally {
            process.descendants().forEach(child -> descendants.put(child.pid(), child));
            process.destroyForcibly();
            process.descendants().forEach(child -> descendants.put(child.pid(), child));
            descendants.values().forEach(child -> {
                child.descendants().forEach(ProcessHandle::destroyForcibly);
                child.destroyForcibly();
            });
            long cleanupDeadline = System.nanoTime() + java.util.concurrent.TimeUnit.SECONDS.toNanos(5);
            if (!process.waitFor(5, java.util.concurrent.TimeUnit.SECONDS)) {
                throw new IOException("Advisory probe process did not exit.");
            }
            for (ProcessHandle child : descendants.values()) {
                if (child.isAlive()) {
                    try {
                        child.onExit().get(Math.max(1, cleanupDeadline - System.nanoTime()),
                                java.util.concurrent.TimeUnit.NANOSECONDS);
                    } catch (java.util.concurrent.ExecutionException | java.util.concurrent.TimeoutException exception) {
                        throw new IOException("Advisory probe descendant did not exit.", exception);
                    }
                }
            }
        }
        if (!finished) {
            writeAtomic(results.resolve("complexity.txt"),
                    "schema=1\nstatus=unavailable\nmessage=Scaling measurement unavailable: advisory probe exceeded 10 seconds.\n");
        }
    }

    @FunctionalInterface
    interface StagedMove {
        void move(Path staged, Path target) throws IOException;
    }

    static void writePhase(Path results, String phase) throws IOException {
        writePhase(results, phase, VerificationMain::moveAtomically);
    }

    static void writePhase(Path results, String phase, StagedMove move) throws IOException {
        writeAtomic(results.resolve("phase"), phase, move);
    }

    private static void writeAtomic(Path target, String content) throws IOException {
        writeAtomic(target, content, VerificationMain::moveAtomically);
    }

    private static void writeAtomic(Path target, String content, StagedMove move) throws IOException {
        Path temporary = Files.createTempFile(target.getParent(), "." + target.getFileName() + "-", ".tmp");
        try {
            Files.writeString(temporary, content, StandardCharsets.UTF_8);
            move.move(temporary, target);
        } finally {
            Files.deleteIfExists(temporary);
        }
    }

    private static void moveAtomically(Path staged, Path target) throws IOException {
        boolean atomic = true;
        for (int attempt = 0; ; attempt++) {
            try {
                if (atomic) {
                    Files.move(staged, target, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
                } else {
                    Files.move(staged, target, StandardCopyOption.REPLACE_EXISTING);
                }
                return;
            } catch (AtomicMoveNotSupportedException ignored) {
                atomic = false;
            } catch (java.nio.file.NoSuchFileException missing) {
                throw missing;
            } catch (java.nio.file.FileSystemException contention) {
                if (!IS_WINDOWS || attempt >= 4) {
                    throw contention;
                }
                LockSupport.parkNanos(TimeUnit.MILLISECONDS.toNanos(5));
            }
        }
    }

    private static String executable(Path javaHome, String tool) {
        return javaHome.resolve("bin").resolve(IS_WINDOWS ? tool + ".exe" : tool).toString();
    }
}
