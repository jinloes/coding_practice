package com.jinloes.practice_plugin.run;

import com.intellij.execution.process.ProcessHandler;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.jinloes.practice_plugin.run.CheckResult.Status.RUNNER_ERROR;
import static org.assertj.core.api.Assertions.assertThat;

class FullCheckAdditionalPathsTest {
    @TempDir
    Path tempDir;

    @Test
    void scheduleWatchdogImmediatelyCancelsForAlreadyTerminatedHandlers() {
        RunSession.RunControl control = new RunSession.RunControl();
        FullCheck fullCheck = new FullCheck(null, null, ignored -> {}, () -> {},
                (attempt, fingerprint, result) -> {});

        fullCheck.scheduleWatchdog(new InMemoryProcessHandler(true), control, tempDir, 0, 1);
        control.cancelWatchdog();

        assertThat(control.stopReason()).isNull();
    }

    @Test
    void watchdogReportsTimerInspectionFailure() {
        AtomicBoolean stopped = new AtomicBoolean();
        RunSession.RunControl control = new RunSession.RunControl();
        FullCheck fullCheck = new FullCheck(null, null, ignored -> {}, () -> stopped.set(true),
                (attempt, fingerprint, result) -> {});

        fullCheck.runWatchdogTick(new InMemoryProcessHandler(false), control, 1, () -> {
            throw new java.io.IOException("clock file disappeared");
        });

        assertThat(stopped).isTrue();
        assertThat(control.stopReason()).isEqualTo(RUNNER_ERROR);
        assertThat(control.stopDetails()).contains("Cannot inspect execution timer");
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
