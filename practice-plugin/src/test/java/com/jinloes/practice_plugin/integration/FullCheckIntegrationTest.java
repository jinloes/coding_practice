package com.jinloes.practice_plugin.integration;

import com.intellij.execution.ExecutionResult;
import com.intellij.execution.RunManager;
import com.intellij.execution.configurations.ConfigurationTypeUtil;
import com.intellij.execution.executors.DefaultRunExecutor;
import com.intellij.execution.process.OSProcessHandler;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.runners.ExecutionEnvironmentBuilder;
import com.intellij.execution.runners.ProgramRunner;
import com.intellij.openapi.util.Disposer;
import com.intellij.testFramework.EdtTestUtil;
import com.jinloes.practice_plugin.catalog.ExerciseCatalog;
import com.jinloes.practice_plugin.run.PracticeConfigurationType;
import com.jinloes.practice_plugin.run.PracticeRunConfiguration;
import com.jinloes.practice_plugin.run.PracticeRunner;
import com.jinloes.practice_plugin.state.ManagedPracticeProgress;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FullCheckIntegrationTest extends PracticeExecutionTestBase {
    @Test
    void plainSwingEdtCanStartFullCheck() throws Exception {
        CountDownLatch completed = new CountDownLatch(1);
        AtomicReference<String> result = new AtomicReference<>("");
        PracticeRunner runner = fixture.getProject().getService(PracticeRunner.class);
        runner.addListener(text -> {
            result.set(text);
            if (text.startsWith("Managed attempt:") && text.contains("Full check:")) {
                completed.countDown();
            }
        });

        runOnPlainSwingEdt(() -> runner.check(attempt.id()));

        assertThat(completed.await(360, TimeUnit.SECONDS)).as(result.get()).isTrue();
        ManagedPracticeProgress.Entry entry = ManagedPracticeProgress.get().entry(attempt.id(), attempt.exerciseId());
        assertThat(entry.status).as(entry.details).isEqualTo("PASSED");
        assertThat(entry.complexity)
                .as("a passing check reports how the solution scaled: %s", entry.details)
                .contains("intended")
                .contains("not proof of complexity");
    }

    @Test
    void isolatedFullCheckRecordsOnlyFreshSavedResults() throws Exception {
        ManagedPracticeProgress.Entry entry = awaitFullCheck(launchFullCheck());
        assertThat(entry.status).as(entry.details).isEqualTo("PASSED");
        assertThat(entry.tests).isEqualTo(ExerciseCatalog.find(attempt.exerciseId()).fullCount());
        assertThat(entry.checkedFingerprint).isEqualTo(workspace.fingerprint(attempt));
    }

    @ParameterizedTest(name = "{0} cannot pass the full check")
    @MethodSource("nonPassingSources")
    void fullCheckRejectsNegativeControls(String ignored, String source) throws Exception {
        Files.writeString(attempt.solution(), source, StandardCharsets.UTF_8);
        ManagedPracticeProgress.Entry entry = awaitFullCheck(launchFullCheck());
        assertThat(entry.status).as(entry.details).isNotEqualTo("PASSED");
        assertThat(entry.lastPassedAt).isEmpty();
    }

    @Test
    void changedSavedSolutionIsReportedAsStale() throws Exception {
        ManagedCheck check = launchFullCheck();
        assertThat(check.started.await(90, TimeUnit.SECONDS)).isTrue();
        Files.writeString(attempt.solution(), "\n// changed while checking\n",
                StandardCharsets.UTF_8, java.nio.file.StandardOpenOption.APPEND);

        ManagedPracticeProgress.Entry entry = awaitFullCheck(check);

        assertThat(entry.status).isEqualTo("RUNNER_ERROR");
        assertThat(entry.details).contains("STALE:");
        assertThat(entry.checkedFingerprint).isNotEqualTo(workspace.fingerprint(attempt));
    }

    @Test
    void cancellationStopsOnlyTheOwnedCheckProcess() throws Exception {
        Files.writeString(attempt.solution(), nonCooperativePairSum(), StandardCharsets.UTF_8);
        ManagedCheck check = launchFullCheck();
        assertThat(check.started.await(90, TimeUnit.SECONDS)).isTrue();
        assertThat(check.handler.get()).isInstanceOf(OSProcessHandler.class);
        Process process = ((OSProcessHandler) check.handler.get()).getProcess();
        Set<Long> owned = java.util.concurrent.ConcurrentHashMap.newKeySet();
        owned.add(process.pid());
        process.descendants().map(ProcessHandle::pid).forEach(owned::add);

        EdtTestUtil.runInEdtAndWait(() -> fixture.getProject().getService(PracticeRunner.class).stop());
        ManagedPracticeProgress.Entry entry = awaitFullCheck(check);

        assertThat(entry.status).isEqualTo("CANCELLED");
        assertThat(check.handler.get().isProcessTerminated()).isTrue();
        assertThat(owned).allSatisfy(pid ->
                assertThat(ProcessHandle.of(pid).map(ProcessHandle::isAlive).orElse(false)).isFalse());
    }

    @Test
    void fullCheckTimesOutAndDoesNotRecordAPass() throws Exception {
        Files.writeString(attempt.solution(), nonCooperativePairSum(), StandardCharsets.UTF_8);
        var limits = ManagedPracticeProgress.get().getState();
        limits.testSeconds = 1;
        limits.suiteSeconds = 2;

        ManagedPracticeProgress.Entry entry = awaitFullCheck(launchFullCheck());

        assertThat(entry.status).as(entry.details).isEqualTo("TIMED_OUT");
        assertThat(entry.lastPassedAt).isEmpty();
    }

    @Test
    void oneExecutionAtATimeIsEnforced() throws Exception {
        Files.writeString(attempt.solution(), nonCooperativePairSum(), StandardCharsets.UTF_8);
        ManagedCheck check = launchFullCheck();
        assertThat(check.started.await(90, TimeUnit.SECONDS)).isTrue();
        PracticeRunner runner = fixture.getProject().getService(PracticeRunner.class);
        assertThatThrownBy(() -> runner.runExamples(attempt.id()))
                .isInstanceOf(com.intellij.execution.ExecutionException.class)
                .hasMessageContaining("already active");
        runner.stop();
        awaitFullCheck(check);
    }

    private ManagedCheck launchFullCheck() throws Exception {
        ManagedCheck check = new ManagedCheck();
        PracticeRunner runner = fixture.getProject().getService(PracticeRunner.class);
        runner.addListener(text -> {
            check.status.set(text);
            if (text.startsWith("Managed attempt:") && text.contains("Full check:")) {
                check.completed.countDown();
            }
        });
        ExecutionResult execution = start(attempt.id());
        Disposer.register(fixture.getTestRootDisposable(), execution.getExecutionConsole());
        check.handler.set(execution.getProcessHandler());
        check.started.countDown();
        execution.getProcessHandler().startNotify();
        return check;
    }

    private ManagedPracticeProgress.Entry awaitFullCheck(ManagedCheck check) throws Exception {
        assertThat(check.completed.await(360, TimeUnit.SECONDS)).as(check.status.get()).isTrue();
        return ManagedPracticeProgress.get().entry(attempt.id(), attempt.exerciseId());
    }

    private ExecutionResult start(String attemptId) throws Exception {
        var environment = EdtTestUtil.runInEdtAndGet(() -> {
            var factory = ConfigurationTypeUtil.findConfigurationType(PracticeConfigurationType.class)
                    .getConfigurationFactories()[0];
            var settings = RunManager.getInstance(fixture.getProject()).createConfiguration("Full check", factory);
            var configuration = (PracticeRunConfiguration) settings.getConfiguration();
            configuration.attemptId = attemptId;
            return ExecutionEnvironmentBuilder.create(DefaultRunExecutor.getRunExecutorInstance(), settings).build();
        });
        var executor = DefaultRunExecutor.getRunExecutorInstance();
        return environment.getState().execute(
                executor, ProgramRunner.getRunner(executor.getId(), environment.getRunProfile()));
    }

    private static Stream<Arguments> nonPassingSources() {
        return Stream.of(
                Arguments.of("starter", ExerciseCatalog.resource(
                        ExerciseCatalog.find("pair-sum"), "Solution.java")),
                Arguments.of("wrong answer", """
                        package com.jinloes.practice;
                        public class Solution {
                            public static int[] findPair(int[] numbers, int target) { return new int[0]; }
                        }
                        """),
                Arguments.of("compile failure", """
                        package com.jinloes.practice;
                        public class Solution {
                            public static int[] findPair(int[] numbers, int target) { return; }
                        }
                        """),
                Arguments.of("runtime failure", """
                        package com.jinloes.practice;
                        public class Solution {
                            public static int[] findPair(int[] numbers, int target) {
                                throw new IllegalStateException("failure");
                            }
                        }
                        """));
    }

    private static String nonCooperativePairSum() {
        return """
                package com.jinloes.practice;
                public class Solution {
                    public static int[] findPair(int[] numbers, int target) {
                        while (true) { Thread.onSpinWait(); }
                    }
                }
                """;
    }

    private static final class ManagedCheck {
        private final CountDownLatch started = new CountDownLatch(1);
        private final CountDownLatch completed = new CountDownLatch(1);
        private final AtomicReference<String> status = new AtomicReference<>("");
        private final AtomicReference<ProcessHandler> handler = new AtomicReference<>();
    }

}
