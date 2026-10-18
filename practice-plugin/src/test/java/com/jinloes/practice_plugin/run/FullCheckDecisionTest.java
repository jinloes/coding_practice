package com.jinloes.practice_plugin.run;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.intellij.execution.process.ProcessHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

import static com.jinloes.practice_plugin.run.CheckResult.Status.COMPILATION_FAILED;
import static com.jinloes.practice_plugin.run.CheckResult.Status.PASSED;
import static com.jinloes.practice_plugin.run.CheckResult.Status.RUNNER_ERROR;
import static com.jinloes.practice_plugin.run.CheckResult.Status.TIMED_OUT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FullCheckDecisionTest {
    @TempDir
    Path tempDir;

    @Test
    void watchdogDecisionDistinguishesSuiteTimeoutSetupTimeoutAndStillRunning() {
        assertThat(FullCheck.watchdogStop(true, 999, 1)).isNull();
        assertThat(FullCheck.watchdogStop(false, TimeUnit.MINUTES.toNanos(5), 1)).isNull();
        assertThat(FullCheck.watchdogStop(new FullCheck.TimerState(true, 999), 1)).isNull();

        assertThat(FullCheck.watchdogStop(true, 1_001, 1))
                .isEqualTo(new FullCheck.WatchdogStop(TIMED_OUT,
                        "Tests exceeded 1 seconds. The owned process tree was stopped."));
        assertThat(FullCheck.watchdogStop(false, TimeUnit.MINUTES.toNanos(5) + 1, 1))
                .isEqualTo(new FullCheck.WatchdogStop(TIMED_OUT,
                        "Setup/compilation exceeded five minutes. Check SDK, network, and Run output."));
    }

    @Test
    void watchdogTickReturnsForTerminatedHandlersAndStopsExpiredSuites() throws Exception {
        java.util.concurrent.atomic.AtomicBoolean stopped = new java.util.concurrent.atomic.AtomicBoolean();
        FullCheck fullCheck = new FullCheck(null, null, ignored -> {}, () -> stopped.set(true),
                (attempt, fingerprint, result) -> {});
        RunSession.RunControl terminatedControl = new RunSession.RunControl();
        InMemoryProcessHandler terminated = new InMemoryProcessHandler(true);

        fullCheck.runWatchdogTick(terminated, terminatedControl, tempDir, 0, 1);

        assertThat(stopped).isFalse();
        assertThat(terminatedControl.stopReason()).isNull();

        Files.writeString(tempDir.resolve("started"), "started", StandardCharsets.UTF_8);
        Files.setLastModifiedTime(tempDir.resolve("started"),
                java.nio.file.attribute.FileTime.fromMillis(System.currentTimeMillis() - 2_000));
        RunSession.RunControl expiredControl = new RunSession.RunControl();
        fullCheck.runWatchdogTick(new InMemoryProcessHandler(false), expiredControl, tempDir, 0, 1);

        assertThat(stopped).isTrue();
        assertThat(expiredControl.stopReason()).isEqualTo(TIMED_OUT);
        assertThat(expiredControl.stopDetails()).contains("Tests exceeded 1 seconds");
    }

    @Test
    void processResultUsesCompilationPhaseBeforeReports() throws Exception {
        CheckResult report = new CheckResult(PASSED, 3, 0, "ok");

        assertThat(FullCheck.phaseOrSetup(tempDir)).isEqualTo("setup");
        FullCheck.TimerState setupTimer = FullCheck.timerState(tempDir, System.nanoTime());
        assertThat(setupTimer.testsStarted()).isFalse();
        assertThat(setupTimer.elapsed()).isNotNegative();
        Files.writeString(tempDir.resolve("phase"), "compiling", StandardCharsets.UTF_8);
        assertThat(FullCheck.phaseOrSetup(tempDir)).isEqualTo("compiling");
        Files.writeString(tempDir.resolve("started"), "started", StandardCharsets.UTF_8);
        Files.setLastModifiedTime(tempDir.resolve("started"),
                java.nio.file.attribute.FileTime.fromMillis(System.currentTimeMillis() - 2_000));
        FullCheck.TimerState suiteTimer = FullCheck.timerState(tempDir, System.nanoTime());
        assertThat(suiteTimer.testsStarted()).isTrue();
        assertThat(suiteTimer.elapsed()).isGreaterThanOrEqualTo(2_000);

        assertThat(FullCheck.resultForPhase("compiling", 1, () -> {
            throw new AssertionError("reports must not be read for compilation failures");
        }).status()).isEqualTo(COMPILATION_FAILED);
        assertThat(FullCheck.resultForPhase("setup", 1, () -> report)).isSameAs(report);
        assertThatThrownBy(() -> FullCheck.resultForPhase("setup", 1, () -> {
            throw new IOException("missing report");
        })).isInstanceOf(IOException.class).hasMessageContaining("missing report");
    }

    @Test
    void stoppedStaleAndComplexityResultsAreRenderedConsistently() throws Exception {
        RunSession.RunControl cancelled = new RunSession.RunControl();
        cancelled.requestCancel();
        assertThat(FullCheck.stoppedResult(cancelled).details())
                .isEqualTo("Run cancelled. No passing result was recorded.");
        RunSession.RunControl timedOut = new RunSession.RunControl();
        timedOut.requestStop(TIMED_OUT, "too slow");
        assertThat(FullCheck.stoppedResult(timedOut).details()).isEqualTo("too slow");

        CheckResult failed = new CheckResult(RUNNER_ERROR, 2, 1, "old");
        assertThat(FullCheck.staleResult(failed).details()).contains("STALE:", "old");
        assertThat(FullCheck.resultProcessingFailure(new IllegalArgumentException("bad result")).details())
                .isEqualTo("bad result");
        assertThat(FullCheck.cleanupFailureResult(failed, new IOException("locked")).details())
                .isEqualTo("Verification cleanup failed: locked");

        CheckResult passed = new CheckResult(PASSED, 1, 0, "ok");
        assertThat(FullCheck.withMeasuredComplexity(passed, "O(n)", "O(1)", tempDir)).isSameAs(passed);

        Files.writeString(tempDir.resolve(ComplexityAnalysis.FILE), "x".repeat(64_001), StandardCharsets.UTF_8);
        assertThat(FullCheck.withMeasuredComplexity(passed, "O(n)", "O(1)", tempDir).complexity())
                .contains("Scaling measurement unavailable", "size limit");

        assertThat(FullCheck.withMeasuredComplexity(failed, "O(n)", "O(1)", tempDir)).isSameAs(failed);
    }

    private static final class InMemoryProcessHandler extends ProcessHandler {
        private final boolean terminated;

        private InMemoryProcessHandler(boolean terminated) {
            this.terminated = terminated;
        }

        @Override
        public boolean isProcessTerminated() {
            return terminated;
        }

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
}
