package com.jinloes.practice_plugin.run;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.ExecutionManager;
import com.intellij.execution.Executor;
import com.intellij.execution.RunManager;
import com.intellij.execution.RunnerAndConfigurationSettings;
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

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
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
