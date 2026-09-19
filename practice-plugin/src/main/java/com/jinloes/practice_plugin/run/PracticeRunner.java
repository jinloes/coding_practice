package com.jinloes.practice_plugin.run;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.ExecutionListener;
import com.intellij.execution.ExecutionManager;
import com.intellij.execution.RunManager;
import com.intellij.execution.RunnerAndConfigurationSettings;
import com.intellij.execution.configurations.ConfigurationTypeUtil;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.executors.DefaultDebugExecutor;
import com.intellij.execution.executors.DefaultRunExecutor;
import com.intellij.execution.junit.JUnitConfiguration;
import com.intellij.execution.junit.JUnitConfigurationType;
import com.intellij.execution.process.KillableColoredProcessHandler;
import com.intellij.execution.process.ProcessListener;
import com.intellij.execution.process.ProcessEvent;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.process.ProcessOutputTypes;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.runners.ExecutionEnvironmentBuilder;
import com.intellij.execution.runners.ExecutionUtil;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.roots.ProjectFileIndex;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiManager;
import com.intellij.util.concurrency.AppExecutorUtil;
import com.jinloes.practice_plugin.catalog.ExerciseCatalog;
import com.jinloes.practice_plugin.state.PracticeProgress;
import com.jinloes.practice_plugin.workspace.PracticeWorkspace;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

import static com.jinloes.practice_plugin.run.CheckResult.Status.*;

@Service(Service.Level.PROJECT)
public final class PracticeRunner implements Disposable {
    private final Project project;
    private final AtomicBoolean running = new AtomicBoolean();
    private volatile ProcessHandler active;
    private volatile CheckResult.Status stopReason;
    private volatile String stopDetails;
    private volatile ScheduledFuture<?> watchdog;
    private final Map<Long, ProcessHandle> ownedProcesses = new ConcurrentHashMap<>();
    private volatile Consumer<String> listener = ignored -> {};
    private volatile JUnitConfiguration debugging;

    public PracticeRunner(Project project) {
        this.project = project;
        project.getMessageBus().connect(this).subscribe(ExecutionManager.EXECUTION_TOPIC, new ExecutionListener() {
            @Override
            public void processStarted(@NotNull String executorId, @NotNull ExecutionEnvironment environment,
                                       @NotNull ProcessHandler handler) {
                if (environment.getRunProfile() == debugging) {
                    active = handler;
                    if (stopReason == CANCELLED) {
                        handler.destroyProcess();
                        return;
                    }
                    message("Debugging examples. Timeouts are disabled; use Stop to terminate.");
                }
            }

            @Override
            public void processNotStarted(@NotNull String executorId, @NotNull ExecutionEnvironment environment) {
                if (environment.getRunProfile() == debugging) {
                    finishDebug("Debug launch did not complete. See the IDE notification and Run console.");
                }
            }

            @Override
            public void processTerminated(@NotNull String executorId, @NotNull ExecutionEnvironment environment,
                                          @NotNull ProcessHandler handler, int exitCode) {
                if (environment.getRunProfile() == debugging) {
                    finishDebug("Debug session finished (exit " + exitCode
                            + "). Use Check Solution to update correctness progress.");
                }
            }
        });
    }

    public void setListener(Consumer<String> listener) {
        this.listener = listener;
    }

    public boolean isRunning() {
        return running.get();
    }

    public void run(String attemptId, boolean full) throws ExecutionException {
        ensureIdle();
        javaHome(project);
        PracticeProgress.get(project).validateLimits();
        FileDocumentManager.getInstance().saveAllDocuments();
        RunManager manager = RunManager.getInstance(project);
        var type = ConfigurationTypeUtil.findConfigurationType(PracticeConfigurationType.class);
        String name = (full ? "Check " : "Examples ") + attemptId;
        RunnerAndConfigurationSettings settings = manager.findConfigurationByName(name);
        if (settings == null || !(settings.getConfiguration() instanceof PracticeRunConfiguration)) {
            settings = manager.createConfiguration(name, type.getConfigurationFactories()[0]);
            manager.addConfiguration(settings);
        }
        var configuration = (PracticeRunConfiguration) settings.getConfiguration();
        configuration.attemptId = attemptId;
        configuration.full = full;
        manager.setSelectedConfiguration(settings);
        ExecutionUtil.runConfiguration(settings, DefaultRunExecutor.getRunExecutorInstance());
    }

    ProcessHandler start(String attemptId, boolean full) throws ExecutionException {
        if (!running.compareAndSet(false, true)) {
            throw new ExecutionException("A practice run is already active. Stop it before starting another.");
        }
        stopReason = null;
        stopDetails = null;
        ownedProcesses.clear();
        try {
            Path root = Path.of(java.util.Objects.requireNonNull(project.getBasePath()));
            var attempt = PracticeWorkspace.readAttempt(root, attemptId);
            var exercise = ExerciseCatalog.find(attempt.exerciseId());
            String fingerprint = PracticeWorkspace.fingerprint(root, attempt);
            var progress = PracticeProgress.get(project);
            progress.validateLimits();
            int testSeconds = progress.getState().testSeconds;
            int suiteSeconds = progress.getState().suiteSeconds;
            int heapMb = progress.getState().heapMb;
            Path home = javaHome(project);
            String runId = UUID.randomUUID().toString();
            Path resultRoot = root.resolve(".practice-results").resolve(runId);
            var command = new GeneralCommandLine(
                    home.resolve("bin").resolve(isWindows() ? "java.exe" : "java").toString(),
                    "-classpath", root.resolve("gradle/wrapper/gradle-wrapper.jar").toString(),
                    "org.gradle.wrapper.GradleWrapperMain",
                    ":" + attemptId + ":test", "--no-daemon", "--console=plain", "--rerun-tasks",
                    "--no-build-cache", "--no-configuration-cache",
                    "-Dorg.gradle.java.home=" + home,
                    "-PpracticeRun=" + runId, "-PpracticeTestSeconds=" + testSeconds,
                    "-PpracticeHeapMb=" + heapMb)
                    .withWorkingDirectory(root).withEnvironment("JAVA_HOME", home.toString());
            if (!full) {
                command.addParameters("--tests", "com.jinloes.practice.ExamplesTest");
            }
            var handler = new BoundedProcessHandler(command, () -> {
                if (stopReason == null) {
                    stopReason = CANCELLED;
                }
                terminateOwnedChildren();
            });
            handler.setShouldKillProcessSoftly(false);
            active = handler;
            long setupStarted = System.nanoTime();
            handler.addProcessListener(new ProcessListener() {
                @Override
                public void processTerminated(@NotNull ProcessEvent event) {
                    ScheduledFuture<?> currentWatchdog = watchdog;
                    if (currentWatchdog != null) {
                        currentWatchdog.cancel(false);
                    }
                    terminateOwnedChildren();
                    AppExecutorUtil.getAppExecutorService().execute(() -> {
                        CheckResult result = new CheckResult(RUNNER_ERROR, 0, 0,
                                "Result processing failed. See the IDE log for diagnostics.");
                        try {
                            if (stopReason != null) {
                                result = new CheckResult(stopReason, 0, 0, stopDetails != null ? stopDetails
                                        : "Run cancelled. No passing result was recorded.");
                            } else {
                                String phase = Files.isRegularFile(resultRoot.resolve("phase"))
                                        ? Files.readString(resultRoot.resolve("phase")) : "setup";
                                result = event.getExitCode() != 0 && phase.equals("compiling")
                                        ? new CheckResult(COMPILATION_FAILED, 0, 0,
                                        "Compilation failed. See the Run console for file and line diagnostics.")
                                        : TestReports.read(resultRoot.resolve(attemptId),
                                        full ? exercise.fullCount() : exercise.exampleCount(), full, event.getExitCode());
                            }
                            boolean stale = !fingerprint.equals(PracticeWorkspace.fingerprint(root, attempt));
                            if (stale) {
                                result = new CheckResult(RUNNER_ERROR, result.tests(), result.failures(),
                                        "STALE: files changed during this run. Check the current solution again.\n" + result.details());
                            }
                        } catch (IOException | UncheckedIOException | IllegalArgumentException e) {
                            result = new CheckResult(RUNNER_ERROR, 0, 0, e.getMessage());
                        } finally {
                            CheckResult completed = result;
                            ApplicationManager.getApplication().invokeLater(() -> {
                                active = null;
                                running.set(false);
                                if (!project.isDisposed()) {
                                    if (full) {
                                        progress.record(attemptId, exercise.id(), fingerprint, completed);
                                    }
                                    message("Attempt: " + attemptId + "\n"
                                            + (full ? "Full check: " : "Examples: ") + completed.status()
                                            + "\n" + completed.details());
                                }
                            });
                        }
                    });
                }
            });
            watchdog = AppExecutorUtil.getAppScheduledExecutorService().scheduleWithFixedDelay(() -> {
                if (handler.isProcessTerminated()) {
                    return;
                }
                handler.getProcess().descendants().forEach(p -> ownedProcesses.put(p.pid(), p));
                try {
                    Path started = resultRoot.resolve("started");
                    boolean expired = Files.isRegularFile(started)
                            ? System.currentTimeMillis() - Files.getLastModifiedTime(started).toMillis() > suiteSeconds * 1000L
                            : System.nanoTime() - setupStarted > TimeUnit.MINUTES.toNanos(5);
                    if (expired) {
                        stopReason = TIMED_OUT;
                        stopDetails = Files.isRegularFile(started)
                                ? "Tests exceeded " + suiteSeconds + " seconds. The owned process tree was stopped."
                                : "Setup/compilation exceeded five minutes. Check SDK, network, and Run output.";
                        stop();
                    }
                } catch (IOException e) {
                    message("Cannot inspect execution timer: " + e.getMessage());
                    stopReason = RUNNER_ERROR;
                    stopDetails = "Cannot inspect execution timer: " + e.getMessage();
                    stop();
                }
            }, 0, 250, TimeUnit.MILLISECONDS);
            if (handler.isProcessTerminated()) {
                watchdog.cancel(false);
            }
            message("Attempt: " + attemptId + "\nRunning " + (full ? "full correctness suite" : "examples")
                    + ". First setup may download Gradle and test dependencies.\n"
                    + "Setup limit: 5 minutes; test execution limit: " + suiteSeconds + " seconds.");
            return handler;
        } catch (IOException | ExecutionException | IllegalArgumentException e) {
            running.set(false);
            throw new ExecutionException("Cannot start practice run: " + e.getMessage(), e);
        }
    }

    public void debug(String attemptId) throws ExecutionException {
        ensureIdle();
        javaHome(project);
        if (DumbService.isDumb(project)) {
            throw new ExecutionException("Wait for indexing and Gradle import to finish before debugging.");
        }
        FileDocumentManager.getInstance().saveAllDocuments();
        Path file = Path.of(project.getBasePath(), "attempts", attemptId,
                "src/test/java/com/jinloes/practice/ExamplesTest.java");
        var virtualFile = LocalFileSystem.getInstance().findFileByNioFile(file);
        if (virtualFile == null || !(PsiManager.getInstance(project).findFile(virtualFile) instanceof PsiJavaFile javaFile)
                || javaFile.getClasses().length != 1 || ModuleUtilCore.findModuleForFile(virtualFile, project) == null
                || !ProjectFileIndex.getInstance(project).isInTestSourceContent(virtualFile)) {
            throw new ExecutionException("The attempt is not imported yet. Use Reload Gradle, wait for import, then retry.");
        }
        RunManager manager = RunManager.getInstance(project);
        var settings = manager.createConfiguration("Debug examples " + attemptId,
                JUnitConfigurationType.getInstance().getFactory());
        var configuration = (JUnitConfiguration) settings.getConfiguration();
        configuration.beClassConfiguration(javaFile.getClasses()[0]);
        configuration.setModule(ModuleUtilCore.findModuleForFile(virtualFile, project));
        configuration.setWorkingDirectory(project.getBasePath());
        configuration.setVMParameters("-Xmx" + PracticeProgress.get(project).getState().heapMb
                + "m -Djunit.jupiter.execution.timeout.mode=disabled");
        manager.setTemporaryConfiguration(settings);
        manager.setSelectedConfiguration(settings);
        var environment = ExecutionEnvironmentBuilder.create(
                DefaultDebugExecutor.getDebugExecutorInstance(), settings).activeTarget().build();
        stopReason = null;
        debugging = configuration;
        running.set(true);
        message("Starting native example debugger. Compilation/import may take a moment.");
        boolean submitted = false;
        try {
            ExecutionManager.getInstance(project).restartRunProfile(environment);
            submitted = true;
        } finally {
            if (!submitted) {
                finishDebug("Debug launch failed before execution. See the IDE error for details.");
            }
        }
    }

    public void stop() {
        if (stopReason == null) {
            stopReason = CANCELLED;
        }
        ProcessHandler handler = active;
        if (handler != null && !handler.isProcessTerminated()) {
            if (handler instanceof KillableColoredProcessHandler process) {
                process.getProcess().descendants().forEach(p -> ownedProcesses.put(p.pid(), p));
            }
            terminateOwnedChildren();
            handler.destroyProcess();
        }
    }

    private void terminateOwnedChildren() {
        ownedProcesses.values().forEach(handle -> {
            if (handle.isAlive()) {
                handle.destroyForcibly();
            }
        });
        ownedProcesses.clear();
    }

    private void ensureIdle() throws ExecutionException {
        if (running.get()) {
            throw new ExecutionException("A practice run is already active. Stop it before starting another.");
        }
    }

    private void finishDebug(String text) {
        active = null;
        debugging = null;
        running.set(false);
        message(text);
    }

    private void message(String text) {
        ApplicationManager.getApplication().invokeLater(() -> {
            if (!project.isDisposed()) {
                listener.accept(text);
            }
        });
    }

    public static Path javaHome(Project project) throws ExecutionException {
        Sdk sdk = ProjectRootManager.getInstance(project).getProjectSdk();
        if (sdk == null || sdk.getHomePath() == null) {
            throw new ExecutionException("Set a JDK 17 or newer in File > Project Structure > Project SDK, then retry.");
        }
        Path home = Path.of(sdk.getHomePath());
        if (!Files.isRegularFile(home.resolve("bin").resolve(isWindows() ? "java.exe" : "java"))
                || !Files.isRegularFile(home.resolve("release"))) {
            throw new ExecutionException("The project SDK is not a usable JDK. Select a JDK 17 or newer.");
        }
        try {
            var properties = new java.util.Properties();
            try (var reader = Files.newBufferedReader(home.resolve("release"))) {
                properties.load(reader);
            }
            String version = properties.getProperty("JAVA_VERSION", "").replace("\"", "");
            int major = Runtime.Version.parse(version).feature();
            if (major < 17) {
                throw new ExecutionException("Gradle requires JDK 17 or newer; selected SDK is Java " + major + ".");
            }
        } catch (IOException | IllegalArgumentException e) {
            throw new ExecutionException("Cannot read the selected JDK version: " + e.getMessage(), e);
        }
        return home;
    }

    private static boolean isWindows() {
        return System.getProperty("os.name").startsWith("Windows");
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
        stop();
        if (watchdog != null) {
            watchdog.cancel(false);
        }
    }
}
