package com.jinloes.practice_plugin.run;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.ExecutionListener;
import com.intellij.execution.ExecutionManager;
import com.intellij.execution.RunManager;
import com.intellij.execution.RunnerAndConfigurationSettings;
import com.intellij.execution.executors.DefaultRunExecutor;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.runners.ExecutionUtil;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.WriteIntentReadAction;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.jinloes.practice_plugin.state.ManagedPracticeProgress;
import com.jinloes.practice_plugin.workspace.ManagedPracticeWorkspace;
import com.jinloes.practice_plugin.workspace.PracticeModuleWorkspace;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.TestOnly;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import static com.jinloes.practice_plugin.run.RunDecisions.fullCheckMessage;
import static com.jinloes.practice_plugin.run.RunDecisions.normalizedInput;

@Service(Service.Level.PROJECT)
public final class PracticeRunner implements Disposable {
    private final Project project;
    private final ManagedPracticeWorkspace attempts = ManagedPracticeWorkspace.get();
    private final AtomicReference<RunSession> session = new AtomicReference<>(RunSession.IDLE);
    private final AtomicReference<RunnerAndConfigurationSettings> pendingCheckSettings = new AtomicReference<>();
    private final ExampleExecution examples;
    private final Consumer<Runnable> continuationDispatcher;
    private final RunEvent.Listeners listeners = new RunEvent.Listeners();

    public PracticeRunner(Project project) {
        this(project, null);
    }

    @TestOnly
    PracticeRunner(Project project, Consumer<Runnable> continuationDispatcher) {
        this.project = project;
        this.continuationDispatcher = continuationDispatcher;
        this.examples = new ExampleExecution(project, attempts, session, this::publish, this::untrackAndRemove);
        project.getMessageBus().connect(this).subscribe(ExecutionManager.EXECUTION_TOPIC, new ExecutionListener() {
            @Override
            public void processStarted(@NotNull String executorId, @NotNull ExecutionEnvironment environment,
                                       @NotNull ProcessHandler handler) {
                RunSession.ExampleSession running = examples.matching(environment);
                if (running != null) {
                    examples.processStarted(running, handler);
                }
            }

            @Override
            public void processNotStarted(@NotNull String executorId, @NotNull ExecutionEnvironment environment) {
                examples.processNotStarted(examples.matching(environment));
                handleCheckProcessNotStarted(environment);
            }

            @Override
            public void processTerminated(@NotNull String executorId, @NotNull ExecutionEnvironment environment,
                                          @NotNull ProcessHandler handler, int exitCode) {
                examples.processTerminated(examples.matching(environment), exitCode);
            }
        });
    }

    @TestOnly
    ExampleExecution examples() {
        return examples;
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
        runAfterSavingDocuments(() -> examples.launchAfterSave(attemptId, argument, debug));
    }

    public void stop() {
        if (!(session.get() instanceof RunSession.Active running)) {
            return;
        }
        RunSession.RunControl control = running.control();
        control.requestCancel();
        ProcessHandler handler = control.active();
        if (handler != null && !handler.isProcessTerminated()) {
            ExampleExecution.stopOwnedProcess(control, handler);
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

    static void requireModelAccess() {
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

    @Override
    public void dispose() {
        RunSession current = session.get();
        stop();
        if (current instanceof RunSession.Active running) {
            running.control().cancelWatchdog();
        }
    }
}
