package com.jinloes.practice_plugin.run;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.ExecutionListener;
import com.intellij.execution.ExecutionManager;
import com.intellij.execution.RunManager;
import com.intellij.execution.RunnerAndConfigurationSettings;
import com.intellij.execution.application.ApplicationConfiguration;
import com.intellij.execution.executors.DefaultDebugExecutor;
import com.intellij.execution.executors.DefaultRunExecutor;
import com.intellij.execution.process.KillableProcessHandler;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.runners.ExecutionUtil;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.WriteIntentReadAction;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiManager;
import com.intellij.util.concurrency.AppExecutorUtil;
import com.jinloes.practice_plugin.catalog.ExerciseCatalog;
import com.jinloes.practice_plugin.state.ManagedPracticeProgress;
import com.jinloes.practice_plugin.workspace.ManagedPracticeWorkspace;
import com.jinloes.practice_plugin.workspace.PracticeModuleWorkspace;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.TestOnly;

import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import static com.jinloes.practice_plugin.run.RunDecisions.exampleFinishedText;
import static com.jinloes.practice_plugin.run.RunDecisions.exampleInstallPreflight;
import static com.jinloes.practice_plugin.run.RunDecisions.exampleResultText;
import static com.jinloes.practice_plugin.run.RunDecisions.exampleRunnerOwned;
import static com.jinloes.practice_plugin.run.RunDecisions.exampleStart;
import static com.jinloes.practice_plugin.run.RunDecisions.fullCheckMessage;
import static com.jinloes.practice_plugin.run.RunDecisions.normalizedInput;

@Service(Service.Level.PROJECT)
public final class PracticeRunner implements Disposable {
    private final Project project;
    private final ManagedPracticeWorkspace attempts = ManagedPracticeWorkspace.get();
    private final AtomicReference<RunSession> session = new AtomicReference<>(RunSession.IDLE);
    private final AtomicReference<RunnerAndConfigurationSettings> pendingCheckSettings = new AtomicReference<>();
    private final Consumer<Runnable> continuationDispatcher;
    private final RunEvent.Listeners listeners = new RunEvent.Listeners();

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
                handleExampleProcessStarted(examples, handler);
            }

            @Override
            public void processNotStarted(@NotNull String executorId, @NotNull ExecutionEnvironment environment) {
                handleExampleProcessNotStarted(examplesFor(environment));
                handleCheckProcessNotStarted(environment);
            }

            @Override
            public void processTerminated(@NotNull String executorId, @NotNull ExecutionEnvironment environment,
                                          @NotNull ProcessHandler handler, int exitCode) {
                RunSession.ExampleSession examples = examplesFor(environment);
                if (examples != null) {
                    finishExamples(exampleFinishedText(examples.input(), exitCode));
                }
            }
        });
    }

    void handleExampleProcessStarted(RunSession.ExampleSession examples, ProcessHandler handler) {
        RunSession.RunControl control = examples.control();
        control.active(handler);
        RunDecisions.ExampleStart start = exampleStart(examples);
        if (start.cancelled()) {
            stopOwnedExampleProcess(control, handler);
            return;
        }
        publish(new RunEvent.Started(RunEvent.attemptId(examples.attempt()), start.message()));
    }

    void handleExampleProcessNotStarted(RunSession.ExampleSession examples) {
        if (examples != null) {
            finishExamples("Example launch did not complete. See the IDE notification and Run console.");
        }
    }

    void handleCheckProcessNotStarted(ExecutionEnvironment environment) {
        RunnerAndConfigurationSettings pending = claimPendingCheck(environment.getRunnerAndConfigurationSettings());
        if (pending != null) {
            removeTemporaryConfiguration(pending);
            if (!isRunning()) {
                publish(new RunEvent.FailedToStart(
                        "Check launch did not complete. See the IDE notification and Run console."));
            }
        }
    }

    private RunnerAndConfigurationSettings claimPendingCheck(RunnerAndConfigurationSettings candidate) {
        return candidate != null && pendingCheckSettings.compareAndSet(candidate, null) ? candidate : null;
    }

    /** The example run this environment belongs to, or null when the event is not ours. */
    private RunSession.ExampleSession examplesFor(ExecutionEnvironment environment) {
        return session.get() instanceof RunSession.ExampleSession examples
                && environment.getRunProfile() == examples.configuration()
                ? examples : null;
    }

    public void addEventListener(Consumer<RunEvent> listener) {
        listeners.addEvent(listener);
    }

    public void removeEventListener(Consumer<RunEvent> listener) {
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
            RunnerAndConfigurationSettings settings = CheckRunConfigurations.install(project, attemptId);
            removeTemporaryConfiguration(pendingCheckSettings.getAndSet(settings));
            ExecutionUtil.runConfiguration(settings, DefaultRunExecutor.getRunExecutorInstance());
        });
    }

    ProcessHandler startCheck(String attemptId, RunnerAndConfigurationSettings launchedSettings)
            throws ExecutionException {
        RunnerAndConfigurationSettings owned = claimPendingCheck(launchedSettings);
        RunnerAndConfigurationSettings temp = owned != null ? owned
                : launchedSettings != null && launchedSettings.isTemporary() ? launchedSettings : null;
        RunSession.RunControl control = new RunSession.RunControl();
        if (!session.compareAndSet(RunSession.IDLE, new RunSession.CheckSession(attemptId, temp, control))) {
            removeTemporaryConfiguration(temp);
            throw new ExecutionException("A practice run is already active. Stop it before starting another.");
        }
        try {
            return new FullCheck(project, attempts, text -> publish(new RunEvent.Started(attemptId, text)),
                    this::stop, this::finishCheck).start(attemptId, control);
        } catch (IOException | ExecutionException | IllegalArgumentException exception) {
            session.set(RunSession.IDLE);
            removeTemporaryConfiguration(temp);
            String message = "Cannot start managed check: " + exception.getMessage();
            publish(new RunEvent.FailedToStart(message));
            throw new ExecutionException(message, exception);
        }
    }

    private void launchExamples(String attemptId, String input, boolean debug) throws ExecutionException {
        ensureIdle();
        String argument = normalizedInput(input);
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
        publish(new RunEvent.Started(attemptId, "Compiling examples."));
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
            ManagedPracticeWorkspace.Attempt attempt, String attemptId, Path runner, String input, boolean debug) {
        try {
            WriteIntentReadAction.runThrowable(() -> {
                RunDecisions.ExampleInstallPreflight preflight = exampleInstallPreflight(project.isDisposed(), session.get());
                if (!preflight.proceed()) {
                    finishExamples(preflight.message());
                    return;
                }
                RunSession.ExampleSession current = preflight.current();
                requireModelAccess();
                var expectedModule = PracticeModuleWorkspace.get(project).open(attempt);
                var runnerFile = LocalFileSystem.getInstance().refreshAndFindFileByNioFile(runner);
                var psiFile = runnerFile == null ? null : PsiManager.getInstance(project).findFile(runnerFile);
                var module = runnerFile == null ? null : ModuleUtilCore.findModuleForFile(runnerFile, project);
                if (!(psiFile instanceof PsiJavaFile javaFile) || !exampleRunnerOwned(javaFile, module, expectedModule)) {
                    finishExamples("Generated example runner is not owned by the attempt module.");
                    return;
                }
                RunnerAndConfigurationSettings settings = ExampleRunConfigurations.install(
                        project, attempt, attemptId, javaFile, module, input, debug);
                session.set(current.installed((ApplicationConfiguration) settings.getConfiguration(), settings));
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
            stopOwnedExampleProcess(control, handler);
        }
    }

    private void finishCheck(ManagedPracticeWorkspace.Attempt attempt, String fingerprint, CheckResult result) {
        ApplicationManager.getApplication().invokeLaterOnWriteThread(() ->
                WriteIntentReadAction.run(() -> {
                    RunnerAndConfigurationSettings settings =
                            session.getAndSet(RunSession.IDLE) instanceof RunSession.CheckSession check
                                    ? check.settings() : null;
                    untrackAndRemove(settings);
                    if (!project.isDisposed()) {
                        ManagedPracticeProgress.get().record(
                                attempt.id(), attempt.exerciseId(), fingerprint, result);
                        publish(new RunEvent.CheckFinished(
                                attempt.id(), attempt.exerciseId(), fullCheckMessage(attempt, result)));
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
                    untrackAndRemove(settings);
                    if (attempt != null && !project.isDisposed()) {
                        ManagedPracticeWorkspace.Attempt finishedAttempt = attempt;
                        PracticeModuleWorkspace.get(project).clearExampleHarness(finishedAttempt);
                        resultText = exampleResultText(resultText, fingerprint,
                                () -> attempts.fingerprint(finishedAttempt));
                    }
                    publish(new RunEvent.ExampleFinished(RunEvent.attemptId(attempt), resultText));
                }
        ));
    }

    private void removeTemporaryConfiguration(RunnerAndConfigurationSettings settings) {
        if (settings == null) {
            return;
        }
        ApplicationManager.getApplication().invokeLaterOnWriteThread(() ->
                WriteIntentReadAction.run(() -> untrackAndRemove(settings)));
    }

    private void untrackAndRemove(RunnerAndConfigurationSettings settings) {
        if (settings != null && !project.isDisposed()) {
            requireModelAccess();
            RunManager.getInstance(project).removeConfiguration(settings);
            PracticeModuleWorkspace.get(project).untrack(settings);
        }
    }

    @TestOnly
    void setPendingCheckForTest(RunnerAndConfigurationSettings settings) {
        pendingCheckSettings.set(settings);
    }

    @TestOnly
    RunSession replaceSessionForTest(RunSession next) {
        return session.getAndSet(next);
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
                publish(new RunEvent.FailedToStart("Cannot start practice execution: " + exception.getMessage()));
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
            publish(new RunEvent.FailedToStart("Cannot start practice execution: " + exception.getMessage()));
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

    private void publish(RunEvent event) {
        listeners.publish(project, event);
    }

    static void stopOwnedExampleProcess(RunSession.RunControl control, ProcessHandler handler) {
        if (handler instanceof KillableProcessHandler killable) {
            killable.setShouldKillProcessSoftly(false);
        }
        control.captureChildrenOf(handler);
        control.terminateOwnedChildren();
        handler.destroyProcess();
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
