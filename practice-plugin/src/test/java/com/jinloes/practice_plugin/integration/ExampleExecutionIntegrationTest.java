package com.jinloes.practice_plugin.integration;

import com.intellij.execution.ExecutionListener;
import com.intellij.execution.ExecutionManager;
import com.intellij.execution.application.ApplicationConfiguration;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.WriteAction;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.testFramework.EdtTestUtil;
import com.jinloes.practice_plugin.run.PracticeRunner;
import com.jinloes.practice_plugin.state.ManagedPracticeProgress;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;

class ExampleExecutionIntegrationTest extends PracticeExecutionTestBase {
    @Test
    void plainSwingEdtCanLaunchVisibleExamples() throws Exception {
        CountDownLatch completed = new CountDownLatch(1);
        AtomicReference<String> result = new AtomicReference<>("");
        PracticeRunner runner = fixture.getProject().getService(PracticeRunner.class);
        runner.addListener(text -> {
            result.set(text);
            if (text.startsWith("Example session finished")) {
                completed.countDown();
            }
        });

        EdtTestUtil.runInEdtAndWait(() ->
            ((com.intellij.execution.impl.ExecutionManagerImpl) ExecutionManager
                    .getInstance(fixture.getProject())).setForceCompilationInTests(true));
        runOnPlainSwingEdt(() -> runner.runExamples(attempt.id()));

        assertThat(completed.await(90, TimeUnit.SECONDS)).as(result.get()).isTrue();
        assertThat(result.get()).contains("exit 0");
    }

    @Test
    void savedDocumentContinuationRetainsReadAndWriteIntentAccess() throws Exception {
        CountDownLatch completed = new CountDownLatch(1);
        AtomicReference<Boolean> readAccess = new AtomicReference<>();
        AtomicReference<Boolean> writeIntent = new AtomicReference<>();
        Class<?> actionType = Class.forName(PracticeRunner.class.getName() + "$ExecutionAction");
        Method runAfterSavingDocuments = PracticeRunner.class.getDeclaredMethod(
                "runAfterSavingDocuments", actionType);
        runAfterSavingDocuments.setAccessible(true);
        Constructor<PracticeRunner> injecting = PracticeRunner.class.getDeclaredConstructor(
                com.intellij.openapi.project.Project.class, java.util.function.Consumer.class);
        injecting.setAccessible(true);
        Object continuation = Proxy.newProxyInstance(
                actionType.getClassLoader(),
                new Class<?>[]{actionType},
                (proxy, method, arguments) -> {
                    if (method.getName().equals("run")) {
                        readAccess.set(ApplicationManager.getApplication().isReadAccessAllowed());
                        writeIntent.set(ApplicationManager.getApplication().isWriteIntentLockAcquired());
                        completed.countDown();
                    }
                    return null;
                });
        ExecutorService executor = Executors.newSingleThreadExecutor();

        PracticeRunner runner = injecting.newInstance(
                fixture.getProject(), (java.util.function.Consumer<Runnable>) executor::execute);
        Disposer.register(fixture.getTestRootDisposable(), runner);
        try {
            runOnPlainSwingEdt(() -> invoke(runAfterSavingDocuments, runner, continuation));

            assertThat(completed.await(30, TimeUnit.SECONDS)).isTrue();
            assertThat(readAccess.get()).isTrue();
            assertThat(writeIntent.get()).isTrue();
        } finally {
            executor.shutdownNow();
            assertThat(executor.awaitTermination(30, TimeUnit.SECONDS)).isTrue();
        }
    }

    @Test
    void visibleExamplesUseGeneratedApplicationRunnerWithoutUpdatingProgress() throws Exception {
        CountDownLatch started = new CountDownLatch(1);
        CountDownLatch completed = new CountDownLatch(1);
        AtomicReference<ApplicationConfiguration> configuration = new AtomicReference<>();
        AtomicReference<String> result = new AtomicReference<>("");
        StringBuffer output = new StringBuffer();
        fixture.getProject().getMessageBus().connect(fixture.getTestRootDisposable()).subscribe(
                ExecutionManager.EXECUTION_TOPIC, new ExecutionListener() {
                    @Override
                    public void processStarted(String executorId, ExecutionEnvironment environment,
                                               ProcessHandler handler) {
                        if (environment.getRunProfile() instanceof ApplicationConfiguration application) {
                            configuration.set(application);
                            handler.addProcessListener(new com.intellij.execution.process.ProcessAdapter() {
                                @Override
                                public void onTextAvailable(
                                        com.intellij.execution.process.ProcessEvent event,
                                        com.intellij.openapi.util.Key outputType
                                ) {
                                    output.append(event.getText());
                                }
                            });
                            started.countDown();
                        }
                    }
                });
        PracticeRunner runner = fixture.getProject().getService(PracticeRunner.class);
        runner.addListener(text -> {
            result.set(text);
            if (text.startsWith("Example session finished")) {
                completed.countDown();
            }
        });

        EdtTestUtil.runInEdtAndWait(() -> {
            ((com.intellij.execution.impl.ExecutionManagerImpl) ExecutionManager
                    .getInstance(fixture.getProject())).setForceCompilationInTests(true);
            runner.runExamples(attempt.id());
        });

        assertThat(started.await(90, TimeUnit.SECONDS)).isTrue();
        assertThat(completed.await(90, TimeUnit.SECONDS)).isTrue();
        assertThat(configuration.get()).isNotNull();
        assertThat(configuration.get().getMainClassName()).isEqualTo("com.jinloes.practice.ExampleRunner");
        assertThat(configuration.get().getConfigurationModule().getModule()).isNotNull();
        assertThat(result.get()).as(output.toString()).contains("exit 0");
        assertThat(ManagedPracticeProgress.get().entry(attempt.id(), attempt.exerciseId()).status)
                .isEqualTo("NOT_RUN");
        assertThat(attempt.directory().resolve("ExampleRunner.java")).doesNotExist();
    }

    @Test
    void customInputRunsTheSolutionOnTheGivenArgumentsWithoutUpdatingProgress() throws Exception {
        CountDownLatch started = new CountDownLatch(1);
        CountDownLatch completed = new CountDownLatch(1);
        AtomicReference<ApplicationConfiguration> configuration = new AtomicReference<>();
        AtomicReference<String> result = new AtomicReference<>("");
        StringBuffer output = new StringBuffer();
        fixture.getProject().getMessageBus().connect(fixture.getTestRootDisposable()).subscribe(
                ExecutionManager.EXECUTION_TOPIC, new ExecutionListener() {
                    @Override
                    public void processStarted(String executorId, ExecutionEnvironment environment,
                                               ProcessHandler handler) {
                        if (environment.getRunProfile() instanceof ApplicationConfiguration application) {
                            configuration.set(application);
                            handler.addProcessListener(new com.intellij.execution.process.ProcessAdapter() {
                                @Override
                                public void onTextAvailable(
                                        com.intellij.execution.process.ProcessEvent event,
                                        com.intellij.openapi.util.Key outputType
                                ) {
                                    output.append(event.getText());
                                }
                            });
                            started.countDown();
                        }
                    }
                });
        PracticeRunner runner = fixture.getProject().getService(PracticeRunner.class);
        runner.addListener(text -> {
            result.set(text);
            if (text.startsWith("Input session finished")) {
                completed.countDown();
            }
        });

        EdtTestUtil.runInEdtAndWait(() -> {
            ((com.intellij.execution.impl.ExecutionManagerImpl) ExecutionManager
                    .getInstance(fixture.getProject())).setForceCompilationInTests(true);
            runner.runExamples(attempt.id(), "[2, 7, 11, 15] 9");
        });

        assertThat(started.await(90, TimeUnit.SECONDS)).isTrue();
        assertThat(completed.await(90, TimeUnit.SECONDS)).isTrue();
        assertThat(configuration.get()).isNotNull();
        assertThat(configuration.get().getProgramParameters())
                .as("the typed input must reach the generated runner as one argument")
                .isEqualTo("\"[2, 7, 11, 15] 9\"");
        assertThat(output.toString())
                .as("custom input must report the call and skip the visible examples")
                .contains("findPair([2, 7, 11, 15], 9) =")
                .doesNotContain("Examples passed:");
        assertThat(result.get()).as(output.toString()).contains("exit 0");
        assertThat(ManagedPracticeProgress.get().entry(attempt.id(), attempt.exerciseId()).status)
                .isEqualTo("NOT_RUN");
        assertThat(attempt.directory().resolve("ExampleRunner.java")).doesNotExist();
    }

    @Test
    void customInputCanBeDebuggedAndPausesAtABreakpointInDurableSolution() throws Exception {
        var solution = LocalFileSystem.getInstance().refreshAndFindFileByNioFile(attempt.solution());
        assertThat(solution).isNotNull();
        CountDownLatch paused = new CountDownLatch(1);
        CountDownLatch stopped = new CountDownLatch(1);
        AtomicReference<com.intellij.xdebugger.XDebugSession> session = new AtomicReference<>();
        AtomicReference<ApplicationConfiguration> configuration = new AtomicReference<>();
        fixture.getProject().getMessageBus().connect(fixture.getTestRootDisposable()).subscribe(
                ExecutionManager.EXECUTION_TOPIC, new ExecutionListener() {
                    @Override
                    public void processStarted(String executorId, ExecutionEnvironment environment,
                                               ProcessHandler handler) {
                        if (environment.getRunProfile() instanceof ApplicationConfiguration application) {
                            configuration.set(application);
                        }
                    }
                });
        fixture.getProject().getMessageBus().connect(fixture.getTestRootDisposable()).subscribe(
                com.intellij.xdebugger.XDebuggerManager.TOPIC,
                new com.intellij.xdebugger.XDebuggerManagerListener() {
                    @Override
                    public void processStarted(com.intellij.xdebugger.XDebugProcess process) {
                        session.set(process.getSession());
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
                    .addLineBreakpoint(type, solution.getUrl(), 8, type.createBreakpointProperties(solution, 8));
        }));
        PracticeRunner runner = fixture.getProject().getService(PracticeRunner.class);
        try {
            EdtTestUtil.runInEdtAndWait(() -> {
                ((com.intellij.execution.impl.ExecutionManagerImpl) ExecutionManager
                        .getInstance(fixture.getProject())).setForceCompilationInTests(true);
                runner.debugExamples(attempt.id(), "[2, 7, 11, 15] 9");
            });
            assertThat(paused.await(180, TimeUnit.SECONDS)).isTrue();
            assertThat(session.get()).isNotNull();
            EdtTestUtil.runInEdtAndWait(() -> {
                assertThat(session.get().isSuspended()).isTrue();
                assertThat(session.get().getCurrentPosition().getFile()).isEqualTo(solution);
                assertThat(configuration.get()).isNotNull();
                assertThat(configuration.get().getProgramParameters())
                        .as("debugging custom input must pass the typed case to the runner")
                        .isEqualTo("\"[2, 7, 11, 15] 9\"");
                assertThat(ManagedPracticeProgress.get().entry(attempt.id(), attempt.exerciseId()).status)
                        .isEqualTo("NOT_RUN");
            });
        } finally {
            EdtTestUtil.runInEdtAndWait(() -> {
                runner.stop();
                WriteAction.run(() -> com.intellij.xdebugger.XDebuggerManager.getInstance(fixture.getProject())
                        .getBreakpointManager().removeBreakpoint(breakpoint));
            });
            if (session.get() != null) {
                assertThat(stopped.await(30, TimeUnit.SECONDS)).isTrue();
            }
        }
    }

    @Test
    void visibleExampleSuccessIsRejectedWhenSolutionChangesDuringRun() throws Exception {
            Files.writeString(attempt.solution(), slowPairSum(), StandardCharsets.UTF_8);
            CountDownLatch started = new CountDownLatch(1);
            CountDownLatch completed = new CountDownLatch(1);
            AtomicReference<String> result = new AtomicReference<>("");
            fixture.getProject().getMessageBus().connect(fixture.getTestRootDisposable()).subscribe(
                    ExecutionManager.EXECUTION_TOPIC, new ExecutionListener() {
                        @Override
                        public void processStarted(String executorId, ExecutionEnvironment environment,
                                                   ProcessHandler handler) {
                            if (environment.getRunProfile() instanceof ApplicationConfiguration) {
                                started.countDown();
                            }
                        }
                    });
            PracticeRunner runner = fixture.getProject().getService(PracticeRunner.class);
            runner.addListener(text -> {
                result.set(text);
                if (text.startsWith("STALE:")) {
                    completed.countDown();
                }
            });

            EdtTestUtil.runInEdtAndWait(() -> runner.runExamples(attempt.id()));
            assertThat(started.await(90, TimeUnit.SECONDS)).isTrue();
            Files.writeString(attempt.solution(), "\n// changed during examples\n",
                    StandardCharsets.UTF_8, java.nio.file.StandardOpenOption.APPEND);

            assertThat(completed.await(90, TimeUnit.SECONDS)).as(result.get()).isTrue();
            assertThat(result.get()).startsWith("STALE:");
            assertThat(ManagedPracticeProgress.get().entry(attempt.id(), attempt.exerciseId()).status)
                    .isEqualTo("NOT_RUN");
    }

    @Test
    void invalidVisibleSolutionReportsCompilationFailureWithoutLaunching() throws Exception {
            Files.writeString(attempt.solution(), "not valid Java", StandardCharsets.UTF_8);
            CountDownLatch completed = new CountDownLatch(1);
            AtomicReference<String> result = new AtomicReference<>("");
            PracticeRunner runner = fixture.getProject().getService(PracticeRunner.class);
            runner.addListener(text -> {
                result.set(text);
                if (text.startsWith("Example compilation failed:")) {
                    completed.countDown();
                }
            });

            EdtTestUtil.runInEdtAndWait(() -> runner.runExamples(attempt.id()));

            assertThat(completed.await(30, TimeUnit.SECONDS)).as(result.get()).isTrue();
            assertThat(runner.isRunning()).isFalse();
            assertThat(ManagedPracticeProgress.get().entry(attempt.id(), attempt.exerciseId()).status)
                    .isEqualTo("NOT_RUN");
    }

    private static String slowPairSum() {
        return """
                package com.jinloes.practice;
                public class Solution {
                    public static int[] findPair(int[] numbers, int target) {
                        try { Thread.sleep(1500); }
                        catch (InterruptedException interrupted) {
                            Thread.currentThread().interrupt();
                            throw new IllegalStateException(interrupted);
                        }
                        for (int left = 0; left < numbers.length; left++) {
                            for (int right = left + 1; right < numbers.length; right++) {
                                if ((long) numbers[left] + numbers[right] == target) {
                                    return new int[]{left, right};
                                }
                            }
                        }
                        return new int[0];
                    }
                }
                """;
    }

    private static void invoke(Method method, Object target, Object argument) throws Exception {
        try {
            method.invoke(target, argument);
        } catch (InvocationTargetException exception) {
            Throwable cause = exception.getCause();
            if (cause instanceof Exception checked) {
                throw checked;
            }
            if (cause instanceof Error error) {
                throw error;
            }
            throw new RuntimeException(cause);
        }
    }

}
