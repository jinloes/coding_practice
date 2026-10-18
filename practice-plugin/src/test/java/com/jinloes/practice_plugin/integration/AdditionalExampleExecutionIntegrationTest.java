package com.jinloes.practice_plugin.integration;

import com.intellij.execution.ExecutionManager;
import com.intellij.testFramework.EdtTestUtil;
import com.jinloes.practice_plugin.run.PracticeRunner;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

class AdditionalExampleExecutionIntegrationTest extends PracticeExecutionTestBase {
    @Test
    void noArgumentDebugUsesVisibleExamples() throws Exception {
        CountDownLatch completed = new CountDownLatch(1);
        java.util.concurrent.atomic.AtomicReference<String> result = new java.util.concurrent.atomic.AtomicReference<>("");
        PracticeRunner runner = fixture.getProject().getService(PracticeRunner.class);
        runner.addListener(text -> {
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
}
