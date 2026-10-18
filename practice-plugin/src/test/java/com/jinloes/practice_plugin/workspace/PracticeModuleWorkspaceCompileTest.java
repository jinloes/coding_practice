package com.jinloes.practice_plugin.workspace;

import com.intellij.openapi.application.PathManager;
import com.intellij.openapi.application.WriteAction;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.testFramework.EdtTestUtil;
import com.intellij.testFramework.fixtures.IdeaProjectTestFixture;
import com.intellij.testFramework.fixtures.IdeaTestFixtureFactory;
import com.jinloes.practice_plugin.catalog.ExampleLibraries;
import com.jinloes.practice_plugin.catalog.ExerciseCatalog;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PracticeModuleWorkspaceCompileTest {
    @TempDir Path temporary;
    private IdeaProjectTestFixture fixture;
    private ManagedPracticeWorkspace attempts;

    @BeforeEach
    void setUp() throws Exception {
        Path host = temporary.resolve("host");
        Files.createDirectories(host);
        EdtTestUtil.runInEdtAndWait(() -> {
            fixture = IdeaTestFixtureFactory.getFixtureFactory()
                    .createFixtureBuilder("managed-module-compile", host, false).getFixture();
            fixture.setUp();
            WriteAction.run(() -> ProjectRootManager.getInstance(fixture.getProject()).setProjectSdk(null));
        });
        attempts = new ManagedPracticeWorkspace(temporary.resolve("config"));
    }

    @AfterEach
    void tearDown() throws Exception {
        if (fixture != null) {
            EdtTestUtil.runInEdtAndWait(() -> {
                PracticeModuleWorkspace.get(fixture.getProject()).dispose();
                fixture.tearDown();
            });
        }
    }

    @Test
    void compileExamplesCompilesTheLearnerSolutionAndGeneratedRunner() throws Exception {
        var attempt = attempts.create(ExerciseCatalog.find("pair-sum"));
        var support = PracticeModuleWorkspace.get(fixture.getProject());
        Path runner = EdtTestUtil.runInEdtAndGet(() ->
                support.prepareExampleHarness(attempt, ExerciseCatalog.find(attempt.exerciseId())));

        support.compileExamples(attempt, runner);

        Path output = generatedBase("module-output").resolve(attempt.id());
        assertThat(output.resolve("com/jinloes/practice/Solution.class")).isRegularFile();
        assertThat(output.resolve("com/jinloes/practice/ExampleRunner.class")).isRegularFile();
        assertThat(support.javaHome()).isDirectory();
    }

    @Test
    void compileExamplesReportsCompilationFailuresAndMissingBundledLibraries() throws Exception {
        var attempt = attempts.create(ExerciseCatalog.find("pair-sum"));
        var support = PracticeModuleWorkspace.get(fixture.getProject());
        Path runner = EdtTestUtil.runInEdtAndGet(() ->
                support.prepareExampleHarness(attempt, ExerciseCatalog.find(attempt.exerciseId())));
        Files.writeString(attempt.solution(), "not valid java");

        assertThatThrownBy(() -> support.compileExamples(attempt, runner))
                .hasMessageContaining("Example compilation failed");

        var fresh = attempts.create(ExerciseCatalog.find("pair-sum"));
        Path freshRunner = EdtTestUtil.runInEdtAndGet(() ->
                support.prepareExampleHarness(fresh, ExerciseCatalog.find(fresh.exerciseId())));
        Path harness = freshRunner.getParent().getParent().getParent().getParent().getParent();
        Files.delete(harness.resolve("libs").resolve(ExampleLibraries.names().getFirst()));

        assertThatThrownBy(() -> support.compileExamples(fresh, freshRunner))
                .hasMessageContaining("Bundled example libraries are missing");
    }

    @Test
    void compileExamplesReportsProcessStartFailuresWhenOutputDisappears() throws Exception {
        var attempt = attempts.create(ExerciseCatalog.find("pair-sum"));
        var support = PracticeModuleWorkspace.get(fixture.getProject());
        Path runner = EdtTestUtil.runInEdtAndGet(() ->
                support.prepareExampleHarness(attempt, ExerciseCatalog.find(attempt.exerciseId())));
        deleteTree(generatedBase("module-output").resolve(attempt.id()));

        assertThatThrownBy(() -> support.compileExamples(attempt, runner))
                .hasMessageContaining("Could not compile examples");
    }

    @Test
    void compileExamplesDestroysTimedOutAndInterruptedCompilerProcesses() throws Exception {
        var attempt = attempts.create(ExerciseCatalog.find("pair-sum"));
        BlockingProcess timeout = new BlockingProcess(false);
        PracticeModuleWorkspace timeoutWorkspace = new PracticeModuleWorkspace(
                fixture.getProject(), (command, diagnostics) -> timeout, 1);
        Path timeoutRunner = EdtTestUtil.runInEdtAndGet(() ->
                timeoutWorkspace.prepareExampleHarness(attempt, ExerciseCatalog.find(attempt.exerciseId())));

        assertThatThrownBy(() -> timeoutWorkspace.compileExamples(attempt, timeoutRunner))
                .hasMessageContaining("Example compilation exceeded 1 seconds");
        assertThat(timeout.destroyed).isTrue();

        var interruptedAttempt = attempts.create(ExerciseCatalog.find("binary-search"));
        BlockingProcess interrupted = new BlockingProcess(true);
        PracticeModuleWorkspace interruptedWorkspace = new PracticeModuleWorkspace(
                fixture.getProject(), (command, diagnostics) -> interrupted, 1);
        Path interruptedRunner = EdtTestUtil.runInEdtAndGet(() ->
                interruptedWorkspace.prepareExampleHarness(
                        interruptedAttempt, ExerciseCatalog.find(interruptedAttempt.exerciseId())));

        try {
            assertThatThrownBy(() -> interruptedWorkspace.compileExamples(interruptedAttempt, interruptedRunner))
                    .hasMessageContaining("Example compilation was interrupted");
            assertThat(interrupted.destroyed).isTrue();
            assertThat(Thread.currentThread().isInterrupted()).isTrue();
        } finally {
            Thread.interrupted();
            EdtTestUtil.runInEdtAndWait(() -> {
                timeoutWorkspace.dispose();
                interruptedWorkspace.dispose();
            });
        }
    }

    private Path generatedBase(String kind) {
        return Path.of(PathManager.getSystemPath(), "algorithm-practice", kind,
                        Integer.toUnsignedString(fixture.getProject().getLocationHash().hashCode()))
                .toAbsolutePath().normalize();
    }

    private static void deleteTree(Path root) throws Exception {
        if (!Files.exists(root)) {
            return;
        }
        try (var paths = Files.walk(root)) {
            for (Path path : paths.sorted(Comparator.reverseOrder()).toList()) {
                Files.delete(path);
            }
        }
    }

    private static final class BlockingProcess extends Process {
        private final boolean interrupt;
        private boolean destroyed;

        private BlockingProcess(boolean interrupt) {
            this.interrupt = interrupt;
        }

        @Override
        public java.io.OutputStream getOutputStream() {
            return java.io.OutputStream.nullOutputStream();
        }

        @Override
        public java.io.InputStream getInputStream() {
            return java.io.InputStream.nullInputStream();
        }

        @Override
        public java.io.InputStream getErrorStream() {
            return java.io.InputStream.nullInputStream();
        }

        @Override
        public int waitFor() {
            return 0;
        }

        @Override
        public boolean waitFor(long timeout, TimeUnit unit) throws InterruptedException {
            if (interrupt) {
                throw new InterruptedException("test interruption");
            }
            return false;
        }

        @Override
        public int exitValue() {
            return 0;
        }

        @Override
        public void destroy() {
            destroyed = true;
        }

        @Override
        public Process destroyForcibly() {
            destroyed = true;
            return this;
        }

        @Override
        public boolean isAlive() {
            return !destroyed;
        }
    }
}
