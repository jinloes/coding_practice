package com.jinloes.practice_plugin.run;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.ExecutionManager;
import com.intellij.execution.Executor;
import com.intellij.execution.RunManager;
import com.intellij.execution.RunnerAndConfigurationSettings;
import com.intellij.execution.configurations.ConfigurationTypeUtil;
import com.intellij.execution.executors.DefaultRunExecutor;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.runners.ExecutionEnvironmentBuilder;
import com.intellij.openapi.application.WriteIntentReadAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.util.ThrowableComputable;
import com.intellij.openapi.util.io.NioFiles;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiManager;
import com.intellij.testFramework.EdtTestUtil;
import com.intellij.testFramework.PlatformTestUtil;
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
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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

        runner.handleExampleProcessStarted(
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

        runner.handleExampleProcessStarted(exampleSession(null), new InMemoryProcessHandler());
        runner.handleExampleProcessNotStarted(exampleSession(null));

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
        runner.handleExampleProcessStarted(exampleSession(attempt), new InMemoryProcessHandler());
        runner.handleExampleProcessNotStarted(exampleSession(attempt));
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
    void abortedCheckStartAndPreflightFailureCleanTemporaryConfigurationsAndPublishFailedToStart()
            throws Exception {
        var project = fixture.getProject();
        var manager = RunManager.getInstance(project);
        PracticeRunner runner = project.getService(PracticeRunner.class);
        List<RunEvent> events = new CopyOnWriteArrayList<>();
        CountDownLatch preflightFailed = new CountDownLatch(1);
        CountDownLatch startFailed = new CountDownLatch(1);
        Consumer<RunEvent> listener = event -> {
            events.add(event);
            if (event instanceof RunEvent.FailedToStart failed) {
                if (failed.text().startsWith("Check launch did not complete")) {
                    preflightFailed.countDown();
                } else if (failed.text().startsWith("Cannot start managed check:")) {
                    startFailed.countDown();
                }
            }
        };
        runner.addEventListener(listener);
        try {
            var executor = DefaultRunExecutor.getRunExecutorInstance();
            var preflightSettings = inEdtWrite(() -> {
                var pending = CheckRunConfigurations.install(project, "pending-attempt");
                var other = CheckRunConfigurations.install(project, "other-attempt");
                runner.setPendingCheckForTest(pending);
                var pub = project.getMessageBus().syncPublisher(ExecutionManager.EXECUTION_TOPIC);
                pub.processNotStarted(executor.getId(), ExecutionEnvironmentBuilder.create(executor, other).build());
                pub.processNotStarted(
                        executor.getId(), ExecutionEnvironmentBuilder.create(executor, other.getConfiguration()).build());
                assertThat(manager.getAllSettings()).contains(pending, other);
                manager.removeConfiguration(other);
                PracticeModuleWorkspace.get(project).untrack(other);
                return pending;
            });

            fireProcessNotStarted(project, executor, preflightSettings);
            assertThat(preflightFailed.await(30, TimeUnit.SECONDS)).as(events.toString()).isTrue();
            assertSettingsAbsent(manager, preflightSettings);
            assertThat(runner.isRunning()).isFalse();

            var startSettings = installPendingCheck(project, runner, "missing-start");
            assertThatThrownBy(() -> runner.startCheck("missing-start", startSettings))
                    .isInstanceOf(ExecutionException.class)
                    .hasMessageContaining("Cannot start managed check:");
            assertThat(startFailed.await(30, TimeUnit.SECONDS)).as(events.toString()).isTrue();
            assertSettingsAbsent(manager, startSettings);
            assertThat(runner.isRunning()).isFalse();
        } finally {
            runner.removeEventListener(listener);
        }
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

    @Test
    void startCheckWhileSessionActiveCleansTemporarySettingsWithoutEmittingFailedToStart() throws Exception {
        var project = fixture.getProject();
        var manager = RunManager.getInstance(project);
        var attempt = ManagedPracticeWorkspace.get().create(ExerciseCatalog.find("pair-sum"));
        PracticeRunner runner = project.getService(PracticeRunner.class);
        List<RunEvent> events = new CopyOnWriteArrayList<>();
        Consumer<RunEvent> listener = events::add;
        runner.addEventListener(listener);

        var executor = DefaultRunExecutor.getRunExecutorInstance();
        var type = ConfigurationTypeUtil.findConfigurationType(PracticeConfigurationType.class);
        var permanent = inEdtWrite(() -> {
            var created = manager.createConfiguration("Saved Check", type.getConfigurationFactories()[0]);
            manager.addConfiguration(created);
            return created;
        });
        String activeMsg = "A practice run is already active. Stop it before starting another.";
        runner.replaceSessionForTest(new RunSession.ExampleSession(
                attempt, "", false, "fp", null, null, new RunSession.RunControl()));
        try {
            var launched = installPendingCheck(project, runner, attempt.id());
            assertCheckRejectedWhileActive(runner, attempt.id(), launched, activeMsg);
            assertSettingsAbsent(manager, launched);
            fireProcessNotStarted(project, executor, launched);
            flushEdt();
            assertThat(events).isEmpty();
            assertThat(runner.isRunning()).isTrue();

            var pair = inEdtWrite(() -> {
                var displaced = CheckRunConfigurations.install(project, "displaced-attempt");
                var newer = CheckRunConfigurations.install(project, "newer-attempt");
                runner.setPendingCheckForTest(newer);
                return List.of(displaced, newer);
            });
            assertCheckRejectedWhileActive(runner, "displaced-attempt", pair.get(0), activeMsg);
            flushEdt();
            EdtTestUtil.runInEdtAndWait(() -> assertThat(manager.getAllSettings())
                    .doesNotContain(pair.get(0)).contains(pair.get(1)));

            fireProcessNotStarted(project, executor, pair.get(1));
            assertSettingsAbsent(manager, pair.get(1));
            assertThat(events).isEmpty();
            assertThat(runner.isRunning()).isTrue();

            assertCheckRejectedWhileActive(runner, attempt.id(), permanent, activeMsg);
            assertCheckRejectedWhileActive(runner, attempt.id(), null, activeMsg);
            flushEdt();
            EdtTestUtil.runInEdtAndWait(() -> assertThat(manager.getAllSettings()).contains(permanent));
            assertThat(events).isEmpty();
        } finally {
            runner.replaceSessionForTest(RunSession.IDLE);
            runner.setPendingCheckForTest(null);
            inEdtWrite(() -> {
                manager.removeConfiguration(permanent);
                return null;
            });
            runner.removeEventListener(listener);
        }
    }

    private static RunnerAndConfigurationSettings installPendingCheck(
            Project project, PracticeRunner runner, String attemptId) throws Exception {
        return inEdtWrite(() -> {
            var installed = CheckRunConfigurations.install(project, attemptId);
            runner.setPendingCheckForTest(installed);
            return installed;
        });
    }

    private static void assertCheckRejectedWhileActive(
            PracticeRunner runner, String attemptId, RunnerAndConfigurationSettings settings, String message) {
        assertThatThrownBy(() -> runner.startCheck(attemptId, settings))
                .isInstanceOf(ExecutionException.class)
                .hasMessage(message);
    }

    private static void assertSettingsAbsent(RunManager manager, RunnerAndConfigurationSettings settings) {
        flushEdt();
        EdtTestUtil.runInEdtAndWait(() -> assertThat(manager.getAllSettings()).doesNotContain(settings));
    }

    private static <T> T inEdtWrite(ThrowableComputable<T, Exception> action) throws Exception {
        return EdtTestUtil.runInEdtAndGet(() -> WriteIntentReadAction.computeThrowable(action));
    }

    private static void fireProcessNotStarted(
            Project project, Executor executor, RunnerAndConfigurationSettings settings) throws Exception {
        inEdtWrite(() -> {
            var env = ExecutionEnvironmentBuilder.create(executor, settings).build();
            project.getMessageBus().syncPublisher(ExecutionManager.EXECUTION_TOPIC)
                    .processNotStarted(executor.getId(), env);
            return null;
        });
    }

    private static void flushEdt() {
        EdtTestUtil.runInEdtAndWait(PlatformTestUtil::dispatchAllInvocationEventsInIdeEventQueue);
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
