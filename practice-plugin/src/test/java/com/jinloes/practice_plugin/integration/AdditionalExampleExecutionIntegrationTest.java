package com.jinloes.practice_plugin.integration;

import com.intellij.execution.ExecutionManager;
import com.intellij.testFramework.EdtTestUtil;
import com.jinloes.practice_plugin.run.PracticeRunner;
import com.jinloes.practice_plugin.state.ManagedPracticeProgress;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AdditionalExampleExecutionIntegrationTest extends PracticeExecutionTestBase {
    @Test
    void noArgumentDebugUsesVisibleExamples() throws Exception {
        CountDownLatch completed = new CountDownLatch(1);
        AtomicReference<String> result = new AtomicReference<>("");
        PracticeRunner runner = fixture.getProject().getService(PracticeRunner.class);
        runner.addEventListener(event -> {
            String text = event.text();
            result.set(text);
            if (text.startsWith("Example session finished")) {
                completed.countDown();
            }
        });

        EdtTestUtil.runInEdtAndWait(() -> {
            ((com.intellij.execution.impl.ExecutionManagerImpl) ExecutionManager
                    .getInstance(fixture.getProject())).setForceCompilationInTests(true);
            runner.debugExamples(attempt.id());
        });

        assertThat(completed.await(90, TimeUnit.SECONDS)).as(result.get()).isTrue();
        assertThat(result.get()).contains("exit 0");
    }

    @Test
    void invalidVisibleSolutionReportsCompilationFailureWithoutLaunching() throws Exception {
        Files.writeString(attempt.solution(), "not valid Java", StandardCharsets.UTF_8);
        CountDownLatch completed = new CountDownLatch(1);
        AtomicReference<String> result = new AtomicReference<>("");
        PracticeRunner runner = fixture.getProject().getService(PracticeRunner.class);
        runner.addEventListener(event -> {
            String text = event.text();
            result.set(text);
            if (text.startsWith("Example compilation failed:")) {
                completed.countDown();
            }
        });

        EdtTestUtil.runInEdtAndWait(() -> runner.runExamples(attempt.id()));

        assertThat(completed.await(30, TimeUnit.SECONDS)).as(result.get()).isTrue();
        assertThat(runner.isRunning()).isFalse();
        assertThat(ManagedPracticeProgress.get().entry(attempt.id(), attempt.exerciseId()).status)
                .isEqualTo("NOT_RUN");
    }

    @Test
    void missingAttemptReportsAStartFailure() {
        PracticeRunner runner = fixture.getProject().getService(PracticeRunner.class);

        assertThatThrownBy(() -> EdtTestUtil.runInEdtAndWait(() -> runner.runExamples("missing-attempt")))
                .isInstanceOf(com.intellij.execution.ExecutionException.class)
                .hasMessageContaining("Invalid managed attempt ID: missing-attempt");
        assertThatThrownBy(() -> EdtTestUtil.runInEdtAndWait(() -> runner.debugExamples("missing-attempt")))
                .isInstanceOf(com.intellij.execution.ExecutionException.class)
                .hasMessageContaining("Invalid managed attempt ID: missing-attempt");

        assertThat(runner.isRunning()).isFalse();
    }
}
