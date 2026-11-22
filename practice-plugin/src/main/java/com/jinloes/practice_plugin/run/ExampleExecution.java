package com.jinloes.practice_plugin.run;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.RunnerAndConfigurationSettings;
import com.intellij.execution.application.ApplicationConfiguration;
import com.intellij.execution.executors.DefaultDebugExecutor;
import com.intellij.execution.executors.DefaultRunExecutor;
import com.intellij.execution.process.KillableProcessHandler;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.runners.ExecutionUtil;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.WriteIntentReadAction;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiManager;
import com.intellij.util.concurrency.AppExecutorUtil;
import com.jinloes.practice_plugin.catalog.ExerciseCatalog;
import com.jinloes.practice_plugin.workspace.ManagedPracticeWorkspace;
import com.jinloes.practice_plugin.workspace.PracticeModuleWorkspace;

import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import static com.jinloes.practice_plugin.run.RunDecisions.exampleFinishedText;
import static com.jinloes.practice_plugin.run.RunDecisions.exampleInstallPreflight;
import static com.jinloes.practice_plugin.run.RunDecisions.exampleResultText;
import static com.jinloes.practice_plugin.run.RunDecisions.exampleRunnerOwned;
import static com.jinloes.practice_plugin.run.RunDecisions.exampleStart;

/**
 * Prepares, compiles, launches, and cleans up visible-example and custom-input runs on behalf of
 * {@link PracticeRunner}.
 *
 * <p>It owns no independent run-guard state; {@link PracticeRunner}'s atomic {@link RunSession}
 * reference remains the single source of truth.
 */
final class ExampleExecution {
    private final Project project;
    private final ManagedPracticeWorkspace attempts;
    private final AtomicReference<RunSession> session;
    private final Consumer<RunEvent> publish;
    private final Consumer<RunnerAndConfigurationSettings> untrackAndRemove;

    ExampleExecution(Project project,
                     ManagedPracticeWorkspace attempts,
                     AtomicReference<RunSession> session,
                     Consumer<RunEvent> publish,
                     Consumer<RunnerAndConfigurationSettings> untrackAndRemove) {
        this.project = project;
        this.attempts = attempts;
        this.session = session;
        this.publish = publish;
        this.untrackAndRemove = untrackAndRemove;
    }

    /** The example run this environment belongs to, or null when the event is not ours. */
    RunSession.ExampleSession matching(ExecutionEnvironment environment) {
        return session.get() instanceof RunSession.ExampleSession examples
                && environment.getRunProfile() == examples.configuration()
                ? examples : null;
    }

    void processStarted(RunSession.ExampleSession examples, ProcessHandler handler) {
        RunSession.RunControl control = examples.control();
        control.active(handler);
        RunDecisions.ExampleStart start = exampleStart(examples);
        if (start.cancelled()) {
            stopOwnedProcess(control, handler);
            return;
        }
        publish.accept(new RunEvent.Started(RunEvent.attemptId(examples.attempt()), start.message()));
    }

    void processNotStarted(RunSession.ExampleSession examples) {
        if (examples != null) {
            finish("Example launch did not complete. See the IDE notification and Run console.");
        }
    }

    void processTerminated(RunSession.ExampleSession examples, int exitCode) {
        if (examples != null) {
            finish(exampleFinishedText(examples.input(), exitCode));
        }
    }

    void launchAfterSave(String attemptId, String input, boolean debug) throws ExecutionException {
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
        publish.accept(new RunEvent.Started(attemptId, "Compiling examples."));
        AppExecutorUtil.getAppExecutorService().execute(() -> {
            try {
                modules.compileExamples(attempt, runner);
            } catch (ExecutionException exception) {
                finish(exception.getMessage());
                return;
            }
            ApplicationManager.getApplication().invokeLaterOnWriteThread(() ->
                    installConfiguration(attempt, attemptId, runner, input, debug));
        });
    }

    private void installConfiguration(
            ManagedPracticeWorkspace.Attempt attempt, String attemptId, Path runner, String input, boolean debug) {
        try {
            WriteIntentReadAction.runThrowable(() -> {
                RunDecisions.ExampleInstallPreflight preflight =
                        exampleInstallPreflight(project.isDisposed(), session.get());
                if (!preflight.proceed()) {
                    finish(preflight.message());
                    return;
                }
                RunSession.ExampleSession current = preflight.current();
                PracticeRunner.requireModelAccess();
                var expectedModule = PracticeModuleWorkspace.get(project).open(attempt);
                var runnerFile = LocalFileSystem.getInstance().refreshAndFindFileByNioFile(runner);
                var psiFile = runnerFile == null ? null : PsiManager.getInstance(project).findFile(runnerFile);
                var module = runnerFile == null ? null : ModuleUtilCore.findModuleForFile(runnerFile, project);
                if (!(psiFile instanceof PsiJavaFile javaFile) || !exampleRunnerOwned(javaFile, module, expectedModule)) {
                    finish("Generated example runner is not owned by the attempt module.");
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
                    finish("Example launch failed before execution: " + failure.getMessage());
                    throw failure;
                }
            });
        } catch (ExecutionException exception) {
            finish("Example launch failed before execution: " + exception.getMessage());
        }
    }

    private void finish(String text) {
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
                    untrackAndRemove.accept(settings);
                    if (attempt != null && !project.isDisposed()) {
                        ManagedPracticeWorkspace.Attempt finishedAttempt = attempt;
                        PracticeModuleWorkspace.get(project).clearExampleHarness(finishedAttempt);
                        resultText = exampleResultText(resultText, fingerprint,
                                () -> attempts.fingerprint(finishedAttempt));
                    }
                    publish.accept(new RunEvent.ExampleFinished(RunEvent.attemptId(attempt), resultText));
                }
        ));
    }

    static void stopOwnedProcess(RunSession.RunControl control, ProcessHandler handler) {
        if (handler instanceof KillableProcessHandler killable) {
            killable.setShouldKillProcessSoftly(false);
        }
        control.captureChildrenOf(handler);
        control.terminateOwnedChildren();
        handler.destroyProcess();
    }
}
