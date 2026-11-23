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
        assertThat(FullCheck.watchdogStop(false, TimeUnit.SECONDS.toNanos(60), 1)).isNull();
        assertThat(FullCheck.watchdogStop(new FullCheck.TimerState(true, 999), 1)).isNull();

        assertThat(FullCheck.watchdogStop(true, 1_001, 1))
                .isEqualTo(new FullCheck.WatchdogStop(TIMED_OUT,
                        "Tests exceeded 1 seconds. The owned process tree was stopped."));
        assertThat(FullCheck.watchdogStop(false, TimeUnit.SECONDS.toNanos(60) + 1, 1))
                .isEqualTo(new FullCheck.WatchdogStop(TIMED_OUT,
                        "Compilation/setup exceeded 60 seconds. Check SDK and Run output."));
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
    void advisoryProbeDoesNotConsumeTheCorrectnessSuiteTimeout() throws Exception {
        Files.writeString(tempDir.resolve("phase"), "testing", StandardCharsets.UTF_8);
        Files.writeString(tempDir.resolve("started"), "started", StandardCharsets.UTF_8);
        Files.setLastModifiedTime(tempDir.resolve("started"),
                java.nio.file.attribute.FileTime.fromMillis(System.currentTimeMillis() - 2_000));
        Files.writeString(tempDir.resolve("TEST-com.jinloes.practice.ExamplesTest.xml"), "<testsuite/>",
                StandardCharsets.UTF_8);
        assertThat(FullCheck.watchdogStop(FullCheck.timerState(tempDir, System.nanoTime()), 1))
                .isEqualTo(new FullCheck.WatchdogStop(TIMED_OUT,
                        "Tests exceeded 1 seconds. The owned process tree was stopped."));

        Path correctnessReport = tempDir.resolve("TEST-com.jinloes.practice.CorrectnessTest.xml");
        Files.writeString(correctnessReport, "<testsuite/>", StandardCharsets.UTF_8);
        java.util.concurrent.atomic.AtomicBoolean stopped = new java.util.concurrent.atomic.AtomicBoolean();
        FullCheck fullCheck = new FullCheck(null, null, ignored -> {}, () -> stopped.set(true),
                (attempt, fingerprint, result) -> {});
        RunSession.RunControl transitionControl = new RunSession.RunControl();
        fullCheck.runWatchdogTick(new InMemoryProcessHandler(false), transitionControl, tempDir, 0, 1);
        assertThat(stopped).isFalse();
        assertThat(transitionControl.stopReason()).isNull();

        Files.setLastModifiedTime(correctnessReport,
                java.nio.file.attribute.FileTime.fromMillis(System.currentTimeMillis() - 31_000));
        assertThat(FullCheck.watchdogStop(FullCheck.timerState(tempDir, System.nanoTime()), 1))
                .isEqualTo(new FullCheck.WatchdogStop(RUNNER_ERROR,
                        "Advisory probe coordinator exceeded 30 seconds. The owned process tree was stopped."));

        Files.writeString(tempDir.resolve("phase"), "probing", StandardCharsets.UTF_8);
        assertThat(FullCheck.watchdogStop(FullCheck.timerState(tempDir, System.nanoTime()), 1)).isNull();
        assertThat(FullCheck.watchdogStop(new FullCheck.TimerState(true, 30_001, "probing"), 1))
                .isEqualTo(new FullCheck.WatchdogStop(RUNNER_ERROR,
                        "Advisory probe coordinator exceeded 30 seconds. The owned process tree was stopped."));
        assertThat(FullCheck.watchdogStop(new FullCheck.TimerState(true, 30_000, "probing"), 1)).isNull();

        java.util.concurrent.atomic.AtomicInteger reads = new java.util.concurrent.atomic.AtomicInteger();
        FullCheck.TimerState retried = FullCheck.readTimerWithRetry(() -> {
            if (reads.getAndIncrement() < 2) {
                throw new java.nio.file.FileSystemException("phase", null, "transient sharing violation");
            }
            return new FullCheck.TimerState(true, 10, "probing");
        });
        assertThat(retried.phase()).isEqualTo("probing");
        assertThatThrownBy(() -> FullCheck.readTimerWithRetry(() -> {
            throw new java.nio.file.FileSystemException("phase", null, "persistent sharing violation");
        })).isInstanceOf(java.nio.file.FileSystemException.class);
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
        assertThat(FullCheck.withMeasuredComplexity(passed, "O(n)", "O(1)", tempDir).complexity())
                .contains("Scaling measurement unavailable", "did not produce a report");

        Files.writeString(tempDir.resolve(ComplexityAnalysis.FILE), "x".repeat(64_001), StandardCharsets.UTF_8);
        assertThat(FullCheck.withMeasuredComplexity(passed, "O(n)", "O(1)", tempDir).complexity())
                .contains("Scaling measurement unavailable", "size limit");

        assertThat(FullCheck.withMeasuredComplexity(failed, "O(n)", "O(1)", tempDir)).isSameAs(failed);
    }

    @Test
    void finishReportsResultProcessingAndWorkspaceCleanupFailures() throws Exception {
        var workspace = new com.jinloes.practice_plugin.workspace.ManagedPracticeWorkspace();
        var exercise = com.jinloes.practice_plugin.catalog.ExerciseCatalog.find("pair-sum");
        var attempt = workspace.create(exercise);
        var attempt2 = workspace.create(exercise);
        try {
            String fingerprint = workspace.fingerprint(attempt);
            var verification1 = com.jinloes.practice_plugin.workspace.VerificationWorkspace.create(
                    attempt, exercise, fingerprint);
            Files.delete(attempt.solution());
            var recorded1 = new java.util.concurrent.atomic.AtomicReference<CheckResult>();
            new FullCheck(null, workspace, ignored -> {}, () -> {},
                    (finishedAttempt, fp, result) -> recorded1.set(result))
                    .finish(attempt, exercise, fingerprint, verification1, new RunSession.RunControl(), 0);
            assertThat(recorded1.get().status()).isEqualTo(RUNNER_ERROR);
            assertThat(verification1.root()).doesNotExist();

            String fingerprint2 = workspace.fingerprint(attempt2);
            var verification2 = com.jinloes.practice_plugin.workspace.VerificationWorkspace.create(
                    attempt2, exercise, fingerprint2);
            try {
                Files.delete(verification2.root().resolve(".algorithm-practice-verification"));
                RunSession.RunControl cancelled = new RunSession.RunControl();
                cancelled.requestCancel();
                var recorded2 = new java.util.concurrent.atomic.AtomicReference<CheckResult>();
                new FullCheck(null, workspace, ignored -> {}, () -> {},
                        (finishedAttempt, fp, result) -> recorded2.set(result))
                        .finish(attempt2, exercise, fingerprint2, verification2, cancelled, 0);
                assertThat(recorded2.get().status()).isEqualTo(RUNNER_ERROR);
                assertThat(recorded2.get().details()).startsWith("Verification cleanup failed:");
            } finally {
                com.intellij.openapi.util.io.NioFiles.deleteRecursively(verification2.root());
            }
        } finally {
            com.intellij.openapi.util.io.NioFiles.deleteRecursively(attempt.directory());
            com.intellij.openapi.util.io.NioFiles.deleteRecursively(attempt2.directory());
        }
    }

    @Test
    void launchCleanupHandlesListenerDetachmentInterruptionAndRejectedExecutors() throws Exception {
        var handler = new InMemoryProcessHandler(true);
        var listener = new com.intellij.execution.process.ProcessListener() {};
        handler.addProcessListener(listener);
        FullCheckLaunchCleanup.releaseFailedLaunch(
                null, null, handler, listener, new RunSession.RunControl(), new IOException("failed"));
        FullCheckLaunchCleanup.releaseFailedLaunch(
                null, null, new InMemoryProcessHandler(true), null, new RunSession.RunControl(), new IOException("failed"));

        Process deadProcess = new Process() {
            @Override public OutputStream getOutputStream() { return OutputStream.nullOutputStream(); }
            @Override public java.io.InputStream getInputStream() { return java.io.InputStream.nullInputStream(); }
            @Override public java.io.InputStream getErrorStream() { return java.io.InputStream.nullInputStream(); }
            @Override public int waitFor() { return 0; }
            @Override public int exitValue() { return 0; }
            @Override public void destroy() {}
            @Override public boolean isAlive() { return false; }
        };

        IOException rejectedOriginal = new IOException("launch");
        java.util.concurrent.ExecutorService shutDown = java.util.concurrent.Executors.newSingleThreadExecutor();
        shutDown.shutdownNow();
        FullCheckLaunchCleanup.releaseFailedHandler(shutDown, handler, deadProcess, rejectedOriginal);
        assertThat(rejectedOriginal.getSuppressed()).hasSize(1);
        assertThat(rejectedOriginal.getSuppressed()[0])
                .isInstanceOf(java.util.concurrent.RejectedExecutionException.class);

        var enteredWait = new java.util.concurrent.CountDownLatch(1);
        var releaseWait = new java.util.concurrent.CountDownLatch(1);
        var blockingHandler = new ProcessHandler() {
            @Override protected void destroyProcessImpl() {}
            @Override protected void detachProcessImpl() {}
            @Override public boolean detachIsDefault() { return false; }
            @Override public OutputStream getProcessInput() { return null; }
            @Override public boolean waitFor(long timeoutInMilliseconds) {
                enteredWait.countDown();
                try {
                    releaseWait.await(5, TimeUnit.SECONDS);
                } catch (InterruptedException ignored) {
                }
                return true;
            }
        };
        Thread caller = Thread.currentThread();
        Thread interrupter = new Thread(() -> {
            try {
                if (enteredWait.await(5, TimeUnit.SECONDS)) {
                    caller.interrupt();
                }
            } catch (InterruptedException ignored) {
            }
        });
        IOException interruptedOriginal = new IOException("launch");
        interrupter.start();
        try {
            FullCheckLaunchCleanup.releaseFailedHandler(blockingHandler, deadProcess, interruptedOriginal);
            assertThat(Thread.interrupted()).isTrue();
            assertThat(interruptedOriginal.getSuppressed()).hasSize(1);
            assertThat(interruptedOriginal.getSuppressed()[0]).isInstanceOf(InterruptedException.class);
        } finally {
            releaseWait.countDown();
            interrupter.join(5_000);
            Thread.interrupted();
        }
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
