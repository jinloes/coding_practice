package com.jinloes.practice_plugin.run;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.ExecutionListener;
import com.intellij.execution.ExecutionManager;
import com.intellij.execution.RunManager;
import com.intellij.execution.RunnerAndConfigurationSettings;
import com.intellij.execution.application.ApplicationConfiguration;
import com.intellij.execution.application.ApplicationConfigurationType;
import com.intellij.execution.configurations.ConfigurationTypeUtil;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.executors.DefaultDebugExecutor;
import com.intellij.execution.executors.DefaultRunExecutor;
import com.intellij.execution.process.KillableColoredProcessHandler;
import com.intellij.execution.process.OSProcessHandler;
import com.intellij.execution.process.ProcessEvent;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.process.ProcessListener;
import com.intellij.execution.process.ProcessOutputTypes;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.runners.ExecutionUtil;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.WriteIntentReadAction;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiManager;
import com.intellij.util.concurrency.AppExecutorUtil;
import com.jinloes.practice_plugin.catalog.ExerciseCatalog;
import com.jinloes.practice_plugin.state.ManagedPracticeProgress;
import com.jinloes.practice_plugin.workspace.ManagedPracticeWorkspace;
import com.jinloes.practice_plugin.workspace.PracticeModuleWorkspace;
import com.jinloes.practice_plugin.workspace.VerificationWorkspace;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.TestOnly;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import static com.jinloes.practice_plugin.run.CheckResult.Status.CANCELLED;
import static com.jinloes.practice_plugin.run.CheckResult.Status.COMPILATION_FAILED;
import static com.jinloes.practice_plugin.run.CheckResult.Status.RUNNER_ERROR;
import static com.jinloes.practice_plugin.run.CheckResult.Status.TIMED_OUT;

@Service(Service.Level.PROJECT)
public final class PracticeRunner implements Disposable {
    private final Project project;
    private final ManagedPracticeWorkspace attempts = ManagedPracticeWorkspace.get();
    /** The one piece of run lifecycle state. Only its owner may transition it. */
    private final AtomicReference<RunSession> session = new AtomicReference<>(RunSession.IDLE);
    /**
     * Settings created by {@link #check(String)} and handed to the {@link #startCheck(String)} the
     * IDE calls back, which is the only place that can claim the guard for them.
     */
    private final AtomicReference<RunnerAndConfigurationSettings> pendingCheckSettings = new AtomicReference<>();
    /**
     * Dispatches the post-save continuation when something other than the IDE's own write-thread
     * dispatch must run it. Null is the real one: the IDE dispatch used in production.
     */
    private final Consumer<Runnable> continuationDispatcher;
    /**
     * Everything currently showing run output. A tool window that is closed and reopened creates a
     * new panel, so registrations must be removable or each cycle leaks the previous panel.
     */
    private final List<Consumer<String>> listeners = new CopyOnWriteArrayList<>();

    public PracticeRunner(Project project) {
        this(project, null);
    }

    @TestOnly
    PracticeRunner(Project project, Consumer<Runnable> continuationDispatcher) {
        this.project = project;
        this.continuationDispatcher = continuationDispatcher;
        project.getMessageBus().connect(this).subscribe(ExecutionManager.EXECUTION_TOPIC, new ExecutionListener() {
            @Override
            public void processStarted(@NotNull String executorId, @NotNull ExecutionEnvironment environment,
                                       @NotNull ProcessHandler handler) {
                RunSession.ExampleSession examples = examplesFor(environment);
                if (examples == null) {
                    return;
                }
                RunSession.RunControl control = examples.control();
                control.active(handler);
                if (control.stopReason() == CANCELLED) {
                    captureOwnedChildren(handler, control);
                    control.terminateOwnedChildren();
                    handler.destroyProcess();
                    return;
                }
                String verb = examples.debug() ? "Debugging" : "Running";
                message(examples.input().isEmpty()
                        ? verb + " visible examples."
                        : verb + " the solution on: " + examples.input());
            }

            @Override
            public void processNotStarted(@NotNull String executorId, @NotNull ExecutionEnvironment environment) {
                if (examplesFor(environment) != null) {
                    finishExamples("Example launch did not complete. See the IDE notification and Run console.");
                }
            }

            @Override
            public void processTerminated(@NotNull String executorId, @NotNull ExecutionEnvironment environment,
                                          @NotNull ProcessHandler handler, int exitCode) {
                RunSession.ExampleSession examples = examplesFor(environment);
                if (examples != null) {
                    finishExamples((examples.input().isEmpty() ? "Example session" : "Input session")
                            + " finished (exit " + exitCode
                            + "). Use Check Solution to update correctness progress.");
                }
            }
        });
    }

    /** The example run this environment belongs to, or null when the event is not ours. */
    private RunSession.ExampleSession examplesFor(ExecutionEnvironment environment) {
        return session.get() instanceof RunSession.ExampleSession examples
                && environment.getRunProfile() == examples.configuration()
                ? examples : null;
    }

    /** Registers a listener. The caller must {@link #removeListener} it when it is disposed. */
    public void addListener(Consumer<String> listener) {
        listeners.add(listener);
    }

    public void removeListener(Consumer<String> listener) {
        listeners.remove(listener);
    }

    @TestOnly
    public int listenerCount() {
        return listeners.size();
    }

    public boolean isRunning() {
        return !(session.get() instanceof RunSession.Idle);
    }

    public void runExamples(String attemptId) throws ExecutionException {
        launchExamples(attemptId, "", false);
    }

    public void runExamples(String attemptId, String input) throws ExecutionException {
        launchExamples(attemptId, input, false);
    }

    public void debugExamples(String attemptId) throws ExecutionException {
        launchExamples(attemptId, "", true);
    }

    public void debugExamples(String attemptId, String input) throws ExecutionException {
        launchExamples(attemptId, input, true);
    }

    public void check(String attemptId) throws ExecutionException {
        ensureIdle();
        runAfterSavingDocuments(() -> {
            RunManager manager = RunManager.getInstance(project);
            var type = ConfigurationTypeUtil.findConfigurationType(PracticeConfigurationType.class);
            RunnerAndConfigurationSettings settings = manager.createConfiguration(
                    "Check " + attemptId, type.getConfigurationFactories()[0]);
            var configuration = (PracticeRunConfiguration) settings.getConfiguration();
            configuration.attemptId = attemptId;
            manager.setTemporaryConfiguration(settings);
            PracticeModuleWorkspace.get(project).track(settings);
            pendingCheckSettings.set(settings);
            manager.setSelectedConfiguration(settings);
            ExecutionUtil.runConfiguration(settings, DefaultRunExecutor.getRunExecutorInstance());
        });
    }

    ProcessHandler startCheck(String attemptId) throws ExecutionException {
        RunSession.RunControl control = new RunSession.RunControl();
        if (!session.compareAndSet(RunSession.IDLE, new RunSession.CheckSession(attemptId, null, control))) {
            throw new ExecutionException("A practice run is already active. Stop it before starting another.");
        }
        // The guard is held now, so the settings this check must clean up can be taken over safely.
        session.set(new RunSession.CheckSession(attemptId, pendingCheckSettings.getAndSet(null), control));
        VerificationWorkspace workspace = null;
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
                    .withEnvironment("JAVA_HOME", home.toString())
                    .withEnvironment("GRADLE_USER_HOME", workspace.gradleHome().toString());
            VerificationWorkspace currentWorkspace = workspace;
            var handler = new BoundedProcessHandler(command, () -> {
                control.requestCancel();
                control.terminateOwnedChildren();
            });
            handler.setShouldKillProcessSoftly(false);
            currentWorkspace.markProcess(handler.getProcess().pid(), fingerprint);
            control.active(handler);
            long setupStarted = System.nanoTime();
            handler.addProcessListener(new ProcessListener() {
                @Override
                public void processTerminated(@NotNull ProcessEvent event) {
                    control.cancelWatchdog();
                    control.terminateOwnedChildren();
                    AppExecutorUtil.getAppExecutorService().execute(() -> finishCheckProcess(
                            attempt, exercise, fingerprint, currentWorkspace, control, event.getExitCode()));
                }
            });
            scheduleWatchdog(handler, control, currentWorkspace.resultDirectory(), setupStarted,
                    progress.getState().suiteSeconds);
            message("Managed attempt: " + attemptId + "\nRunning the isolated full correctness suite.\n"
                    + "Setup limit: 5 minutes; test execution limit: "
                    + progress.getState().suiteSeconds + " seconds.");
            return handler;
        } catch (IOException | ExecutionException | IllegalArgumentException exception) {
            if (workspace != null) {
                try {
                    workspace.markFinished("launch-failed");
                    workspace.cleanupAfterRun();
                } catch (IOException ignored) {
                    // Preserve uncertain workspaces.
                }
            }
            session.set(RunSession.IDLE);
            throw new ExecutionException("Cannot start managed check: " + exception.getMessage(), exception);
        }
    }

    /**
     * Attaches the scaling measurement to a passing result. The probe only runs after the suite
     * passes, and a missing or unreadable report never changes the verdict.
     */
    private static CheckResult withMeasuredComplexity(
            CheckResult result,
            ExerciseCatalog.Exercise exercise,
            VerificationWorkspace workspace
    ) {
        if (result.status() != CheckResult.Status.PASSED) {
            return result;
        }
        try {
            return ComplexityAnalysis.read(workspace.resultDirectory())
                    .map(report -> result.withComplexity(
                            report.render(exercise.intendedTime(), exercise.intendedSpace())))
                    .orElse(result);
        } catch (IOException | RuntimeException failure) {
            return result.withComplexity("Scaling measurement unavailable: " + failure.getMessage());
        }
    }

    private void finishCheckProcess(
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
                String phase = Files.isRegularFile(workspace.resultDirectory().resolve("phase"))
                        ? Files.readString(workspace.resultDirectory().resolve("phase")) : "setup";
                result = exitCode != 0 && phase.equals("compiling")
                        ? new CheckResult(COMPILATION_FAILED, 0, 0,
                        "Compilation failed. See the Run console for file and line diagnostics.")
                        : TestReports.read(workspace.resultDirectory(), exercise.fullCount(), exitCode);
                result = withMeasuredComplexity(result, exercise, workspace);
            }
            if (!fingerprint.equals(attempts.fingerprint(attempt))) {
                result = staleResult(result);
            }
        } catch (IOException | UncheckedIOException | IllegalArgumentException exception) {
            result = new CheckResult(RUNNER_ERROR, 0, 0, exception.getMessage());
        } finally {
            try {
                workspace.markFinished(fingerprint);
                workspace.cleanupAfterRun();
            } catch (IOException cleanupFailure) {
                result = new CheckResult(RUNNER_ERROR, result.tests(), result.failures(),
                        "Verification cleanup failed: " + cleanupFailure.getMessage());
            }
            finishCheck(attempt, fingerprint, result);
        }
    }

    private void launchExamples(String attemptId, String input, boolean debug) throws ExecutionException {
        ensureIdle();
        String argument = input == null ? "" : input.trim();
        runAfterSavingDocuments(() -> launchExamplesAfterSave(attemptId, argument, debug));
    }

    private void launchExamplesAfterSave(String attemptId, String input, boolean debug)
            throws ExecutionException {
        ManagedPracticeWorkspace.Attempt attempt;
        try {
            attempt = attempts.read(attemptId);
        } catch (IOException exception) {
            throw new ExecutionException(exception.getMessage(), exception);
        }
        PracticeModuleWorkspace modules = PracticeModuleWorkspace.get(project);
        modules.open(attempt);
        Path runner = modules.prepareExampleHarness(attempt, ExerciseCatalog.find(attempt.exerciseId()));
        String fingerprint;
        try {
            fingerprint = attempts.fingerprint(attempt);
        } catch (IOException exception) {
            modules.clearExampleHarness(attempt);
            throw new ExecutionException(exception.getMessage(), exception);
        }
        RunSession.ExampleSession claimed = new RunSession.ExampleSession(
                attempt, input, debug, fingerprint, null, null, new RunSession.RunControl());
        if (!session.compareAndSet(RunSession.IDLE, claimed)) {
            modules.clearExampleHarness(attempt);
            throw new ExecutionException("A practice run is already active. Stop it before starting another.");
        }
        AppExecutorUtil.getAppExecutorService().execute(() -> {
            try {
                modules.compileExamples(attempt, runner);
            } catch (ExecutionException exception) {
                finishExamples(exception.getMessage());
                return;
            }
            ApplicationManager.getApplication().invokeLaterOnWriteThread(() ->
                    installExampleConfiguration(attempt, attemptId, runner, input, debug));
        });
    }

    private void installExampleConfiguration(
            ManagedPracticeWorkspace.Attempt attempt,
            String attemptId,
            Path runner,
            String input,
            boolean debug
    ) {
        try {
            WriteIntentReadAction.runThrowable(() -> {
                if (project.isDisposed()
                        || !(session.get() instanceof RunSession.ExampleSession current)
                        || current.control().stopReason() == CANCELLED) {
                    finishExamples("Example launch cancelled.");
                    return;
                }
                requireModelAccess();
                var expectedModule = PracticeModuleWorkspace.get(project).open(attempt);
                var runnerFile = LocalFileSystem.getInstance().refreshAndFindFileByNioFile(runner);
                var psiFile = runnerFile == null ? null : PsiManager.getInstance(project).findFile(runnerFile);
                var module = runnerFile == null ? null : ModuleUtilCore.findModuleForFile(runnerFile, project);
                if (!(psiFile instanceof PsiJavaFile javaFile) || javaFile.getClasses().length != 1
                        || module == null || module.isDisposed() || module != expectedModule) {
                    finishExamples("Generated example runner is not owned by the attempt module.");
                    return;
                }
                RunManager manager = RunManager.getInstance(project);
                RunnerAndConfigurationSettings settings = manager.createConfiguration(
                        (debug ? "Debug " : "Run ") + (input.isEmpty() ? "" : "input ") + attemptId,
                        ApplicationConfigurationType.getInstance().getConfigurationFactories()[0]);
                var configuration = (ApplicationConfiguration) settings.getConfiguration();
                configuration.setMainClass(javaFile.getClasses()[0]);
                configuration.setModule(module);
                configuration.setWorkingDirectory(attempt.directory().toString());
                configuration.setProgramParameters(input.isEmpty() ? null : quoteArgument(input));
                manager.setTemporaryConfiguration(settings);
                manager.setSelectedConfiguration(settings);
                session.set(current.installed(configuration, settings));
                try {
                    ExecutionUtil.runConfiguration(settings, debug
                            ? DefaultDebugExecutor.getDebugExecutorInstance()
                            : DefaultRunExecutor.getRunExecutorInstance());
                } catch (RuntimeException failure) {
                    finishExamples("Example launch failed before execution: " + failure.getMessage());
                    throw failure;
                }
            });
        } catch (ExecutionException exception) {
            finishExamples("Example launch failed before execution: " + exception.getMessage());
        }
    }

    public void stop() {
        if (!(session.get() instanceof RunSession.Active running)) {
            return;
        }
        RunSession.RunControl control = running.control();
        control.requestCancel();
        ProcessHandler handler = control.active();
        if (handler != null && !handler.isProcessTerminated()) {
            captureOwnedChildren(handler, control);
            control.terminateOwnedChildren();
            handler.destroyProcess();
        }
    }

    private void scheduleWatchdog(ProcessHandler handler, RunSession.RunControl control, Path resultRoot,
                                  long setupStarted, int suiteSeconds) {
        control.watchdog(AppExecutorUtil.getAppScheduledExecutorService().scheduleWithFixedDelay(() -> {
            if (handler.isProcessTerminated()) {
                return;
            }
            captureOwnedChildren(handler, control);
            try {
                Path started = resultRoot.resolve("started");
                boolean expired = Files.isRegularFile(started)
                        ? System.currentTimeMillis() - Files.getLastModifiedTime(started).toMillis() > suiteSeconds * 1000L
                        : System.nanoTime() - setupStarted > TimeUnit.MINUTES.toNanos(5);
                if (expired) {
                    control.requestStop(TIMED_OUT, Files.isRegularFile(started)
                            ? "Tests exceeded " + suiteSeconds + " seconds. The owned process tree was stopped."
                            : "Setup/compilation exceeded five minutes. Check SDK, network, and Run output.");
                    stop();
                }
            } catch (IOException exception) {
                control.requestStop(RUNNER_ERROR, "Cannot inspect execution timer: " + exception.getMessage());
                stop();
            }
        }, 0, 250, TimeUnit.MILLISECONDS));
        if (handler.isProcessTerminated()) {
            control.cancelWatchdog();
        }
    }

    private void finishCheck(ManagedPracticeWorkspace.Attempt attempt, String fingerprint, CheckResult result) {
        CheckResult completed = result;
        ApplicationManager.getApplication().invokeLaterOnWriteThread(() ->
                WriteIntentReadAction.run(() -> {
                    RunnerAndConfigurationSettings settings =
                            session.getAndSet(RunSession.IDLE) instanceof RunSession.CheckSession check
                                    ? check.settings() : null;
                    if (settings != null && !project.isDisposed()) {
                        requireModelAccess();
                        RunManager.getInstance(project).removeConfiguration(settings);
                        PracticeModuleWorkspace.get(project).untrack(settings);
                    }
                    if (!project.isDisposed()) {
                        ManagedPracticeProgress.get().record(
                                attempt.id(), attempt.exerciseId(), fingerprint, completed);
                        message("Managed attempt: " + attempt.id() + "\nFull check: " + completed.status()
                                + "\n" + completed.details()
                                + (completed.complexity().isBlank() ? "" : "\n\n" + completed.complexity()));
                    }
                }));
    }

    private void finishExamples(String text) {
        ApplicationManager.getApplication().invokeLaterOnWriteThread(() ->
                WriteIntentReadAction.run(() -> {
                    String resultText = text;
                    ManagedPracticeWorkspace.Attempt attempt = null;
                    String fingerprint = null;
                    RunnerAndConfigurationSettings settings = null;
                    if (session.getAndSet(RunSession.IDLE) instanceof RunSession.ExampleSession examples) {
                        attempt = examples.attempt();
                        fingerprint = examples.fingerprint();
                        settings = examples.settings();
                    }
                    if (settings != null && !project.isDisposed()) {
                        requireModelAccess();
                        RunManager.getInstance(project).removeConfiguration(settings);
                    }
                    if (attempt != null && !project.isDisposed()) {
                        PracticeModuleWorkspace.get(project).clearExampleHarness(attempt);
                        if (fingerprint != null && resultText.contains("session finished (exit 0)")) {
                            try {
                                if (!fingerprint.equals(attempts.fingerprint(attempt))) {
                                    resultText = "STALE: files changed during the example run; run the current solution again.";
                                }
                            } catch (IOException exception) {
                                resultText = "Example result is unavailable: " + exception.getMessage();
                            }
                        }
                    }
                    message(resultText);
                }
        ));
    }

    private static String quoteArgument(String input) {
        return '"' + input.replace("\\", "\\\\").replace("\"", "\\\"") + '"';
    }

    private CheckResult stoppedResult(RunSession.RunControl control) {
        return new CheckResult(control.stopReason(), 0, 0, control.stopDetails() != null ? control.stopDetails()
                : "Run cancelled. No passing result was recorded.");
    }

    private static CheckResult staleResult(CheckResult result) {
        return new CheckResult(RUNNER_ERROR, result.tests(), result.failures(),
                "STALE: files changed during this run. Check the current solution again.\n" + result.details());
    }

    private static void captureOwnedChildren(ProcessHandler handler, RunSession.RunControl control) {
        if (handler instanceof OSProcessHandler process) {
            process.getProcess().descendants().forEach(control::capture);
        }
    }

    private void ensureIdle() throws ExecutionException {
        if (isRunning()) {
            throw new ExecutionException("A practice run is already active. Stop it before starting another.");
        }
    }

    private void runAfterSavingDocuments(ExecutionAction action) throws ExecutionException {
        Consumer<Runnable> dispatcher = continuationDispatcher;
        if (dispatcher != null) {
            ApplicationManager.getApplication().invokeLaterOnWriteThread(() ->
                    WriteIntentReadAction.run(() -> {
                        FileDocumentManager.getInstance().saveAllDocuments();
                        dispatcher.accept(() -> continueWithWriteIntent(action));
                    }));
            return;
        }
        if (ApplicationManager.getApplication().isDispatchThread()
                && ApplicationManager.getApplication().isWriteIntentLockAcquired()) {
            saveAndContinueWithWriteIntent(action);
            return;
        }
        ApplicationManager.getApplication().invokeLaterOnWriteThread(() -> {
            try {
                saveAndContinueWithWriteIntent(action);
            } catch (ExecutionException exception) {
                message("Cannot start practice execution: " + exception.getMessage());
            }
        });
    }

    private static void saveAndContinueWithWriteIntent(ExecutionAction action) throws ExecutionException {
        WriteIntentReadAction.runThrowable(() -> {
            FileDocumentManager.getInstance().saveAllDocuments();
            action.run();
        });
    }

    private void continueWithWriteIntent(ExecutionAction action) {
        try {
            WriteIntentReadAction.runThrowable(action::run);
        } catch (ExecutionException exception) {
            message("Cannot start practice execution: " + exception.getMessage());
        }
    }

    private static void requireModelAccess() {
        ApplicationManager.getApplication().assertReadAccessAllowed();
        ApplicationManager.getApplication().assertWriteIntentLockAcquired();
    }

    @FunctionalInterface
    private interface ExecutionAction {
        void run() throws ExecutionException;
    }

    private void message(String text) {
        ApplicationManager.getApplication().invokeLater(() -> {
            if (!project.isDisposed()) {
                listeners.forEach(each -> each.accept(text));
            }
        });
    }

    private static final class BoundedProcessHandler extends KillableColoredProcessHandler {
        private static final int LIMIT = 128 * 1024;
        private final Runnable cancelled;
        private int captured;

        private BoundedProcessHandler(GeneralCommandLine command, Runnable cancelled) throws ExecutionException {
            super(command);
            this.cancelled = cancelled;
        }

        @Override
        public void destroyProcess() {
            cancelled.run();
            super.destroyProcess();
        }

        @Override
        public void killProcess() {
            cancelled.run();
            super.killProcess();
        }

        @Override
        public synchronized void coloredTextAvailable(@NotNull String text, @NotNull Key outputType) {
            if (captured >= LIMIT) {
                return;
            }
            int length = Math.min(text.length(), LIMIT - captured);
            super.coloredTextAvailable(text.substring(0, length), outputType);
            captured += length;
            if (captured == LIMIT) {
                super.coloredTextAvailable("\n[Practice console truncated at 128 KiB.]\n", ProcessOutputTypes.SYSTEM);
            }
        }
    }

    @Override
    public void dispose() {
        RunSession current = session.get();
        stop();
        if (current instanceof RunSession.Active running) {
            running.control().cancelWatchdog();
        }
    }
}
