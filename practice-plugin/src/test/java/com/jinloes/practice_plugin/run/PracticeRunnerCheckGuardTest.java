package com.jinloes.practice_plugin.run;

import com.intellij.execution.*;
import com.intellij.execution.configurations.ConfigurationTypeUtil;
import com.intellij.execution.executors.DefaultRunExecutor;
import com.intellij.execution.runners.ExecutionEnvironmentBuilder;
import com.intellij.openapi.application.WriteIntentReadAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.ThrowableComputable;
import com.intellij.openapi.util.io.NioFiles;
import com.intellij.testFramework.EdtTestUtil;
import com.intellij.testFramework.PlatformTestUtil;
import com.intellij.testFramework.fixtures.IdeaProjectTestFixture;
import com.intellij.testFramework.fixtures.IdeaTestFixtureFactory;
import com.jinloes.practice_plugin.catalog.ExerciseCatalog;
import com.jinloes.practice_plugin.workspace.ManagedPracticeWorkspace;
import com.jinloes.practice_plugin.workspace.PracticeModuleWorkspace;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PracticeRunnerCheckGuardTest {
    private IdeaProjectTestFixture fixture;
    private Path host;

    @BeforeEach
    void setUpProject() throws Exception {
        host = Path.of("build", "agent-fixtures", "practice-runner-check-guard-" + System.nanoTime())
                .toAbsolutePath().normalize();
        Files.createDirectories(host);
        EdtTestUtil.runInEdtAndWait(() -> {
            fixture = IdeaTestFixtureFactory.getFixtureFactory()
                    .createFixtureBuilder("practice-runner-check-guard", host, false).getFixture();
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
    void markerFailureAfterActualAcquisitionAwaitsOwnedProcessesBeforeReleasingGuard() throws Exception {
        var project = fixture.getProject();
        PracticeRunner runner = project.getService(PracticeRunner.class);
        var attempt = ManagedPracticeWorkspace.get().create(ExerciseCatalog.find("pair-sum"));
        AtomicReference<Process> acquired = new AtomicReference<>();
        AtomicReference<ProcessHandle> child = new AtomicReference<>();
        AtomicReference<Path> verificationRoot = new AtomicReference<>();
        AtomicReference<String> validMarker = new AtomicReference<>();
        AtomicBoolean boundedWait = new AtomicBoolean();
        AtomicBoolean exitedWhenFailurePublished = new AtomicBoolean();
        List<RunEvent> events = new CopyOnWriteArrayList<>();
        Consumer<RunEvent> listener = event -> {
            events.add(event);
            if (event instanceof RunEvent.FailedToStart) {
                exitedWhenFailurePublished.set(!runner.isRunning()
                        && !acquired.get().isAlive() && !child.get().isAlive());
            }
        };
        runner.addEventListener(listener);
        project.putUserData(FullCheck.COMMAND_SETUP, command -> command.setProcessCreator(builder -> {
            Path root = command.getWorkDirectory().toPath();
            verificationRoot.set(root);
            Path marker = root.resolve(".algorithm-practice-verification");
            try {
                validMarker.set(Files.readString(marker));
                Path ready = host.resolve("registration-child-ready");
                builder.command(TestChildProcess.command(
                        FullCheckWorkspaceCleanupPathsTest.RegistrationChild.class, ready.toString()));
                builder.environment().remove("JAVA_TOOL_OPTIONS");
                builder.environment().remove("JDK_JAVA_OPTIONS");
                Process process = builder.start();
                acquired.set(process);
                child.set(FullCheckWorkspaceCleanupPathsTest.awaitChildPid(ready));
                assertThat(process.descendants().map(ProcessHandle::pid).toList()).contains(child.get().pid());
                assertThat(runner.isRunning()).isTrue();
                assertThat(process.isAlive()).isTrue();
                assertThat(child.get().isAlive()).isTrue();
                Files.writeString(marker, "schema=broken");
                return new GuardedProcess(process, runner, boundedWait);
            } catch (InterruptedException exception) {
                Thread.currentThread().interrupt();
                throw new UncheckedIOException(new IOException("Interrupted awaiting the test child.", exception));
            } catch (IOException exception) {
                throw new UncheckedIOException(exception);
            }
        }));
        try {
            var settings = installPendingCheck(project, runner, attempt.id());
            assertThatThrownBy(() -> runner.startCheck(attempt.id(), settings))
                    .isInstanceOf(ExecutionException.class)
                    .hasMessageContaining("Unsupported verification workspace marker.");

            assertSettingsAbsent(RunManager.getInstance(project), settings);
            assertThat(boundedWait).isTrue();
            assertThat(exitedWhenFailurePublished).isTrue();
            assertThat(acquired.get().isAlive()).isFalse();
            assertThat(child.get().isAlive()).isFalse();
            assertThat(verificationRoot.get()).isDirectory();
            assertThat(events).singleElement().isInstanceOf(RunEvent.FailedToStart.class);

            assertThatThrownBy(() -> runner.startCheck("missing-after-marker-failure", null))
                    .isInstanceOf(ExecutionException.class)
                    .hasMessageContaining("Cannot start managed check:");
            flushEdt();
            assertThat(runner.isRunning()).isFalse();
            assertThat(events).hasSize(2).allMatch(RunEvent.FailedToStart.class::isInstance);
        } finally {
            project.putUserData(FullCheck.COMMAND_SETUP, null);
            runner.removeEventListener(listener);
            if (child.get() != null) {
                child.get().destroyForcibly();
                child.get().onExit().get(5, TimeUnit.SECONDS);
            }
            FullCheckWorkspaceCleanupPathsTest.destroyProcessTree(acquired.get());
            if (verificationRoot.get() != null && Files.exists(verificationRoot.get())) {
                Files.writeString(verificationRoot.get().resolve(".algorithm-practice-verification"), validMarker.get());
                NioFiles.deleteRecursively(verificationRoot.get());
            }
            NioFiles.deleteRecursively(attempt.directory());
        }
    }

    private static final class GuardedProcess extends Process {
        private final Process delegate;
        private final PracticeRunner runner;
        private final AtomicBoolean boundedWait;

        private GuardedProcess(Process delegate, PracticeRunner runner, AtomicBoolean boundedWait) {
            this.delegate = delegate;
            this.runner = runner;
            this.boundedWait = boundedWait;
        }

        @Override
        public java.io.OutputStream getOutputStream() {
            return delegate.getOutputStream();
        }

        @Override
        public java.io.InputStream getInputStream() {
            return delegate.getInputStream();
        }

        @Override
        public java.io.InputStream getErrorStream() {
            return delegate.getErrorStream();
        }

        @Override
        public int waitFor() throws InterruptedException {
            return delegate.waitFor();
        }

        @Override
        public int exitValue() {
            return delegate.exitValue();
        }

        @Override
        public long pid() {
            return delegate.pid();
        }

        @Override
        public boolean isAlive() {
            return delegate.isAlive();
        }

        @Override
        public void destroy() {
            delegate.destroy();
        }

        @Override
        public java.util.stream.Stream<ProcessHandle> descendants() {
            return delegate.descendants();
        }

        @Override
        public Process destroyForcibly() {
            assertThat(runner.isRunning()).isTrue();
            delegate.destroyForcibly();
            return this;
        }

        @Override
        public boolean waitFor(long timeout, TimeUnit unit) throws InterruptedException {
            assertThat(runner.isRunning()).isTrue();
            boundedWait.set(true);
            boolean exited = delegate.waitFor(timeout, unit);
            assertThat(runner.isRunning()).isTrue();
            return exited;
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
}
