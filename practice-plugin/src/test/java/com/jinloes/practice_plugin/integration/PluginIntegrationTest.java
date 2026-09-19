package com.jinloes.practice_plugin.integration;

import com.intellij.codeInsight.daemon.impl.DaemonCodeAnalyzerImpl;
import com.intellij.execution.ExecutionListener;
import com.intellij.execution.ExecutionManager;
import com.intellij.execution.RunManager;
import com.intellij.execution.configurations.ConfigurationTypeUtil;
import com.intellij.execution.configurations.RuntimeConfigurationError;
import com.intellij.execution.executors.DefaultRunExecutor;
import com.intellij.execution.impl.ConsoleViewImpl;
import com.intellij.execution.process.ProcessAdapter;
import com.intellij.execution.process.ProcessEvent;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.process.OSProcessHandler;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.runners.ProgramRunner;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.compiler.CompilerManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.ide.plugins.PluginManagerCore;
import com.intellij.openapi.extensions.PluginId;
import com.intellij.openapi.wm.ToolWindowAnchor;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.testFramework.EdtTestUtil;
import com.intellij.testFramework.PlatformTestUtil;
import com.intellij.testFramework.fixtures.IdeaProjectTestFixture;
import com.intellij.testFramework.fixtures.IdeaTestFixtureFactory;
import com.jinloes.practice_plugin.run.PracticeConfigurationType;
import com.jinloes.practice_plugin.run.PracticeRunConfiguration;
import com.jinloes.practice_plugin.run.PracticeRunner;
import com.jinloes.practice_plugin.state.PracticeProgress;
import com.jinloes.practice_plugin.state.ScratchPracticeProgress;
import com.jinloes.practice_plugin.ui.PracticeToolWindowFactory;
import com.jinloes.practice_plugin.workspace.PracticeSupportEnvironment;
import com.jinloes.practice_plugin.workspace.ScratchAttemptStore;
import org.jdom.Element;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PluginIntegrationTest {
    @TempDir
    Path temporary;
    private IdeaProjectTestFixture fixture;

    @BeforeEach
    void setUp() throws Exception {
        Path host = temporary.resolve("host");
        Files.createDirectories(host);
        EdtTestUtil.runInEdtAndWait(() -> {
            fixture = IdeaTestFixtureFactory.getFixtureFactory()
                    .createFixtureBuilder("algorithm-practice", host, false).getFixture();
            fixture.setUp();
        });
    }

    @AfterEach
    void tearDown() throws Exception {
        if (fixture != null) {
            try {
                quiesceTestProject();
            } finally {
                EdtTestUtil.runInEdtAndWait(fixture::tearDown);
                fixture = null;
            }
        }
    }

    @Test
    void pluginLoadsAndProjectServicesResolve() throws Exception {
        EdtTestUtil.runInEdtAndWait(() -> {
            assertThat(PluginManagerCore.getPlugin(PluginId.getId("com.jinloes.practice"))).isNotNull();
            assertThat(PracticeProgress.get(fixture.getProject())).isNotNull();
            assertThat(ScratchPracticeProgress.get()).isNotNull();
            assertThat(fixture.getProject().getService(PracticeRunner.class).isRunning()).isFalse();
        });
    }

    @Test
    void nativeRunConfigurationRoundTripsAndFindsItsExecutor() throws Exception {
        EdtTestUtil.runInEdtAndWait(() -> {
            var type = ConfigurationTypeUtil.findConfigurationType(PracticeConfigurationType.class);
            var settings = RunManager.getInstance(fixture.getProject())
                    .createConfiguration("Check attempt", type.getConfigurationFactories()[0]);
            var configuration = (PracticeRunConfiguration) settings.getConfiguration();
            configuration.attemptId = "pair-sum-abc123";
            configuration.full = false;
            configuration.kind = PracticeRunConfiguration.Kind.SCRATCH;
            Element serialized = new Element("configuration");
            configuration.writeExternal(serialized);
            var restored = (PracticeRunConfiguration) type.getConfigurationFactories()[0]
                    .createTemplateConfiguration(fixture.getProject());
            restored.readExternal(serialized);
            assertThat(restored.attemptId).isEqualTo(configuration.attemptId);
            assertThat(restored.full).isFalse();
            assertThat(restored.kind).isEqualTo(PracticeRunConfiguration.Kind.SCRATCH);
            assertThat(ProgramRunner.getRunner(DefaultRunExecutor.EXECUTOR_ID, restored)).isNotNull();
            restored.readExternal(new Element("configuration"));
            assertThat(restored.kind).isEqualTo(PracticeRunConfiguration.Kind.LEGACY);
            restored.attemptId = "../outside";
            assertThatThrownBy(restored::checkConfiguration).isInstanceOf(RuntimeConfigurationError.class);
        });
    }

    @Test
    void toolWindowCreatesAndDisposesNativeContent() throws Exception {
        EdtTestUtil.runInEdtAndWait(() -> {
            var manager = ToolWindowManager.getInstance(fixture.getProject());
            var window = manager.registerToolWindow("Practice integration", true, ToolWindowAnchor.RIGHT);
            try {
                new PracticeToolWindowFactory().createToolWindowContent(fixture.getProject(), window);
                assertThat(window.getContentManager().getContentCount()).isEqualTo(1);
                assertThat(window.getContentManager().getContent(0).getComponent())
                        .isInstanceOf(javax.swing.JPanel.class);
            } finally {
                manager.unregisterToolWindow("Practice integration");
            }
        });
    }

    @Test
    void userFacingCleanupRefusesDuringAScratchRunAndSucceedsWhenIdle() throws Exception {
        ScratchAttemptStore store = new ScratchAttemptStore(fixture.getProject());
        var attempt = store.create(com.jinloes.practice_plugin.catalog.ExerciseCatalog.find("pair-sum"));
        var scratchFile = LocalFileSystem.getInstance().refreshAndFindFileByNioFile(attempt.solution());
        assertThat(scratchFile).isNotNull();
        EdtTestUtil.runInEdtAndWait(() ->
                assertThat(FileEditorManager.getInstance(fixture.getProject()).openFile(scratchFile, true)).isNotEmpty());

        PracticeRunner runner = fixture.getProject().getService(PracticeRunner.class);
        PracticeSupportEnvironment support = PracticeSupportEnvironment.get(fixture.getProject());
        CountDownLatch started = new CountDownLatch(1);
        CountDownLatch terminated = new CountDownLatch(1);
        CountDownLatch completed = new CountDownLatch(1);
        AtomicReference<ProcessHandler> process = new AtomicReference<>();
        AtomicReference<Boolean> activeDuringCallback = new AtomicReference<>();
        AtomicReference<Boolean> cleanupDuringCallback = new AtomicReference<>();
        AtomicReference<Throwable> callbackFailure = new AtomicReference<>();
        AtomicReference<String> commandLine = new AtomicReference<>("");
        AtomicReference<String> handlerType = new AtomicReference<>("");
        AtomicReference<String> status = new AtomicReference<>("");
        StringBuffer processOutput = new StringBuffer();
        Disposable executionSubscription = Disposer.newDisposable("scratch cleanup integration run");
        Disposer.register(fixture.getTestRootDisposable(), executionSubscription);
        fixture.getProject().getMessageBus().connect(executionSubscription).subscribe(
                ExecutionManager.EXECUTION_TOPIC, new ExecutionListener() {
                    @Override
                    public void processStarted(String executorId, ExecutionEnvironment environment,
                                               ProcessHandler handler) {
                        if (environment.getRunProfile() instanceof PracticeRunConfiguration configuration
                                && configuration.kind == PracticeRunConfiguration.Kind.SCRATCH) {
                            process.set(handler);
                            handlerType.set(handler.getClass().getName());
                            if (handler instanceof OSProcessHandler osProcess) {
                                commandLine.set(osProcess.getProcess().info().commandLine().orElse(""));
                            }
                            handler.addProcessListener(new ProcessAdapter() {
                                @Override
                                public void onTextAvailable(ProcessEvent event, Key outputType) {
                                    processOutput.append(event.getText());
                                }
                            });
                            try {
                                activeDuringCallback.set(runner.isRunning() && !handler.isProcessTerminated());
                                cleanupDuringCallback.set(ApplicationManager.getApplication().isDispatchThread()
                                        ? support.cleanupAbandonedArtifacts()
                                        : EdtTestUtil.runInEdtAndGet(support::cleanupAbandonedArtifacts));
                            } catch (Throwable failure) {
                                callbackFailure.set(failure);
                            }
                            started.countDown();
                        }
                    }

                    @Override
                    public void processTerminated(String executorId, ExecutionEnvironment environment,
                                                  ProcessHandler handler, int exitCode) {
                        if (handler == process.get()) {
                            terminated.countDown();
                        }
                    }
                });
        runner.setListener(text -> {
            status.set(text);
            if (text.contains("Full check:")) {
                completed.countDown();
            }
        });

        try {
            EdtTestUtil.runInEdtAndWait(() -> runner.runScratch(attempt.id(), true));
            assertThat(started.await(90, TimeUnit.SECONDS)).isTrue();
            assertThat(process.get()).isNotNull();
            assertThat(callbackFailure.get()).isNull();
            runner.stop();
            assertThat(terminated.await(90, TimeUnit.SECONDS)).isTrue();
            assertThat(completed.await(90, TimeUnit.SECONDS)).isTrue();
            String consoleText = EdtTestUtil.runInEdtAndGet(() -> {
                for (var descriptor : ExecutionManager.getInstance(fixture.getProject())
                        .getContentManager().getAllDescriptors()) {
                    if (descriptor.getExecutionConsole() instanceof ConsoleViewImpl console) {
                        console.flushDeferredText();
                        return console.getEditor().getDocument().getText();
                    }
                }
                return "";
            });
            assertThat(activeDuringCallback.get())
                    .as("handler=%s%ncommand=%s%nstatus=%s%noutput=%s%nconsole=%s",
                            handlerType.get(), commandLine.get(), status.get(), processOutput, consoleText)
                    .isTrue();
            assertThat(cleanupDuringCallback.get())
                    .as("command=%s%nstatus=%s%noutput=%s", commandLine.get(), status.get(), processOutput)
                    .isFalse();

            assertThat(runner.isRunning()).isFalse();
            assertThat(process.get().isProcessTerminated()).isTrue();
            assertThat(EdtTestUtil.runInEdtAndGet(support::cleanupAbandonedArtifacts)).isTrue();
        } finally {
            Disposer.dispose(executionSubscription);
        }
    }

    private void quiesceTestProject() throws Exception {
        var project = fixture.getProject();
        PracticeRunner runner = project.getService(PracticeRunner.class);
        runner.stop();
        EdtTestUtil.runInEdtAndWait(() -> PlatformTestUtil.waitWithEventsDispatching(
                "scratch execution and compilation to finish",
                () -> !runner.isRunning()
                        && ExecutionManager.getInstance(project).getRunningProcesses().length == 0
                        && !CompilerManager.getInstance(project).isCompilationActive(),
                90_000));

        EdtTestUtil.runInEdtAndWait(() -> {
            ((DaemonCodeAnalyzerImpl) DaemonCodeAnalyzerImpl.getInstance(project)).setUpdateByTimerEnabled(false);
            var fileEditorManager = FileEditorManager.getInstance(project);
            for (var file : fileEditorManager.getOpenFiles()) {
                fileEditorManager.closeFile(file);
            }
            var executionManager = ExecutionManager.getInstance(project);
            var contentManager = executionManager.getContentManager();
            for (var descriptor : new ArrayList<>(contentManager.getAllDescriptors())) {
                for (var executor : executionManager.getExecutors(descriptor)) {
                    contentManager.removeRunContent(executor, descriptor);
                }
            }
            runner.setListener(ignored -> {});
            PracticeSupportEnvironment.get(project).dispose();
        });

        DumbService.getInstance(project).waitForSmartMode();
        EdtTestUtil.runInEdtAndWait(PlatformTestUtil::dispatchAllInvocationEventsInIdeEventQueue);
        EdtTestUtil.runInEdtAndWait(() -> PlatformTestUtil.waitWithEventsDispatching(
                "editor daemon and execution follow-up work to finish",
                () -> !CompilerManager.getInstance(project).isCompilationActive()
                        && !((DaemonCodeAnalyzerImpl) DaemonCodeAnalyzerImpl.getInstance(project)).isRunningOrPending()
                        && ExecutionManager.getInstance(project).getRunningProcesses().length == 0
                        && ExecutionManager.getInstance(project).getContentManager().getAllDescriptors().isEmpty(),
                90_000));
        PlatformTestUtil.waitForAllBackgroundActivityToCalmDown();
        EdtTestUtil.runInEdtAndWait(PlatformTestUtil::dispatchAllInvocationEventsInIdeEventQueue);
    }
}
