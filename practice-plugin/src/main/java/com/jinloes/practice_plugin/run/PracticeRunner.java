package com.jinloes.practice_plugin.run;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.ExecutionListener;
import com.intellij.execution.ExecutionManager;
import com.intellij.execution.RunManager;
import com.intellij.execution.RunnerAndConfigurationSettings;
import com.intellij.execution.application.ApplicationConfiguration;
import com.intellij.execution.configurations.ConfigurationTypeUtil;
import com.intellij.execution.executors.DefaultDebugExecutor;
import com.intellij.execution.executors.DefaultRunExecutor;
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
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import static com.jinloes.practice_plugin.run.CheckResult.Status.CANCELLED;

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
                    control.captureChildrenOf(handler);
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
        try {
            return new FullCheck(project, attempts, this::message, this::stop, this::finishCheck)
                    .start(attemptId, control);
        } catch (IOException | ExecutionException | IllegalArgumentException exception) {
            session.set(RunSession.IDLE);
            throw new ExecutionException("Cannot start managed check: " + exception.getMessage(), exception);
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
            control.captureChildrenOf(handler);
            control.terminateOwnedChildren();
            handler.destroyProcess();
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

    @Override
    public void dispose() {
        RunSession current = session.get();
        stop();
        if (current instanceof RunSession.Active running) {
            running.control().cancelWatchdog();
        }
    }
}
