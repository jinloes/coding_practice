package com.jinloes.practice_plugin.run;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.process.ProcessEvent;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.process.ProcessListener;
import com.intellij.openapi.diagnostic.ControlFlowException;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.intellij.util.concurrency.AppExecutorUtil;
import com.jinloes.practice_plugin.catalog.ExerciseCatalog;
import com.jinloes.practice_plugin.state.ManagedPracticeProgress;
import com.jinloes.practice_plugin.workspace.ManagedPracticeWorkspace;
import com.jinloes.practice_plugin.workspace.PracticeModuleWorkspace;
import com.jinloes.practice_plugin.workspace.VerificationWorkspace;
import org.jetbrains.annotations.TestOnly;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.FileSystemException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;
import java.util.function.Consumer;

import static com.jinloes.practice_plugin.run.CheckResult.Status.*;
import static com.jinloes.practice_plugin.run.FullCheckLaunchCleanup.releaseFailedLaunch;

/**
 * Launches one full correctness check in an isolated verification workspace, enforces its setup and
 * suite limits, and turns the finished process into a {@link CheckResult}.
 *
 * <p>It owns no run-guard state. {@link PracticeRunner} claims the guard before calling
 * {@link #start}, and releases it from the {@link Completion} this class invokes exactly once after
 * the process ends.
 */
final class FullCheck {
    @TestOnly
    static final Key<Consumer<GeneralCommandLine>> COMMAND_SETUP =
            Key.create("algorithm-practice.full-check-command-setup");
    private static final long SETUP_LIMIT_NANOS = TimeUnit.SECONDS.toNanos(60);
    private static final long PROBE_COORDINATOR_LIMIT_MILLIS = 30_000;

    /**
     * Receives the final result of a check whose process started.
     */
    @FunctionalInterface
    interface Completion {
        void finished(ManagedPracticeWorkspace.Attempt attempt, String fingerprint, CheckResult result);
    }

    private final Project project;
    private final ManagedPracticeWorkspace attempts;
    private final Consumer<String> message;
    private final Runnable stop;
    private final Completion completion;

    FullCheck(Project project, ManagedPracticeWorkspace attempts, Consumer<String> message, Runnable stop,
              Completion completion) {
        this.project = project;
        this.attempts = attempts;
        this.message = message;
        this.stop = stop;
        this.completion = completion;
    }

    /**
     * Starts the check process. On failure any created workspace is marked and cleaned up, the
     * completion is not invoked, and the caller must release the guard.
     */
    ProcessHandler start(String attemptId, RunSession.RunControl control) throws IOException, ExecutionException {
        VerificationWorkspace workspace = null;
        Process acquired = null;
        BoundedProcessHandler acquiredHandler = null;
        ProcessListener completionListener = null;
        try {
            FileDocumentManager.getInstance().saveAllDocuments();
            ManagedPracticeWorkspace.Attempt attempt = attempts.read(attemptId);
            var exercise = ExerciseCatalog.find(attempt.exerciseId());
            String fingerprint = attempts.fingerprint(attempt);
            ManagedPracticeProgress progress = ManagedPracticeProgress.get();
            progress.validateLimits();
            Path home = PracticeModuleWorkspace.get(project).javaHome();
            workspace = VerificationWorkspace.create(attempt, exercise, fingerprint);
            var command = new GeneralCommandLine(workspace.command(
                    home, progress.getState().testSeconds, progress.getState().heapMb))
                    .withWorkingDirectory(workspace.root())
                    .withEnvironment("JAVA_HOME", home.toString());
            Consumer<GeneralCommandLine> setup = project.getUserData(COMMAND_SETUP);
            if (setup != null) {
                setup.accept(command);
            }
            VerificationWorkspace currentWorkspace = workspace;
            var handler = new BoundedProcessHandler(command, () -> {
                control.requestCancel();
                control.terminateOwnedChildren();
            });
            acquiredHandler = handler;
            acquired = handler.getProcess();
            handler.setShouldKillProcessSoftly(false);
            currentWorkspace.markProcess(handler.getProcess().pid(), fingerprint);
            control.active(handler);
            long setupStarted = System.nanoTime();
            completionListener = new ProcessListener() {
                @Override
                public void processTerminated(ProcessEvent event) {
                    control.cancelWatchdog();
                    control.terminateOwnedChildren();
                    AppExecutorUtil.getAppExecutorService().execute(() -> finish(
                            attempt, exercise, fingerprint, currentWorkspace, control, event.getExitCode()));
                }
            };
            handler.addProcessListener(completionListener);
            message.accept("Managed attempt: " + attemptId + "\nRunning the isolated full correctness suite.\n"
                    + "Compilation limit: 60 seconds; test execution limit: "
                    + progress.getState().suiteSeconds + " seconds.");
            scheduleWatchdog(handler, control, currentWorkspace.resultDirectory(), setupStarted,
                    progress.getState().suiteSeconds);
            return handler;
        } catch (IOException | ExecutionException | IllegalArgumentException exception) {
            releaseFailedLaunch(workspace, acquired, acquiredHandler, completionListener, control, exception);
            throw exception;
        } catch (RuntimeException exception) {
            releaseFailedLaunch(workspace, acquired, acquiredHandler, completionListener, control, exception);
            if (exception instanceof ControlFlowException) {
                throw exception;
            }
            throw new ExecutionException(exception.getMessage(), exception);
        }
    }

    void scheduleWatchdog(ProcessHandler handler, RunSession.RunControl control, Path resultRoot,
                          long setupStarted, int suiteSeconds) {
        control.watchdog(AppExecutorUtil.getAppScheduledExecutorService().scheduleWithFixedDelay(() ->
                        runWatchdogTick(handler, control, resultRoot, setupStarted, suiteSeconds),
                0, 250, TimeUnit.MILLISECONDS));
        if (handler.isProcessTerminated()) {
            control.cancelWatchdog();
        }
    }

    void runWatchdogTick(ProcessHandler handler, RunSession.RunControl control, Path resultRoot,
                         long setupStarted, int suiteSeconds) {
        runWatchdogTick(handler, control, suiteSeconds, () -> timerState(resultRoot, setupStarted));
    }

    void runWatchdogTick(ProcessHandler handler, RunSession.RunControl control, int suiteSeconds, TimerReader timer) {
        if (handler.isProcessTerminated()) {
            return;
        }
        control.captureChildrenOf(handler);
        try {
            WatchdogStop expired = watchdogStop(readTimerWithRetry(timer), suiteSeconds);
            if (expired != null) {
                control.requestStop(expired.reason(), expired.details());
                stop.run();
            }
        } catch (IOException exception) {
            control.requestStop(RUNNER_ERROR, "Cannot inspect execution timer: " + exception.getMessage());
            stop.run();
        }
    }

    static TimerState readTimerWithRetry(TimerReader timer) throws IOException {
        for (int attempt = 0; ; attempt++) {
            try {
                return timer.read();
            } catch (FileSystemException contention) {
                if (attempt >= 2) {
                    throw contention;
                }
                LockSupport.parkNanos(TimeUnit.MILLISECONDS.toNanos(5));
            }
        }
    }

    static TimerState timerState(Path resultRoot, long setupStarted) throws IOException {
        String phase = phaseOrSetup(resultRoot);
        if ("probing".equals(phase)) {
            return new TimerState(true,
                    System.currentTimeMillis() - Files.getLastModifiedTime(resultRoot.resolve("phase")).toMillis(),
                    phase);
        }
        Path correctnessReport = resultRoot.resolve("TEST-com.jinloes.practice.CorrectnessTest.xml");
        if (Files.isRegularFile(resultRoot.resolve("TEST-com.jinloes.practice.ExamplesTest.xml"))
                && Files.isRegularFile(correctnessReport)) {
            return new TimerState(true,
                    System.currentTimeMillis() - Files.getLastModifiedTime(correctnessReport).toMillis(),
                    "probing");
        }
        Path started = resultRoot.resolve("started");
        boolean testsStarted = Files.isRegularFile(started);
        return new TimerState(testsStarted, testsStarted
                ? System.currentTimeMillis() - Files.getLastModifiedTime(started).toMillis()
                : System.nanoTime() - setupStarted);
    }

    static WatchdogStop watchdogStop(TimerState timer, int suiteSeconds) {
        if ("probing".equals(timer.phase())) {
            return timer.elapsed() > PROBE_COORDINATOR_LIMIT_MILLIS
                    ? new WatchdogStop(RUNNER_ERROR,
                    "Advisory probe coordinator exceeded 30 seconds. The owned process tree was stopped.")
                    : null;
        }
        return watchdogStop(timer.testsStarted(), timer.elapsed(), suiteSeconds);
    }

    static WatchdogStop watchdogStop(boolean testsStarted, long elapsed, int suiteSeconds) {
        if (testsStarted && elapsed > suiteSeconds * 1000L) {
            return new WatchdogStop(TIMED_OUT,
                    "Tests exceeded " + suiteSeconds + " seconds. The owned process tree was stopped.");
        }
        if (!testsStarted && elapsed > SETUP_LIMIT_NANOS) {
            return new WatchdogStop(TIMED_OUT,
                    "Compilation/setup exceeded 60 seconds. Check SDK and Run output.");
        }
        return null;
    }

    void finish(
            ManagedPracticeWorkspace.Attempt attempt,
            ExerciseCatalog.Exercise exercise,
            String fingerprint,
            VerificationWorkspace workspace,
            RunSession.RunControl control,
            int exitCode
    ) {
        CheckResult result = new CheckResult(RUNNER_ERROR, 0, 0,
                "Result processing failed. See the IDE log for diagnostics.");
        try {
            if (control.stopReason() != null) {
                result = stoppedResult(control);
            } else {
                String phase = phaseOrSetup(workspace.resultDirectory());
                result = resultForPhase(phase, exitCode,
                        () -> TestReports.read(workspace.resultDirectory(), exercise.fullCount(), exitCode));
                result = withMeasuredComplexity(result, exercise, workspace);
            }
            if (!fingerprint.equals(attempts.fingerprint(attempt))) {
                result = staleResult(result);
            }
        } catch (IOException | UncheckedIOException | IllegalArgumentException exception) {
            result = resultProcessingFailure(exception);
        } finally {
            try {
                workspace.markFinished(fingerprint);
                workspace.cleanupAfterRun();
            } catch (IOException cleanupFailure) {
                result = cleanupFailureResult(result, cleanupFailure);
            }
            completion.finished(attempt, fingerprint, result);
        }
    }

    static String phaseOrSetup(Path resultDirectory) throws IOException {
        Path phase = resultDirectory.resolve("phase");
        return Files.isRegularFile(phase) ? Files.readString(phase) : "setup";
    }

    static CheckResult resultForPhase(String phase, int exitCode, ReportReader reports) throws IOException {
        return exitCode != 0 && phase.equals("compiling")
                ? new CheckResult(COMPILATION_FAILED, 0, 0,
                "Compilation failed. See the Run console for file and line diagnostics.")
                : reports.read();
    }

    /**
     * Attaches the scaling measurement to a passing result. The probe only runs after the suite
     * passes, and a missing or unreadable report never changes the verdict.
     */
    static CheckResult withMeasuredComplexity(
            CheckResult result,
            ExerciseCatalog.Exercise exercise,
            VerificationWorkspace workspace
    ) {
        if (result.status() != CheckResult.Status.PASSED) {
            return result;
        }
        return withMeasuredComplexity(result, exercise.intendedTime(), exercise.intendedSpace(),
                workspace.resultDirectory());
    }

    static CheckResult withMeasuredComplexity(
            CheckResult result,
            String intendedTime,
            String intendedSpace,
            Path resultDirectory
    ) {
        if (result.status() != CheckResult.Status.PASSED) {
            return result;
        }
        try {
            return ComplexityAnalysis.read(resultDirectory)
                    .map(report -> result.withComplexity(
                            report.render(intendedTime, intendedSpace)))
                    .orElseGet(() -> result.withComplexity(
                            "Scaling measurement unavailable: the advisory probe did not produce a report."));
        } catch (IOException | RuntimeException failure) {
            return result.withComplexity("Scaling measurement unavailable: " + failure.getMessage());
        }
    }

    static CheckResult stoppedResult(RunSession.RunControl control) {
        return new CheckResult(control.stopReason(), 0, 0, control.stopDetails() != null ? control.stopDetails()
                : "Run cancelled. No passing result was recorded.");
    }

    static CheckResult staleResult(CheckResult result) {
        return new CheckResult(RUNNER_ERROR, result.tests(), result.failures(),
                "STALE: files changed during this run. Check the current solution again.\n" + result.details());
    }

    static CheckResult resultProcessingFailure(Exception exception) {
        return new CheckResult(RUNNER_ERROR, 0, 0, exception.getMessage());
    }

    static CheckResult cleanupFailureResult(CheckResult result, IOException cleanupFailure) {
        return new CheckResult(RUNNER_ERROR, result.tests(), result.failures(),
                "Verification cleanup failed: " + cleanupFailure.getMessage());
    }

    record TimerState(boolean testsStarted, long elapsed, String phase) {
        TimerState(boolean testsStarted, long elapsed) {
            this(testsStarted, elapsed, testsStarted ? "testing" : "setup");
        }
    }

    record WatchdogStop(CheckResult.Status reason, String details) {
    }

    @FunctionalInterface
    interface ReportReader {
        CheckResult read() throws IOException;
    }

    @FunctionalInterface
    interface TimerReader {
        TimerState read() throws IOException;
    }
}
