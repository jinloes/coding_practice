package com.jinloes.practice_plugin.integration;

import com.intellij.execution.ExecutionResult;
import com.intellij.execution.RunManager;
import com.intellij.execution.configurations.ConfigurationTypeUtil;
import com.intellij.execution.executors.DefaultRunExecutor;
import com.intellij.execution.process.OSProcessHandler;
import com.intellij.execution.runners.ExecutionEnvironmentBuilder;
import com.intellij.execution.runners.ProgramRunner;
import com.intellij.openapi.application.WriteAction;
import com.intellij.openapi.projectRoots.JavaSdk;
import com.intellij.openapi.projectRoots.ProjectJdkTable;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.testFramework.EdtTestUtil;
import com.intellij.testFramework.fixtures.IdeaProjectTestFixture;
import com.intellij.testFramework.fixtures.IdeaTestFixtureFactory;
import com.jinloes.practice_plugin.catalog.ExerciseCatalog;
import com.jinloes.practice_plugin.run.PracticeConfigurationType;
import com.jinloes.practice_plugin.run.PracticeRunConfiguration;
import com.jinloes.practice_plugin.run.PracticeRunner;
import com.jinloes.practice_plugin.state.PracticeProgress;
import com.jinloes.practice_plugin.workspace.PracticeWorkspace;
import com.jinloes.practice_plugin.workspace.GradleWorkspaceImport;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("gradle-integration")
class PracticeExecutionIntegrationTest {
    @TempDir Path temporary;
    private IdeaProjectTestFixture fixture;
    private Path root;

    @BeforeEach
    void setUp() throws Exception {
        root = temporary.toRealPath().resolve("practice");
        PracticeWorkspace.create(root);
        EdtTestUtil.runInEdtAndWait(() -> {
            fixture = IdeaTestFixtureFactory.getFixtureFactory()
                    .createFixtureBuilder("practice-execution", root, false).getFixture();
            fixture.setUp();
            WriteAction.run(() -> {
                var sdk = JavaSdk.getInstance().createJdk("Practice test JDK", System.getProperty("java.home"), false);
                ProjectJdkTable.getInstance().addJdk(sdk, fixture.getTestRootDisposable());
                ProjectRootManager.getInstance(fixture.getProject()).setProjectSdk(sdk);
            });
        });
        assertThat(Path.of(fixture.getProject().getBasePath())).isEqualTo(root);
    }

    @AfterEach
    void tearDown() throws Exception {
        if (fixture != null) {
            EdtTestUtil.runInEdtAndWait(fixture::tearDown);
        }
    }

    @Test
    void fullCheckRunsOutsideTheIdeAndRecordsFreshProgress() throws Exception {
        var exercise = ExerciseCatalog.find("pair-sum");
        var attempt = PracticeWorkspace.createAttempt(root, exercise);
        try (var input = getClass().getResourceAsStream("/reference/pair-sum/Solution.java")) {
            assertThat(input).isNotNull();
            Files.writeString(attempt.solution(), new String(input.readAllBytes(), StandardCharsets.UTF_8));
        }
        var unrelated = PracticeWorkspace.createAttempt(root, ExerciseCatalog.find("binary-search"));
        Files.writeString(unrelated.solution(), "This unrelated attempt intentionally does not compile.");
        CountDownLatch completed = new CountDownLatch(1);
        PracticeRunner runner = fixture.getProject().getService(PracticeRunner.class);
        runner.setListener(text -> {
            if (text.contains("Full check:")) {
                completed.countDown();
            }
        });
        ExecutionResult execution = start(attempt.id());
        com.intellij.openapi.util.Disposer.register(fixture.getTestRootDisposable(), execution.getExecutionConsole());
        try {
            execution.getProcessHandler().startNotify();
            assertThat(completed.await(180, TimeUnit.SECONDS)).as("full check completed").isTrue();
            EdtTestUtil.runInEdtAndWait(() -> {
                var entry = PracticeProgress.get(fixture.getProject()).entry(attempt.id(), exercise.id());
                assertThat(entry.status).as(entry.details).isEqualTo("PASSED");
                assertThat(entry.tests).isEqualTo(exercise.fullCount());
                assertThat(entry.checkedFingerprint).isEqualTo(PracticeWorkspace.fingerprint(root, attempt));
                assertThat(runner.isRunning()).isFalse();
            });
        } finally {
            runner.stop();
        }
    }

    @Test
    void explicitCancellationDoesNotRecordAPass() throws Exception {
        var exercise = ExerciseCatalog.find("pair-sum");
        var attempt = PracticeWorkspace.createAttempt(root, exercise);
        CountDownLatch completed = new CountDownLatch(1);
        PracticeRunner runner = fixture.getProject().getService(PracticeRunner.class);
        runner.setListener(text -> {
            if (text.contains("Full check:")) {
                completed.countDown();
            }
        });
        ExecutionResult execution = start(attempt.id());
        com.intellij.openapi.util.Disposer.register(fixture.getTestRootDisposable(), execution.getExecutionConsole());
        try {
            execution.getProcessHandler().startNotify();
            execution.getProcessHandler().destroyProcess();
            assertThat(completed.await(30, TimeUnit.SECONDS)).isTrue();
            EdtTestUtil.runInEdtAndWait(() -> {
                var entry = PracticeProgress.get(fixture.getProject()).entry(attempt.id(), exercise.id());
                assertThat(entry.status).isEqualTo("CANCELLED");
                assertThat(entry.lastPassedAt).isEmpty();
                assertThat(runner.isRunning()).isFalse();
            });
            assertThat(((OSProcessHandler) execution.getProcessHandler()).getProcess().isAlive()).isFalse();
        } finally {
            runner.stop();
        }
    }

    @org.junit.jupiter.params.ParameterizedTest
    @org.junit.jupiter.params.provider.ValueSource(strings = {
            "ASSERTION_FAILED", "COMPILATION_FAILED", "RUNTIME_ERROR", "RUNNER_ERROR"
    })
    void reportsExecutionFailuresWithoutRecordingAPass(String expectedStatus) throws Exception {
        var exercise = ExerciseCatalog.find("pair-sum");
        var attempt = PracticeWorkspace.createAttempt(root, exercise);
        switch (expectedStatus) {
            case "ASSERTION_FAILED" -> Files.writeString(attempt.solution(), """
                    package com.jinloes.practice;
                    public class Solution {
                        public static int[] findPair(int[] numbers, int target) { return new int[0]; }
                    }
                    """);
            case "COMPILATION_FAILED" -> Files.writeString(attempt.solution(), "invalid Java");
            case "RUNNER_ERROR" -> Files.writeString(root.resolve("build.gradle"),
                    "\nthrow new GradleException('Simulated setup failure')\n",
                    java.nio.file.StandardOpenOption.APPEND);
            case "RUNTIME_ERROR" -> { /* The starter deliberately throws UnsupportedOperationException. */ }
            default -> throw new IllegalArgumentException(expectedStatus);
        }
        CountDownLatch completed = new CountDownLatch(1);
        PracticeRunner runner = fixture.getProject().getService(PracticeRunner.class);
        runner.setListener(text -> {
            if (text.contains("Full check:")) {
                completed.countDown();
            }
        });
        ExecutionResult execution = start(attempt.id());
        com.intellij.openapi.util.Disposer.register(fixture.getTestRootDisposable(), execution.getExecutionConsole());
        try {
            execution.getProcessHandler().startNotify();
            assertThat(completed.await(180, TimeUnit.SECONDS)).isTrue();
            EdtTestUtil.runInEdtAndWait(() -> {
                var entry = PracticeProgress.get(fixture.getProject()).entry(attempt.id(), exercise.id());
                assertThat(entry.status).as(entry.details).isEqualTo(expectedStatus);
                assertThat(entry.lastPassedAt).isEmpty();
                assertThat(runner.isRunning()).isFalse();
            });
        } finally {
            runner.stop();
        }
    }

    @Test
    void missingSdkProvidesActionableGuidance() {
        EdtTestUtil.runInEdtAndWait(() -> WriteAction.run(() ->
                ProjectRootManager.getInstance(fixture.getProject()).setProjectSdk(null)));
        org.assertj.core.api.Assertions.assertThatThrownBy(() -> PracticeRunner.javaHome(fixture.getProject()))
                .isInstanceOf(com.intellij.execution.ExecutionException.class)
                .hasMessageContaining("Project Structure");
    }

    @org.junit.jupiter.params.ParameterizedTest
    @org.junit.jupiter.params.provider.ValueSource(booleans = {false, true})
    void changedOrRemovedInputsCannotRecordAPassOrLeaveTheRunnerBusy(boolean removeMetadata) throws Exception {
        var exercise = ExerciseCatalog.find("pair-sum");
        var attempt = PracticeWorkspace.createAttempt(root, exercise);
        try (var input = getClass().getResourceAsStream("/reference/pair-sum/Solution.java")) {
            assertThat(input).isNotNull();
            Files.writeString(attempt.solution(), new String(input.readAllBytes(), StandardCharsets.UTF_8));
        }
        CountDownLatch completed = new CountDownLatch(1);
        PracticeRunner runner = fixture.getProject().getService(PracticeRunner.class);
        runner.setListener(text -> {
            if (text.contains("Full check:")) {
                completed.countDown();
            }
        });
        ExecutionResult execution = start(attempt.id());
        com.intellij.openapi.util.Disposer.register(fixture.getTestRootDisposable(), execution.getExecutionConsole());
        try {
            if (removeMetadata) {
                Files.delete(attempt.directory().resolve(".practice-attempt"));
            } else {
                Files.writeString(attempt.solution(), "\n// Edited during verification\n",
                        java.nio.file.StandardOpenOption.APPEND);
            }
            execution.getProcessHandler().startNotify();
            assertThat(completed.await(180, TimeUnit.SECONDS)).isTrue();
            EdtTestUtil.runInEdtAndWait(() -> {
                var entry = PracticeProgress.get(fixture.getProject()).entry(attempt.id(), exercise.id());
                assertThat(entry.status).as(entry.details).isEqualTo("RUNNER_ERROR");
                assertThat(entry.lastPassedAt).isEmpty();
                assertThat(runner.isRunning()).isFalse();
                if (!removeMetadata) {
                    assertThat(entry.details).contains("STALE:");
                }
            });
        } finally {
            runner.stop();
        }
    }

    @Test
    void nonCooperativeInfiniteLoopIsTerminatedByTheSuiteDeadline() throws Exception {
        var exercise = ExerciseCatalog.find("pair-sum");
        var attempt = PracticeWorkspace.createAttempt(root, exercise);
        Files.writeString(attempt.solution(), """
                package com.jinloes.practice;
                public class Solution {
                    public static int[] findPair(int[] numbers, int target) {
                        while (true) { Thread.onSpinWait(); }
                    }
                }
                """);
        CountDownLatch completed = new CountDownLatch(1);
        PracticeRunner runner = fixture.getProject().getService(PracticeRunner.class);
        EdtTestUtil.runInEdtAndWait(() -> {
            var limits = PracticeProgress.get(fixture.getProject()).getState();
            limits.testSeconds = 1;
            limits.suiteSeconds = 1;
        });
        runner.setListener(text -> {
            if (text.contains("Full check:")) {
                completed.countDown();
            }
        });
        ExecutionResult execution = start(attempt.id());
        com.intellij.openapi.util.Disposer.register(fixture.getTestRootDisposable(), execution.getExecutionConsole());
        try {
            execution.getProcessHandler().startNotify();
            assertThat(completed.await(120, TimeUnit.SECONDS)).isTrue();
            EdtTestUtil.runInEdtAndWait(() -> {
                var entry = PracticeProgress.get(fixture.getProject()).entry(attempt.id(), exercise.id());
                assertThat(entry.status).as(entry.details).isEqualTo("TIMED_OUT");
                assertThat(entry.lastPassedAt).isEmpty();
                assertThat(runner.isRunning()).isFalse();
            });
            assertThat(((OSProcessHandler) execution.getProcessHandler()).getProcess().isAlive()).isFalse();
        } finally {
            runner.stop();
        }
    }

    @org.junit.jupiter.params.ParameterizedTest
    @org.junit.jupiter.params.provider.ValueSource(booleans = {false, true})
    void gradleImportEnablesABreakpointInTheLearnerSolution(boolean missingDebugRunner) throws Exception {
        var attempt = PracticeWorkspace.createAttempt(root, ExerciseCatalog.find("pair-sum"));
        CountDownLatch imported = new CountDownLatch(1);
        AtomicBoolean successfulImport = new AtomicBoolean();
        EdtTestUtil.runInEdtAndWait(() -> GradleWorkspaceImport.refresh(fixture.getProject(), success -> {
            successfulImport.set(success);
            imported.countDown();
        }));
        assertThat(imported.await(180, TimeUnit.SECONDS)).as("Gradle import finished").isTrue();
        assertThat(successfulImport.get()).as("Gradle project imported successfully").isTrue();
        com.intellij.openapi.project.DumbService.getInstance(fixture.getProject()).waitForSmartMode();

        var solution = LocalFileSystem.getInstance().refreshAndFindFileByNioFile(attempt.solution());
        assertThat(solution).isNotNull();
        EdtTestUtil.runInEdtAndWait(() -> {
            var exampleFile = LocalFileSystem.getInstance().findFileByNioFile(
                    attempt.directory().resolve("src/test/java/com/jinloes/practice/ExamplesTest.java"));
            assertThat(exampleFile).as("import makes example file available in VFS").isNotNull();
            assertThat(com.intellij.openapi.roots.ProjectFileIndex.getInstance(fixture.getProject())
                    .isInTestSourceContent(exampleFile))
                    .as("test root imported; file=%s roots=%s", exampleFile.getPath(),
                            java.util.Arrays.stream(com.intellij.openapi.module.ModuleManager
                                            .getInstance(fixture.getProject()).getModules())
                                    .flatMap(module -> java.util.Arrays.stream(com.intellij.openapi.roots.ModuleRootManager
                                            .getInstance(module).getSourceRoots(true)))
                                    .map(file -> file.getPath()).toList())
                    .isTrue();
        });
        if (missingDebugRunner) {
            EdtTestUtil.runInEdtAndWait(() -> {
                ((com.intellij.openapi.extensions.impl.ExtensionPointImpl<?>)
                        ProgramRunner.PROGRAM_RUNNER_EP.getPoint())
                        .maskAll(java.util.List.of(), fixture.getTestRootDisposable(), false);
                PracticeRunner runner = fixture.getProject().getService(PracticeRunner.class);
                org.assertj.core.api.Assertions.assertThatThrownBy(() -> runner.debug(attempt.id()))
                        .isInstanceOf(com.intellij.execution.ExecutionException.class);
                assertThat(runner.isRunning()).as("a rejected launch does not reserve the runner").isFalse();
            });
            return;
        }
        CountDownLatch paused = new CountDownLatch(1);
        CountDownLatch stopped = new CountDownLatch(1);
        StringBuffer debugOutput = new StringBuffer();
        fixture.getProject().getMessageBus().connect(fixture.getTestRootDisposable()).subscribe(
                com.intellij.openapi.compiler.CompilerTopics.COMPILATION_STATUS,
                new com.intellij.openapi.compiler.CompilationStatusListener() {
                    @Override public void compilationFinished(boolean aborted, int errors, int warnings,
                                                              com.intellij.openapi.compiler.CompileContext context) {
                        for (var message : context.getMessages(com.intellij.openapi.compiler.CompilerMessageCategory.ERROR)) {
                            debugOutput.append(message.getMessage()).append('\n');
                        }
                    }
                });
        AtomicReference<com.intellij.xdebugger.XDebugSession> session = new AtomicReference<>();
        fixture.getProject().getMessageBus().connect(fixture.getTestRootDisposable()).subscribe(
                com.intellij.xdebugger.XDebuggerManager.TOPIC, new com.intellij.xdebugger.XDebuggerManagerListener() {
                    @Override public void processStarted(com.intellij.xdebugger.XDebugProcess process) {
                        session.set(process.getSession());
                        process.getProcessHandler().addProcessListener(new com.intellij.execution.process.ProcessListener() {
                            @Override public void onTextAvailable(com.intellij.execution.process.ProcessEvent event,
                                                                  com.intellij.openapi.util.Key outputType) {
                                debugOutput.append(event.getText());
                            }
                        });
                        process.getSession().addSessionListener(new com.intellij.xdebugger.XDebugSessionListener() {
                            @Override public void sessionPaused() { paused.countDown(); }
                            @Override public void sessionStopped() { stopped.countDown(); }
                        }, fixture.getTestRootDisposable());
                    }
                });
        var breakpoint = EdtTestUtil.runInEdtAndGet(() -> WriteAction.compute(() -> {
            var type = com.intellij.xdebugger.breakpoints.XBreakpointType.EXTENSION_POINT_NAME
                    .findExtension(com.intellij.debugger.ui.breakpoints.JavaLineBreakpointType.class);
            assertThat(type).isNotNull();
            return com.intellij.xdebugger.XDebuggerManager.getInstance(fixture.getProject()).getBreakpointManager()
                    .addLineBreakpoint(type, solution.getUrl(), 4, type.createBreakpointProperties(solution, 4));
        }));
        PracticeRunner runner = fixture.getProject().getService(PracticeRunner.class);
        AtomicReference<String> debugStatus = new AtomicReference<>("");
        runner.setListener(text -> {
            debugStatus.set(text);
            if (text.startsWith("Debug launch did not") || text.startsWith("Debug session finished")) {
                paused.countDown();
            }
        });
        ((com.intellij.openapi.projectRoots.impl.ProjectJdkTableImpl) ProjectJdkTable.getInstance()).saveOnDisk();
        try {
            EdtTestUtil.runInEdtAndWait(() -> {
                ((com.intellij.execution.impl.ExecutionManagerImpl) com.intellij.execution.ExecutionManager
                        .getInstance(fixture.getProject())).setForceCompilationInTests(true);
                runner.debug(attempt.id());
            });
            assertThat(paused.await(180, TimeUnit.SECONDS))
                    .as("breakpoint in Solution.java was hit; status=%s; output=%s", debugStatus.get(), debugOutput).isTrue();
            assertThat(session.get()).as("debugger launched; status=%s; output=%s", debugStatus.get(), debugOutput)
                    .isNotNull();
            EdtTestUtil.runInEdtAndWait(() -> {
                assertThat(session.get().isSuspended()).as("breakpoint hit; output=%s", debugOutput).isTrue();
                assertThat(session.get().getCurrentPosition().getFile()).isEqualTo(solution);
                assertThat(PracticeProgress.get(fixture.getProject()).entry(attempt.id(), attempt.exerciseId()).lastPassedAt)
                        .isEmpty();
            });
        } finally {
            EdtTestUtil.runInEdtAndWait(() -> {
                runner.stop();
                WriteAction.run(() -> com.intellij.xdebugger.XDebuggerManager.getInstance(fixture.getProject())
                        .getBreakpointManager().removeBreakpoint(breakpoint));
            });
            if (session.get() != null) {
                assertThat(stopped.await(30, TimeUnit.SECONDS)).as("debug session stopped").isTrue();
            }
        }
    }

    private ExecutionResult start(String attemptId) throws Exception {
        var environment = EdtTestUtil.runInEdtAndGet(() -> {
            var factory = ConfigurationTypeUtil.findConfigurationType(PracticeConfigurationType.class)
                    .getConfigurationFactories()[0];
            var settings = RunManager.getInstance(fixture.getProject()).createConfiguration("Full check", factory);
            var configuration = (PracticeRunConfiguration) settings.getConfiguration();
            configuration.attemptId = attemptId;
            configuration.full = true;
            return ExecutionEnvironmentBuilder.create(DefaultRunExecutor.getRunExecutorInstance(), settings).build();
        });
        var executor = DefaultRunExecutor.getRunExecutorInstance();
        return environment.getState().execute(executor, ProgramRunner.getRunner(executor.getId(), environment.getRunProfile()));
    }
}
