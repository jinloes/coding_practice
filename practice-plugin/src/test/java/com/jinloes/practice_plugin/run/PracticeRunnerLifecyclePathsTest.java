package com.jinloes.practice_plugin.run;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.RunManager;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.openapi.application.WriteIntentReadAction;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.util.ThrowableComputable;
import com.intellij.openapi.util.io.NioFiles;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiManager;
import com.intellij.testFramework.EdtTestUtil;
import com.intellij.testFramework.fixtures.IdeaProjectTestFixture;
import com.intellij.testFramework.fixtures.IdeaTestFixtureFactory;
import com.jinloes.practice_plugin.catalog.ExerciseCatalog;
import com.jinloes.practice_plugin.workspace.ManagedPracticeWorkspace;
import com.jinloes.practice_plugin.workspace.PracticeModuleWorkspace;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Delayed;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;

class PracticeRunnerLifecyclePathsTest {
    private IdeaProjectTestFixture fixture;
    private Path host;

    @BeforeEach
    void setUpProject() throws Exception {
        host = Path.of("build", "agent-fixtures", "practice-runner-lifecycle-" + System.nanoTime())
                .toAbsolutePath().normalize();
        Files.createDirectories(host);
        EdtTestUtil.runInEdtAndWait(() -> {
            fixture = IdeaTestFixtureFactory.getFixtureFactory()
                    .createFixtureBuilder("practice-runner-lifecycle", host, false).getFixture();
            fixture.setUp();
        });
    }

    @AfterEach
    void tearDownProject() throws Exception {
        if (fixture != null) {
            fixture.getProject().getService(PracticeRunner.class).stop();
            EdtTestUtil.runInEdtAndWait(() -> fixture.tearDown());
        }
        if (host != null) {
            NioFiles.deleteRecursively(host);
        }
    }

    @Test
    void exampleProcessStartedCancelsAlreadyStoppedSessions() {
        PracticeRunner runner = fixture.getProject().getService(PracticeRunner.class);
        RunSession.RunControl control = new RunSession.RunControl();
        control.requestCancel();
        InMemoryProcessHandler handler = new InMemoryProcessHandler();

        runner.examples().processStarted(
                new RunSession.ExampleSession(null, "", false, "fp", null, null, control), handler);

        assertThat(control.active()).isSameAs(handler);
    }

    @Test
    void exampleProcessStartedAndNotStartedReportLifecycleMessages() throws Exception {
        PracticeRunner runner = fixture.getProject().getService(PracticeRunner.class);
        CountDownLatch running = new CountDownLatch(1);
        CountDownLatch notStarted = new CountDownLatch(1);
        AtomicReference<String> last = new AtomicReference<>("");
        runner.addEventListener(event -> {
            String text = event.text();
            last.set(text);
            if (text.equals("Running visible examples.")) {
                running.countDown();
            } else if (text.startsWith("Example launch did not complete")) {
                notStarted.countDown();
            }
        });

        runner.examples().processStarted(exampleSession(null), new InMemoryProcessHandler());
        runner.examples().processNotStarted(exampleSession(null));

        assertThat(running.await(30, TimeUnit.SECONDS)).as(last.get()).isTrue();
        assertThat(notStarted.await(30, TimeUnit.SECONDS)).as(last.get()).isTrue();
    }

    @Test
    void disposeCancelsActiveWatchdog() {
        PracticeRunner runner = fixture.getProject().getService(PracticeRunner.class);
        RunSession.RunControl control = new RunSession.RunControl();
        RecordingFuture future = new RecordingFuture();
        control.watchdog(future);
        runner.replaceSessionForTest(new RunSession.CheckSession("attempt", null, control));

        runner.dispose();

        assertThat(future.cancelled).isTrue();
    }

    @Test
    void asynchronousSaveContinuationReportsStartFailures() throws Exception {
        assertMissingAttemptFailsToStart(fixture.getProject().getService(PracticeRunner.class));
    }

    @Test
    void injectedContinuationReportsStartFailures() throws Exception {
        PracticeRunner runner = new PracticeRunner(fixture.getProject(), Runnable::run);
        try {
            assertMissingAttemptFailsToStart(runner);
        } finally {
            Disposer.dispose(runner);
        }
    }

    @Test
    void publishesStructuredRunEventsAndRemovesEventListeners() throws Exception {
        PracticeRunner runner = fixture.getProject().getService(PracticeRunner.class);
        List<RunEvent> events = new CopyOnWriteArrayList<>();
        CountDownLatch received = new CountDownLatch(3);
        Consumer<RunEvent> listener = event -> {
            events.add(event);
            received.countDown();
        };

        int before = runner.listenerCount();
        runner.addEventListener(listener);
        assertThat(runner.listenerCount()).isEqualTo(before + 1);

        var attempt = ManagedPracticeWorkspace.get().create(ExerciseCatalog.find("pair-sum"));
        runner.examples().processStarted(exampleSession(attempt), new InMemoryProcessHandler());
        runner.examples().processNotStarted(exampleSession(attempt));
        runner.runExamples("missing-attempt");

        assertThat(received.await(30, TimeUnit.SECONDS)).isTrue();
        assertThat(events).contains(new RunEvent.Started(attempt.id(), "Running visible examples."));
        assertThat(events).anySatisfy(event -> assertThat(event).isInstanceOf(RunEvent.ExampleFinished.class));
        assertThat(events).anySatisfy(event -> assertThat(event).isInstanceOf(RunEvent.FailedToStart.class));

        runner.removeEventListener(listener);
        assertThat(runner.listenerCount()).isEqualTo(before);

        assertThat(new RunEvent.Started(null, null)).isEqualTo(new RunEvent.Started("", ""));
        assertThat(new RunEvent.ExampleFinished(null, null)).isEqualTo(new RunEvent.ExampleFinished("", ""));
        assertThat(new RunEvent.CheckFinished("id-1", "pair-sum", "ok").exerciseId()).isEqualTo("pair-sum");
        assertThat(new RunEvent.FailedToStart(null).text()).isEmpty();
    }

    @Test
    void temporaryExampleConfigurationsAreTrackedAndCleanedOnWorkspaceDispose() throws Exception {
        var project = fixture.getProject();
        var manager = RunManager.getInstance(project);
        var attempt = ManagedPracticeWorkspace.get().create(ExerciseCatalog.find("pair-sum"));
        var modules = PracticeModuleWorkspace.get(project);
        inEdtWrite(() -> {
            var module = modules.open(attempt);
            var runnerPath = modules.prepareExampleHarness(attempt, ExerciseCatalog.find("pair-sum"));
            var vFile = LocalFileSystem.getInstance().refreshAndFindFileByNioFile(runnerPath);
            var psiFile = (PsiJavaFile) PsiManager.getInstance(project).findFile(vFile);
            var installed = ExampleRunConfigurations.install(
                    project, attempt, attempt.id(), psiFile, module, "", false);
            assertThat(manager.getAllSettings()).contains(installed);
            modules.dispose();
            assertThat(manager.getAllSettings()).doesNotContain(installed);
            return null;
        });
    }

    @Test
    void runExamplesPublishesImmediateStartedEventWhenSessionIsClaimed() throws Exception {
        var project = fixture.getProject();
        var attempt = ManagedPracticeWorkspace.get().create(ExerciseCatalog.find("pair-sum"));
        PracticeRunner runner = project.getService(PracticeRunner.class);
        List<RunEvent> events = new CopyOnWriteArrayList<>();
        CountDownLatch started = new CountDownLatch(1);
        CountDownLatch finished = new CountDownLatch(1);
        Consumer<RunEvent> listener = event -> {
            events.add(event);
            if (event instanceof RunEvent.Started s && s.text().equals("Compiling examples.")) {
                started.countDown();
            } else if (event instanceof RunEvent.ExampleFinished) {
                finished.countDown();
            }
        };
        runner.addEventListener(listener);
        try {
            EdtTestUtil.runInEdtAndWait(() -> {
                try {
                    runner.runExamples(attempt.id());
                } catch (ExecutionException exception) {
                    throw new AssertionError(exception);
                }
            });
            assertThat(started.await(10, TimeUnit.SECONDS)).as(events.toString()).isTrue();
            assertThat(events).contains(new RunEvent.Started(attempt.id(), "Compiling examples."));
            runner.stop();
            assertThat(finished.await(30, TimeUnit.SECONDS)).as(events.toString()).isTrue();
            assertThat(runner.isRunning()).isFalse();
        } finally {
            runner.removeEventListener(listener);
            runner.stop();
        }
    }

    private static <T> T inEdtWrite(ThrowableComputable<T, Exception> action) throws Exception {
        return EdtTestUtil.runInEdtAndGet(() -> WriteIntentReadAction.computeThrowable(action));
    }

    private static RunSession.ExampleSession exampleSession(ManagedPracticeWorkspace.Attempt attempt) {
        return new RunSession.ExampleSession(attempt, "", false, "fp", null, null, new RunSession.RunControl());
    }

    private static void assertMissingAttemptFailsToStart(PracticeRunner runner) throws Exception {
        CountDownLatch message = new CountDownLatch(1);
        AtomicReference<String> last = new AtomicReference<>("");
        runner.addEventListener(event -> {
            last.set(event.text());
            if (event.text().startsWith("Cannot start practice execution:")) {
                message.countDown();
            }
        });
        runner.runExamples("missing-attempt");
        assertThat(message.await(30, TimeUnit.SECONDS)).as(last.get()).isTrue();
        assertThat(last.get()).contains("missing-attempt");
    }

    private static final class InMemoryProcessHandler extends ProcessHandler {
        @Override
        protected void destroyProcessImpl() {
            notifyProcessTerminated(0);
        }

        @Override
        protected void detachProcessImpl() {
            notifyProcessDetached();
        }

        @Override
        public boolean detachIsDefault() {
            return false;
        }

        @Override
        public OutputStream getProcessInput() {
            return null;
        }
    }

    private static final class RecordingFuture implements ScheduledFuture<Object> {
        private boolean cancelled;

        @Override public long getDelay(@NotNull TimeUnit unit) { return 0; }
        @Override public int compareTo(@NotNull Delayed other) { return 0; }
        @Override public boolean cancel(boolean mayInterruptIfRunning) { cancelled = true; return true; }
        @Override public boolean isCancelled() { return cancelled; }
        @Override public boolean isDone() { return cancelled; }
        @Override public Object get() { return null; }
        @Override public Object get(long timeout, @NotNull TimeUnit unit) { return null; }
    }
}
