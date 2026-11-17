package com.jinloes.practice_plugin.runner;

import com.jinloes.practice_plugin.catalog.ExerciseCatalog;
import com.jinloes.practice_plugin.run.CheckResult;
import com.jinloes.practice_plugin.run.TestReports;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class VerificationRunnerTest {
    @TempDir
    Path tempDir;

    @Test
    void rejectsWrongArgumentCountAndBuildsIsolatedJvmCommands() {
        assertThatThrownBy(() -> VerificationMain.run(tempDir, "only-one"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Expected <javaHome> <testSeconds> <heapMb>");

        Path javaHome = Path.of(System.getProperty("java.home"));
        Path classes = tempDir.resolve("classes");
        Path results = tempDir.resolve(".practice-results");

        assertThat(VerificationMain.compileCommand(javaHome, "a.jar", tempDir, classes))
                .contains("--release", "17", "-encoding", "UTF-8", "-classpath", "a.jar", "-d", classes.toString());
        assertThat(VerificationMain.testCommand(javaHome, "a.jar", classes, results, 5, 256))
                .contains("-Xmx256m", "-Djunit.jupiter.execution.timeout.default=5s",
                        JUnitSuiteRunner.class.getName(), results.toString());
        assertThat(VerificationMain.probeCommand(javaHome, classes, results, 256))
                .contains("-Xmx256m", "-classpath", classes.toString(),
                        "com.jinloes.practice.ComplexityProbe", results.toString());
    }

    @Test
    void suiteRunnerWritesValidJUnitXmlForPassedFailedSkippedAndAbortedTests() throws Exception {
        Path src = tempDir.resolve("sample-src/sample");
        Path classes = tempDir.resolve("sample-classes");
        Files.createDirectories(src);
        Files.createDirectories(classes);
        Files.writeString(src.resolve("PassingSuite.java"), """
                package sample;
                import org.junit.jupiter.api.Test;
                import static org.assertj.core.api.Assertions.assertThat;
                public class PassingSuite {
                    @Test void passes() { assertThat(1 + 1).isEqualTo(2); }
                }
                """, StandardCharsets.UTF_8);
        Files.writeString(src.resolve("MixedOutcomeSuite.java"), """
                package sample;
                import org.junit.jupiter.api.Assumptions;
                import org.junit.jupiter.api.Disabled;
                import org.junit.jupiter.api.Test;
                import static org.assertj.core.api.Assertions.assertThat;
                public class MixedOutcomeSuite {
                    @Test void failsAssertion() { assertThat("actual").isEqualTo("expected <escaped>"); }
                    @Test void throwsRuntimeError() { throw new IllegalStateException("boom & crash"); }
                    @Test void abortsViaAssumption() { Assumptions.assumeTrue(false, "aborted"); }
                    @Test @Disabled("intentionally skipped") void disabledCase() {}
                }
                """, StandardCharsets.UTF_8);
        String java = Path.of(System.getProperty("java.home"), "bin",
                System.getProperty("os.name").startsWith("Windows") ? "java.exe" : "java").toString();
        Process compile = new ProcessBuilder(
                java,
                "--module", "jdk.compiler/com.sun.tools.javac.Main",
                "--release", "17",
                "-encoding", "UTF-8",
                "-Xlint:-classfile",
                "-classpath", System.getProperty("practice.test.classpath", System.getProperty("java.class.path")),
                "-d", classes.toString(),
                src.resolve("PassingSuite.java").toString(),
                src.resolve("MixedOutcomeSuite.java").toString())
                .redirectErrorStream(true)
                .start();
        String compilerOutput = new String(compile.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        assertThat(compile.waitFor()).as(compilerOutput).isZero();

        Path results = tempDir.resolve("reports");
        ClassLoader previous = Thread.currentThread().getContextClassLoader();
        int exit;
        try (var loader = new java.net.URLClassLoader(new java.net.URL[]{classes.toUri().toURL()},
                VerificationRunnerTest.class.getClassLoader())) {
            Thread.currentThread().setContextClassLoader(loader);
            exit = JUnitSuiteRunner.executeSuites(results, List.of(
                    "sample.PassingSuite",
                    "sample.MixedOutcomeSuite"));
        } finally {
            Thread.currentThread().setContextClassLoader(previous);
        }

        assertThat(exit).isEqualTo(1);
        String passingXml = Files.readString(results.resolve("TEST-sample.PassingSuite.xml"), StandardCharsets.UTF_8);
        String mixedXml = Files.readString(results.resolve("TEST-sample.MixedOutcomeSuite.xml"), StandardCharsets.UTF_8);

        assertThat(passingXml)
                .contains("<testsuite name=\"sample.PassingSuite\"")
                .contains("<testcase name=\"passes\"");
        assertThat(mixedXml)
                .contains("<failure")
                .contains("<error")
                .contains("<skipped");
    }

    @Test
    void verificationMainRunsEndToEndOfflineOnAReferenceWorkspace() throws Exception {
        var exercise = ExerciseCatalog.find("pair-sum");
        Path solution = tempDir.resolve("attempt/src/main/java/com/jinloes/practice/Solution.java");
        Files.createDirectories(solution.getParent());
        try (var reference = VerificationRunnerTest.class.getResourceAsStream("/reference/pair-sum/Solution.java")) {
            assertThat(reference).isNotNull();
            Files.writeString(solution, new String(reference.readAllBytes(), StandardCharsets.UTF_8),
                    StandardCharsets.UTF_8);
        }
        var attempt = new com.jinloes.practice_plugin.workspace.ManagedPracticeWorkspace.Attempt(
                "pair-sum-0123456789abcdef0123456789abcdef", "pair-sum", "1", solution);
        var fixture = com.intellij.testFramework.EdtTestUtil.runInEdtAndGet(() -> {
            var created = com.intellij.testFramework.fixtures.IdeaTestFixtureFactory.getFixtureFactory()
                    .createLightFixtureBuilder("verification-runner-test").getFixture();
            created.setUp();
            return created;
        });
        try {
            var workspace = com.jinloes.practice_plugin.workspace.VerificationWorkspace.create(
                    attempt, exercise, "fingerprint");
            try {
                int exit = VerificationMain.run(workspace.root(), System.getProperty("java.home"), "5", "256");
                assertThat(exit).isZero();
                CheckResult check = TestReports.read(workspace.resultDirectory(), exercise.fullCount(), exit);
                assertThat(check.status()).isEqualTo(CheckResult.Status.PASSED);
                assertThat(workspace.resultDirectory().resolve("complexity.txt")).isRegularFile();
            } finally {
                workspace.markFinished("fingerprint");
                workspace.cleanupAfterRun();
            }
        } finally {
            com.intellij.testFramework.EdtTestUtil.runInEdtAndWait(fixture::tearDown);
        }
    }
}
