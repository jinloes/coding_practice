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
    void rejectsWrongArgumentCountAndBuildsIsolatedJvmCommands() throws Exception {
        assertThatThrownBy(() -> VerificationMain.run(tempDir, "only-one"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Expected <javaHome> <testSeconds> <heapMb>");

        Path javaHome = Path.of(System.getProperty("java.home"));
        Path classes = tempDir.resolve("classes");
        Path results = tempDir.resolve(".practice-results");
        Files.createDirectories(results);
        VerificationMain.writePhase(results, "testing");
        VerificationMain.writePhase(results, "probing", (staged, target) -> {
            assertThat(Files.readString(target, StandardCharsets.UTF_8)).isEqualTo("testing");
            assertThat(Files.readString(staged, StandardCharsets.UTF_8)).isEqualTo("probing");
            Files.move(staged, target, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
        });
        assertThat(Files.readString(results.resolve("phase"), StandardCharsets.UTF_8)).isEqualTo("probing");
        assertThatThrownBy(() -> VerificationMain.writePhase(results, "broken", (staged, target) -> {
            assertThat(Files.readString(target, StandardCharsets.UTF_8)).isEqualTo("probing");
            throw new java.io.IOException("simulated move failure");
        })).isInstanceOf(java.io.IOException.class).hasMessage("simulated move failure");
        assertThat(Files.readString(results.resolve("phase"), StandardCharsets.UTF_8)).isEqualTo("probing");
        try (var remaining = Files.list(results)) {
            assertThat(remaining.map(path -> path.getFileName().toString()).toList()).containsExactly("phase");
        }

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
    void suiteRunnerEscapesIllegalXmlControlCharactersInFailureMessagesAndStackTraces() throws Exception {
        Path src = tempDir.resolve("control-src/sample");
        Path classes = tempDir.resolve("control-classes");
        Files.createDirectories(src);
        Files.createDirectories(classes);
        Files.writeString(src.resolve("ControlCharSuite.java"), """
                package sample;
                import org.junit.jupiter.api.Test;
                import static org.assertj.core.api.Assertions.assertThat;
                public class ControlCharSuite {
                    @Test void failsWithNullCharacters() {
                        assertThat(new String(new char[3])).isEqualTo("abc");
                    }
                    @Test void throwsControlAndNonCharacterText() {
                        throw new IllegalStateException("bad:\\u0001:\\ufffe:\\ud800:ok:\\uD83D\\uDE00");
                    }
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
                src.resolve("ControlCharSuite.java").toString())
                .redirectErrorStream(true)
                .start();
        String compilerOutput = new String(compile.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        assertThat(compile.waitFor()).as(compilerOutput).isZero();

        Path results = tempDir.resolve("control-reports");
        ClassLoader previous = Thread.currentThread().getContextClassLoader();
        int exit;
        try (var loader = new java.net.URLClassLoader(new java.net.URL[]{classes.toUri().toURL()},
                VerificationRunnerTest.class.getClassLoader())) {
            Thread.currentThread().setContextClassLoader(loader);
            exit = JUnitSuiteRunner.executeSuites(results, List.of("sample.ControlCharSuite"));
        } finally {
            Thread.currentThread().setContextClassLoader(previous);
        }

        assertThat(exit).isEqualTo(1);
        Path report = results.resolve("TEST-sample.ControlCharSuite.xml");
        assertThat(report).isRegularFile();
        String xml = Files.readString(report, StandardCharsets.UTF_8);
        assertThat(xml)
                .contains("\\u0000\\u0000\\u0000")
                .contains("\\u0001")
                .contains("\\ufffe")
                .contains("\\ud800");
        assertThat(javax.xml.parsers.DocumentBuilderFactory.newInstance()
                .newDocumentBuilder().parse(report.toFile()).getDocumentElement().getAttribute("name"))
                .isEqualTo("sample.ControlCharSuite");
        assertThat(JUnitSuiteRunner.sanitizeXml10(null)).isEmpty();
        assertThat(JUnitSuiteRunner.sanitizeXml10("line\t1\r\nline 2 \uD83D\uDE00"))
                .isEqualTo("line\t1\r\nline 2 \uD83D\uDE00");
        assertThat(JUnitSuiteRunner.sanitizeXml10("\udc00\uffff")).isEqualTo("\\udc00\\uffff");

        Files.move(report, results.resolve("TEST-com.jinloes.practice.ExamplesTest.xml"));
        String pairedXml = xml.replace("name=\"sample.ControlCharSuite\"",
                "name=\"com.jinloes.practice.ExamplesTest\"");
        Files.writeString(results.resolve("TEST-com.jinloes.practice.ExamplesTest.xml"), pairedXml,
                StandardCharsets.UTF_8);
        Files.writeString(results.resolve("TEST-com.jinloes.practice.CorrectnessTest.xml"),
                "<testsuite name=\"com.jinloes.practice.CorrectnessTest\"><testcase name=\"passes\"/></testsuite>",
                StandardCharsets.UTF_8);
        CheckResult parsed = TestReports.read(results, 3, 1);
        assertThat(parsed.status()).isEqualTo(CheckResult.Status.RUNTIME_ERROR);
        assertThat(parsed.details()).contains("\\u0000\\u0000\\u0000", "\uD83D\uDE00");

        Files.writeString(src.resolve("AssertionNullSuite.java"), """
                package sample;
                import org.junit.jupiter.api.Test;
                import static org.assertj.core.api.Assertions.assertThat;
                public class AssertionNullSuite {
                    @Test void failsWithNullCharacters() {
                        assertThat(new String(new char[3])).isEqualTo("abc");
                    }
                }
                """, StandardCharsets.UTF_8);
        Process compileAssertion = new ProcessBuilder(
                java,
                "--module", "jdk.compiler/com.sun.tools.javac.Main",
                "--release", "17",
                "-encoding", "UTF-8",
                "-Xlint:-classfile",
                "-classpath", System.getProperty("practice.test.classpath", System.getProperty("java.class.path")),
                "-d", classes.toString(),
                src.resolve("AssertionNullSuite.java").toString())
                .redirectErrorStream(true)
                .start();
        String assertionCompilerOutput = new String(compileAssertion.getInputStream().readAllBytes(),
                StandardCharsets.UTF_8);
        assertThat(compileAssertion.waitFor()).as(assertionCompilerOutput).isZero();

        Path assertionResults = tempDir.resolve("assertion-control-reports");
        try (var loader = new java.net.URLClassLoader(new java.net.URL[]{classes.toUri().toURL()},
                VerificationRunnerTest.class.getClassLoader())) {
            Thread.currentThread().setContextClassLoader(loader);
            assertThat(JUnitSuiteRunner.executeSuites(assertionResults, List.of("sample.AssertionNullSuite")))
                    .isEqualTo(1);
        } finally {
            Thread.currentThread().setContextClassLoader(previous);
        }
        Path assertionXmlPath = assertionResults.resolve("TEST-sample.AssertionNullSuite.xml");
        String assertionXml = Files.readString(assertionXmlPath, StandardCharsets.UTF_8)
                .replace("name=\"sample.AssertionNullSuite\"", "name=\"com.jinloes.practice.ExamplesTest\"");
        Files.delete(assertionXmlPath);
        Files.writeString(assertionResults.resolve("TEST-com.jinloes.practice.ExamplesTest.xml"), assertionXml,
                StandardCharsets.UTF_8);
        Files.writeString(assertionResults.resolve("TEST-com.jinloes.practice.CorrectnessTest.xml"),
                "<testsuite name=\"com.jinloes.practice.CorrectnessTest\"><testcase name=\"passes\"/></testsuite>",
                StandardCharsets.UTF_8);
        CheckResult assertionResult = TestReports.read(assertionResults, 2, 1);
        assertThat(assertionResult.status()).isEqualTo(CheckResult.Status.ASSERTION_FAILED);
        assertThat(assertionResult.details()).contains("\\u0000\\u0000\\u0000");
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
                Files.delete(workspace.resultDirectory().resolve("complexity.txt"));
                Files.writeString(workspace.root().resolve("src/main/java/com/jinloes/practice/ComplexityProbe.java"), """
                        package com.jinloes.practice;
                        public class ComplexityProbe {
                            public static void main(String[] args) throws Exception {
                                if (args.length == 2) {
                                    new java.util.concurrent.CountDownLatch(1).await();
                                    return;
                                }
                                var javaExecutable = java.nio.file.Path.of(System.getProperty("java.home"), "bin",
                                    System.getProperty("os.name").startsWith("Windows") ? "java.exe" : "java");
                                Process child = new ProcessBuilder(javaExecutable.toString(), "-classpath",
                                    System.getProperty("java.class.path"), ComplexityProbe.class.getName(),
                                    args[0], "child").start();
                                var results = java.nio.file.Path.of(args[0]);
                                java.nio.file.Files.writeString(results.resolve("probe-root-pid"),
                                    Long.toString(ProcessHandle.current().pid()));
                                java.nio.file.Files.writeString(results.resolve("probe-child-pid"),
                                    Long.toString(child.pid()));
                                child.waitFor();
                            }
                        }
                        """, StandardCharsets.UTF_8);
                int expiredProbeExit = VerificationMain.run(
                        workspace.root(), System.getProperty("java.home"), "5", "256");
                assertThat(expiredProbeExit).isZero();
                CheckResult passed = TestReports.read(workspace.resultDirectory(), exercise.fullCount(), expiredProbeExit);
                assertThat(passed.status()).isEqualTo(CheckResult.Status.PASSED);
                assertThat(com.jinloes.practice_plugin.run.ComplexityAnalysis.read(workspace.resultDirectory())
                        .orElseThrow().render("O(n)", "O(1)")).contains("Scaling measurement unavailable", "10 seconds");
                for (String pidFile : List.of("probe-root-pid", "probe-child-pid")) {
                    long pid = Long.parseLong(Files.readString(workspace.resultDirectory().resolve(pidFile)));
                    assertThat(ProcessHandle.of(pid).map(ProcessHandle::isAlive).orElse(false)).isFalse();
                }
            } finally {
                workspace.markFinished("fingerprint");
                workspace.cleanupAfterRun();
            }
        } finally {
            com.intellij.testFramework.EdtTestUtil.runInEdtAndWait(fixture::tearDown);
        }
    }
}
