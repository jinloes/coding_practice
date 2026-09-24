package com.jinloes.practice_plugin.catalog;

import org.junit.platform.launcher.Launcher;
import org.junit.platform.launcher.LauncherDiscoveryRequest;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;
import org.junit.platform.launcher.listeners.SummaryGeneratingListener;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.platform.engine.discovery.DiscoverySelectors.selectClass;

/**
 * Compiles exercise sources with {@code --release 17} into throwaway directories under
 * {@code build/} and runs them: the JUnit suites in-process, the example runner and complexity
 * probe as child JVMs.
 */
final class CatalogSandbox {
    private CatalogSandbox() {
    }

    static String launchRunner(String classpath, String... arguments) throws Exception {
        List<String> command = new ArrayList<>(List.of(
                Path.of(System.getProperty("java.home"), "bin",
                        System.getProperty("os.name").startsWith("Windows") ? "java.exe" : "java").toString(),
                "-classpath", classpath,
                "com.jinloes.practice.ExampleRunner"));
        command.addAll(List.of(arguments));
        Process process = new ProcessBuilder(command).redirectErrorStream(true).start();
        try {
            assertThat(process.waitFor(30, java.util.concurrent.TimeUnit.SECONDS))
                    .as("example runner finished for arguments %s", List.of(arguments)).isTrue();
            String output = new String(process.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
            assertThat(process.exitValue())
                    .as("example runner exit code for arguments %s: %s", List.of(arguments), output)
                    .isZero();
            return output;
        } finally {
            if (process.isAlive()) {
                process.destroyForcibly();
                process.waitFor(10, java.util.concurrent.TimeUnit.SECONDS);
            }
        }
    }

    static RunSummary compileAndRun(
            ExerciseCatalog.Exercise exercise,
            String solution,
            String label
    ) throws Exception {
        Files.createDirectories(Path.of("build"));
        Path root = Files.createTempDirectory(Path.of("build"),
                "catalog-" + exercise.id() + "-" + label + "-");
        try {
            Path sourceRoot = root.resolve("src/com/jinloes/practice");
            Path classes = root.resolve("classes");
            Files.createDirectories(sourceRoot);
            Files.createDirectories(classes);
            Files.writeString(sourceRoot.resolve("Solution.java"), solution, StandardCharsets.UTF_8);
            Files.writeString(
                    sourceRoot.resolve("ExamplesTest.java"),
                    ExerciseCatalog.resource(exercise, "ExamplesTest.java"),
                    StandardCharsets.UTF_8);
            Files.writeString(
                    sourceRoot.resolve("CorrectnessTest.java"),
                    ExerciseCatalog.resource(exercise, "CorrectnessTest.java"),
                    StandardCharsets.UTF_8);
            compile(List.of(
                    sourceRoot.resolve("Solution.java"),
                    sourceRoot.resolve("ExamplesTest.java"),
                    sourceRoot.resolve("CorrectnessTest.java")), classes, exercise.id() + " " + label);
            return execute(classes);
        } finally {
            deleteTree(root);
        }
    }

    static void launchProbe(String classpath, String results) throws Exception {
        Process process = new ProcessBuilder(
                Path.of(System.getProperty("java.home"), "bin",
                        System.getProperty("os.name").startsWith("Windows") ? "java.exe" : "java").toString(),
                "-classpath", classpath,
                "com.jinloes.practice.ComplexityProbe", results)
                .redirectErrorStream(true).start();
        try {
            assertThat(process.waitFor(90, java.util.concurrent.TimeUnit.SECONDS))
                    .as("complexity probe finished").isTrue();
            String output = new String(process.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
            assertThat(process.exitValue()).as("complexity probe exit code: %s", output).isZero();
        } finally {
            if (process.isAlive()) {
                process.destroyForcibly();
                process.waitFor(10, java.util.concurrent.TimeUnit.SECONDS);
            }
        }
    }

    static void compile(List<Path> sources, Path classes, String description) throws Exception {
        String classpath = System.getProperty(
                "practice.test.classpath",
                System.getProperty("java.class.path"));
        List<String> arguments = new ArrayList<>(List.of(
                Path.of(System.getProperty("java.home"), "bin",
                        System.getProperty("os.name").startsWith("Windows") ? "java.exe" : "java").toString(),
                "--module", "jdk.compiler/com.sun.tools.javac.Main",
                "--release", "17",
                "-encoding", "UTF-8",
                "-classpath", classpath,
                "-d", classes.toString()));
        arguments.addAll(sources.stream().map(Path::toString).toList());
        Path diagnostics = classes.getParent().resolve("compiler.log");
        Process process = new ProcessBuilder(arguments).redirectErrorStream(true)
                .redirectOutput(diagnostics.toFile()).start();
        try {
            assertThat(process.waitFor(30, java.util.concurrent.TimeUnit.SECONDS))
                    .as("compiler finished for %s", description).isTrue();
            assertThat(process.exitValue())
                    .as("compile %s: %s", description, Files.readString(diagnostics))
                    .isZero();
        } finally {
            if (process.isAlive()) {
                process.destroyForcibly();
                process.waitFor(10, java.util.concurrent.TimeUnit.SECONDS);
            }
        }
    }

    private static RunSummary execute(Path classes) throws Exception {
        URL[] classpath = {classes.toUri().toURL()};
        ClassLoader previous = Thread.currentThread().getContextClassLoader();
        try (URLClassLoader loader = new URLClassLoader(classpath, previous)) {
            Thread.currentThread().setContextClassLoader(loader);
            LauncherDiscoveryRequest request = LauncherDiscoveryRequestBuilder.request()
                    .selectors(selectClass("com.jinloes.practice.ExamplesTest"),
                            selectClass("com.jinloes.practice.CorrectnessTest"))
                    .build();
            Launcher launcher = LauncherFactory.create();
            SummaryGeneratingListener listener = new SummaryGeneratingListener();
            launcher.registerTestExecutionListeners(listener);
            launcher.execute(request);
            var summary = listener.getSummary();
            String details = summary.getFailures().stream()
                    .map(failure -> failure.getTestIdentifier().getDisplayName()
                            + ": " + failure.getException().getMessage())
                    .collect(Collectors.joining("; "));
            return new RunSummary(summary.getTestsFoundCount(), summary.getTestsFailedCount(), details);
        } finally {
            Thread.currentThread().setContextClassLoader(previous);
        }
    }

    static void deleteTree(Path root) throws IOException {
        if (!Files.exists(root)) {
            return;
        }
        try (Stream<Path> paths = Files.walk(root)) {
            for (Path path : paths.sorted(Comparator.reverseOrder()).toList()) {
                Files.deleteIfExists(path);
            }
        }
    }

    record RunSummary(long testsFound, long failures, String failureDetails) {
    }}
