package com.jinloes.practice_plugin.workspace;

import com.jinloes.practice_plugin.catalog.ExerciseCatalog;
import com.jinloes.practice_plugin.run.CheckResult;
import com.jinloes.practice_plugin.run.TestReports;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PracticeWorkspaceTest {
    private static final ExerciseCatalog.Exercise PAIR_SUM = ExerciseCatalog.find("pair-sum");

    @TempDir
    Path tempDir;

    @Test
    void createsWorkspaceWithPackagedGradleBootstrap() throws Exception {
        Path root = tempDir.resolve("practice");

        PracticeWorkspace.create(root);

        assertThat(PracticeWorkspace.isWorkspace(root)).isTrue();
        assertThat(Files.readString(root.resolve(PracticeWorkspace.MARKER))).isEqualTo("schema=1\n");
        assertThat(Files.isDirectory(root.resolve("attempts"))).isTrue();
        assertThat(Files.readString(root.resolve("settings.gradle")))
                .contains("rootProject.name = 'algorithm-practice'");
        assertThat(Files.readString(root.resolve("build.gradle")))
                .contains("org.junit:junit-bom:5.11.4");
        for (String bootstrap : List.of(
                "gradlew",
                "gradlew.bat",
                "gradle/wrapper/gradle-wrapper.jar",
                "gradle/wrapper/gradle-wrapper.properties")) {
            Path file = root.resolve(bootstrap);
            assertThat(file).as("packaged bootstrap %s", bootstrap).isRegularFile();
            assertThat(Files.size(file)).as("packaged bootstrap %s", bootstrap).isPositive();
        }
        assertThat(root.resolve("gradlew")).isExecutable();
    }

    @Test
    void neverOverwritesAnExistingDirectoryOrSolution() throws Exception {
        Path root = tempDir.resolve("existing");
        Path solution = root.resolve("src/main/java/com/jinloes/practice/Solution.java");
        Files.createDirectories(solution.getParent());
        Files.writeString(solution, "keep this solution");

        assertThatThrownBy(() -> PracticeWorkspace.create(root))
                .isInstanceOf(IOException.class);

        assertThat(Files.readString(solution)).isEqualTo("keep this solution");
        try (var entries = Files.list(root)) {
            assertThat(entries.toList()).containsExactly(root.resolve("src"));
        }
    }

    @Test
    void createsIsolatedAttemptsAndDoesNotReplaceTheirSolutions() throws Exception {
        Path root = createWorkspace();
        PracticeWorkspace.Attempt first = PracticeWorkspace.createAttempt(root, PAIR_SUM);
        PracticeWorkspace.Attempt second = PracticeWorkspace.createAttempt(root, PAIR_SUM);
        String originalSecondSolution = Files.readString(second.solution());
        Files.writeString(first.solution(), "custom first solution");

        assertThat(first.id()).isNotEqualTo(second.id());
        assertThat(first.directory()).isNotEqualTo(second.directory());
        assertThat(Files.readString(first.solution())).isEqualTo("custom first solution");
        assertThat(Files.readString(second.solution())).isEqualTo(originalSecondSolution);
        assertThat(first.directory().resolve("src/test/java/com/jinloes/practice/ExamplesTest.java"))
                .isRegularFile();
        assertThat(first.directory().resolve("src/test/java/com/jinloes/practice/CorrectnessTest.java"))
                .isRegularFile();
        assertThat(root.resolve("src/main/java/com/jinloes/practice/Solution.java")).doesNotExist();
    }

    @Test
    void resumesAnAttemptAndListsOnlyMatchingMarkedAttempts() throws Exception {
        Path root = createWorkspace();
        PracticeWorkspace.Attempt pairSum = PracticeWorkspace.createAttempt(root, PAIR_SUM);
        PracticeWorkspace.Attempt binarySearch =
                PracticeWorkspace.createAttempt(root, ExerciseCatalog.find("binary-search"));
        Files.createDirectories(root.resolve("attempts/ignored-directory"));
        String editedSolution = "edited while the IDE was closed";
        Files.writeString(pairSum.solution(), editedSolution);

        PracticeWorkspace.Attempt resumed = PracticeWorkspace.readAttempt(root, pairSum.id());

        assertThat(resumed).isEqualTo(pairSum);
        assertThat(Files.readString(resumed.solution())).isEqualTo(editedSolution);
        assertThat(PracticeWorkspace.attempts(root, PAIR_SUM.id()))
                .extracting(PracticeWorkspace.Attempt::id)
                .containsExactly(pairSum.id());
        assertThat(PracticeWorkspace.attempts(root, binarySearch.exerciseId()))
                .extracting(PracticeWorkspace.Attempt::id)
                .containsExactly(binarySearch.id());
    }

    @Test
    void rejectsAttemptsDirectoryRedirectedOutsideTheWorkspace() throws Exception {
        Path root = createWorkspace();
        Path outside = tempDir.resolve("outside");
        Files.createDirectories(outside);
        Files.delete(root.resolve("attempts"));
        Files.createSymbolicLink(root.resolve("attempts"), outside);

        assertThatThrownBy(() -> PracticeWorkspace.createAttempt(root, PAIR_SUM))
                .isInstanceOf(IOException.class);
        assertThatThrownBy(() -> PracticeWorkspace.attempts(root, PAIR_SUM.id()))
                .isInstanceOf(IOException.class);
        try (var entries = Files.list(outside)) {
            assertThat(entries.toList()).isEmpty();
        }
    }

    @Test
    void rejectsAttemptPathTraversalAndExternalSymlinks() throws Exception {
        Path root = createWorkspace();
        Path outside = tempDir.resolve("outside-attempt");
        Files.createDirectories(outside);
        Files.writeString(outside.resolve(".practice-attempt"),
                "exercise=pair-sum\nrevision=1\ncreated=2026-01-01T00:00:00Z\n");
        Files.createSymbolicLink(root.resolve("attempts/outside"), outside);

        assertThatThrownBy(() -> PracticeWorkspace.readAttempt(root, "../outside"))
                .isInstanceOf(IOException.class);
        assertThatThrownBy(() -> PracticeWorkspace.readAttempt(root, "outside"))
                .isInstanceOf(IOException.class);
    }

    @Test
    void rejectsFingerprintingAnAttemptOutsideTheWorkspace() throws Exception {
        Path root = createWorkspace();
        Path outside = tempDir.resolve("foreign-attempt");
        Files.createDirectories(outside.resolve("src"));
        Files.writeString(outside.resolve("src/Solution.java"), "foreign source");
        Files.writeString(outside.resolve("build.gradle"), "foreign build");
        Files.writeString(outside.resolve(".practice-attempt"),
                "exercise=pair-sum\nrevision=1\ncreated=2026-01-01T00:00:00Z\n");
        PracticeWorkspace.Attempt foreign =
                new PracticeWorkspace.Attempt("pair-sum-foreign", PAIR_SUM.id(),
                        PracticeWorkspace.REVISION, outside);

        assertThatThrownBy(() -> PracticeWorkspace.fingerprint(root, foreign))
                .isInstanceOf(IOException.class);
    }

    @Test
    void rejectsWorkspaceWithAnUnsupportedMarkerSchema() throws Exception {
        Path root = tempDir.resolve("not-a-workspace");
        Files.createDirectories(root.resolve("attempts"));
        Files.writeString(root.resolve(PracticeWorkspace.MARKER), "schema=2\n");

        assertThat(PracticeWorkspace.isWorkspace(root)).isFalse();
        assertThatThrownBy(() -> PracticeWorkspace.attempts(root, PAIR_SUM.id()))
                .isInstanceOf(IOException.class);
        assertThatThrownBy(() -> PracticeWorkspace.createAttempt(root, PAIR_SUM))
                .isInstanceOf(IOException.class);
    }

    @Test
    void fingerprintChangesWhenSourceTestsOrBuildInputsChange() throws Exception {
        Path root = createWorkspace();
        PracticeWorkspace.Attempt attempt = PracticeWorkspace.createAttempt(root, PAIR_SUM);
        String baseline = PracticeWorkspace.fingerprint(root, attempt);

        Files.writeString(attempt.solution(), Files.readString(attempt.solution()) + "\n// changed\n");
        assertThat(PracticeWorkspace.fingerprint(root, attempt)).isNotEqualTo(baseline);

        Files.writeString(
                attempt.directory().resolve("src/test/java/com/jinloes/practice/ExamplesTest.java"),
                Files.readString(attempt.directory().resolve("src/test/java/com/jinloes/practice/ExamplesTest.java"))
                        + "\n// changed\n");
        assertThat(PracticeWorkspace.fingerprint(root, attempt)).isNotEqualTo(baseline);

        Files.writeString(root.resolve("build.gradle"), Files.readString(root.resolve("build.gradle"))
                + "\n// changed\n");
        assertThat(PracticeWorkspace.fingerprint(root, attempt)).isNotEqualTo(baseline);

        Files.writeString(attempt.directory().resolve("build.gradle"),
                Files.readString(attempt.directory().resolve("build.gradle")) + "\n// changed\n");
        assertThat(PracticeWorkspace.fingerprint(root, attempt)).isNotEqualTo(baseline);
    }

    @Test
    @Tag("gradle-integration")
    void generatedWorkspaceRunsOnlyTheSelectedAttemptAndRejectsAStalePass() throws Exception {
        Path root = createWorkspace();
        PracticeWorkspace.Attempt passing = PracticeWorkspace.createAttempt(root, PAIR_SUM);
        PracticeWorkspace.Attempt compilingFailure = PracticeWorkspace.createAttempt(root, PAIR_SUM);
        try (InputStream input = getClass().getResourceAsStream("/reference/pair-sum/Solution.java")) {
            assertThat(input).isNotNull();
            Files.writeString(passing.solution(),
                    new String(input.readAllBytes(), StandardCharsets.UTF_8));
        }
        Files.writeString(compilingFailure.solution(), "This is intentionally invalid Java.");

        Path results = root.resolve(".practice-results/unique").resolve(passing.id());
        int passingExit = runGradle(root, passing.id(), "unique");

        assertThat(passingExit).isZero();
        CheckResult passingResult =
                TestReports.read(results, PAIR_SUM.fullCount(), true, passingExit);
        assertThat(passingResult.status()).isEqualTo(CheckResult.Status.PASSED);
        assertThat(passingResult.tests()).isEqualTo(PAIR_SUM.fullCount());
        assertThat(passingResult.failures()).isZero();
        assertThat(root.resolve(".practice-results/unique").resolve(compilingFailure.id()))
                .doesNotExist();

        Files.writeString(passing.solution(), "This is now intentionally invalid Java.");
        int compilationExit = runGradle(root, passing.id(), "unique");

        assertThat(compilationExit).isNotZero();
        assertThat(Files.readString(root.resolve(".practice-results/unique/phase")))
                .isEqualTo("compiling");
        CheckResult staleReport =
                TestReports.read(results, PAIR_SUM.fullCount(), true, compilationExit);
        assertThat(staleReport.status()).isEqualTo(CheckResult.Status.RUNNER_ERROR);
    }

    private static int runGradle(Path root, String attemptId, String runId) throws Exception {
        String javaName = System.getProperty("os.name").startsWith("Windows")
                ? "java.exe" : "java";
        Path java = Path.of(System.getProperty("java.home"), "bin", javaName);
        Process process = new ProcessBuilder(
                java.toString(),
                "-classpath",
                root.resolve("gradle/wrapper/gradle-wrapper.jar").toString(),
                "org.gradle.wrapper.GradleWrapperMain",
                ":" + attemptId + ":test",
                "--no-daemon",
                "--rerun-tasks",
                "--no-build-cache",
                "-PpracticeRun=" + runId)
                .directory(root.toFile())
                .redirectErrorStream(true)
                .redirectOutput(ProcessBuilder.Redirect.DISCARD)
                .start();
        try {
            assertThat(process.waitFor(180, TimeUnit.SECONDS))
                    .as("Gradle wrapper completed within the timeout")
                    .isTrue();
            return process.exitValue();
        } finally {
            destroyProcessTree(process);
        }
    }

    private static void destroyProcessTree(Process process) {
        ProcessHandle root = process.toHandle();
        List<ProcessHandle> descendants = new ArrayList<>(root.descendants().toList());
        descendants.sort(Comparator.comparingLong(ProcessHandle::pid).reversed());
        descendants.forEach(handle -> {
            if (handle.isAlive()) {
                handle.destroyForcibly();
            }
        });
        if (root.isAlive()) {
            root.destroyForcibly();
        }
    }

    private Path createWorkspace() throws IOException {
        Path root = tempDir.resolve("workspace-" + System.nanoTime());
        PracticeWorkspace.create(root);
        return root;
    }
}
