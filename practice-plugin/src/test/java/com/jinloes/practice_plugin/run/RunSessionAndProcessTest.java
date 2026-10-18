package com.jinloes.practice_plugin.run;

import com.intellij.execution.process.ProcessHandler;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

class RunSessionAndProcessTest {
    @Test
    void runControlKeepsTheFirstCancelReasonUnlessADiagnosisArrives() {
        RunSession.RunControl control = new RunSession.RunControl();

        control.requestCancel();
        control.requestCancel();

        assertThat(control.stopReason()).isEqualTo(CheckResult.Status.CANCELLED);
        assertThat(control.stopDetails()).isNull();

        control.requestStop(CheckResult.Status.TIMED_OUT, "too slow");

        assertThat(control.stopReason()).isEqualTo(CheckResult.Status.TIMED_OUT);
        assertThat(control.stopDetails()).isEqualTo("too slow");
    }

    @Test
    void watchdogCanBeCancelledOnceOrWhenAbsent() {
        RunSession.RunControl control = new RunSession.RunControl();
        RecordingFuture future = new RecordingFuture();

        control.cancelWatchdog();
        control.watchdog(future);
        control.cancelWatchdog();
        control.cancelWatchdog();

        assertThat(future.cancelled).isTrue();
    }

    @Test
    void capturedOwnedProcessesAreTerminatedAndForgotten() throws Exception {
        Process process = new ProcessBuilder("powershell.exe", "-NoProfile", "-Command", "Start-Sleep -Seconds 30")
                .start();
        RunSession.RunControl control = new RunSession.RunControl();
        control.capture(process.toHandle());

        control.terminateOwnedChildren();
        boolean exited = process.waitFor(30, TimeUnit.SECONDS);
        control.terminateOwnedChildren();

        assertThat(exited).isTrue();
        assertThat(process.isAlive()).isFalse();
    }

    @Test
    void nonOsHandlersHaveNoChildrenToCapture() {
        RunSession.RunControl control = new RunSession.RunControl();

        control.captureChildrenOf(new InMemoryProcessHandler());
        control.terminateOwnedChildren();

        assertThat(control.active()).isNull();
    }

    @Test
    void boundedProcessHandlerPureHelpersCancelAndClipOutput() {
        java.util.concurrent.atomic.AtomicBoolean cancelled = new java.util.concurrent.atomic.AtomicBoolean();

        BoundedProcessHandler.cancel(() -> cancelled.set(true));

        assertThat(cancelled).isTrue();
        BoundedProcessHandler.OutputChunk passThrough = BoundedProcessHandler.clip("short", 0);
        assertThat(passThrough.text()).isEqualTo("short");
        assertThat(passThrough.captured()).isEqualTo(5);
        assertThat(passThrough.truncated()).isFalse();
        assertThat(passThrough.skip()).isFalse();

        BoundedProcessHandler.OutputChunk capped = BoundedProcessHandler.clip("abcdef", 128 * 1024 - 2);
        assertThat(capped.text()).isEqualTo("ab");
        assertThat(capped.captured()).isEqualTo(128 * 1024);
        assertThat(capped.truncated()).isTrue();
        assertThat(capped.skip()).isFalse();

        BoundedProcessHandler.OutputChunk skipped = BoundedProcessHandler.clip("ignored", 128 * 1024);
        assertThat(skipped.skip()).isTrue();
        assertThat(skipped.text()).isEmpty();
    }

    @Test
    void practiceRunnerFormatsExampleLifecycleMessagesAndStaleResults() {
        RunSession.RunControl running = new RunSession.RunControl();
        var visible = new RunSession.ExampleSession(null, "", false, "fp", null, null, running);
        assertThat(RunDecisions.exampleStart(visible))
                .isEqualTo(new RunDecisions.ExampleStart(false, "Running visible examples."));
        var inputDebug = new RunSession.ExampleSession(null, "1 2", true, "fp", null, null, running);
        assertThat(RunDecisions.exampleStart(inputDebug).message())
                .isEqualTo("Debugging the solution on: 1 2");

        RunSession.RunControl cancelled = new RunSession.RunControl();
        cancelled.requestCancel();
        var cancelledSession = new RunSession.ExampleSession(null, "", false, "fp", null, null, cancelled);
        assertThat(RunDecisions.exampleStart(cancelledSession).cancelled()).isTrue();

        assertThat(RunDecisions.normalizedInput(null)).isEmpty();
        assertThat(RunDecisions.normalizedInput("  [1, 2] 3  ")).isEqualTo("[1, 2] 3");
        assertThat(RunDecisions.exampleFinishedText("", 0)).startsWith("Example session finished (exit 0)");
        assertThat(RunDecisions.exampleFinishedText("1 2", 7)).startsWith("Input session finished (exit 7)");

        String success = RunDecisions.exampleFinishedText("", 0);
        assertThat(RunDecisions.exampleResultText(success, null, () -> "changed")).isEqualTo(success);
        assertThat(RunDecisions.exampleResultText("Example session finished (exit 1)", "old", () -> "changed"))
                .contains("exit 1");
        assertThat(RunDecisions.exampleResultText(success, "same", () -> "same")).isEqualTo(success);
        assertThat(RunDecisions.exampleResultText(success, "old", () -> "new")).startsWith("STALE:");
        assertThat(RunDecisions.exampleResultText(success, "old", () -> {
            throw new IOException("cannot read");
        })).isEqualTo("Example result is unavailable: cannot read");
    }

    @Test
    void practiceRunnerFormatsFullCheckMessagesWithOptionalComplexity() {
        var attempt = new com.jinloes.practice_plugin.workspace.ManagedPracticeWorkspace.Attempt(
                "attempt-1", "pair-sum", "1", Path.of("Solution.java"));

        assertThat(RunDecisions.fullCheckMessage(attempt,
                new CheckResult(CheckResult.Status.PASSED, 3, 0, "ok")))
                .isEqualTo("Managed attempt: attempt-1\nFull check: PASSED\nok");
        assertThat(RunDecisions.fullCheckMessage(attempt,
                new CheckResult(CheckResult.Status.PASSED, 3, 0, "ok", "complexity")))
                .endsWith("\n\ncomplexity");
    }

    @Test
    void practiceRunnerPreflightAndCancellationHelpersClassifyLaunchState() {
        RunSession.RunControl running = new RunSession.RunControl();
        var examples = new RunSession.ExampleSession(null, "", false, "fp", null, null, running);

        assertThat(RunDecisions.exampleInstallPreflight(false, examples).proceed()).isTrue();
        assertThat(RunDecisions.exampleInstallPreflight(true, examples).message())
                .isEqualTo("Example launch cancelled.");
        assertThat(RunDecisions.exampleInstallPreflight(false, RunSession.IDLE).proceed()).isFalse();

        RunSession.RunControl cancelled = new RunSession.RunControl();
        cancelled.requestCancel();
        var cancelledExamples = new RunSession.ExampleSession(null, "", false, "fp", null, null, cancelled);
        assertThat(RunDecisions.exampleInstallPreflight(false, cancelledExamples).proceed()).isFalse();

        InMemoryProcessHandler handler = new InMemoryProcessHandler();
        running.active(handler);
        PracticeRunner.stopOwnedExampleProcess(running, handler);
    }

    private static final class RecordingFuture implements ScheduledFuture<Object> {
        private boolean cancelled;

        @Override
        public long getDelay(@NotNull TimeUnit unit) {
            return 0;
        }

        @Override
        public int compareTo(@NotNull java.util.concurrent.Delayed other) {
            return 0;
        }

        @Override
        public boolean cancel(boolean mayInterruptIfRunning) {
            cancelled = true;
            return true;
        }

        @Override
        public boolean isCancelled() {
            return cancelled;
        }

        @Override
        public boolean isDone() {
            return cancelled;
        }

        @Override
        public Object get() {
            return null;
        }

        @Override
        public Object get(long timeout, @NotNull TimeUnit unit) {
            return null;
        }

    }

    private static final class InMemoryProcessHandler extends ProcessHandler {
        private boolean destroyed;

        @Override
        protected void destroyProcessImpl() {
            destroyed = true;
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
        public java.io.OutputStream getProcessInput() {
            return null;
        }
    }
}
