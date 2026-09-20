package com.jinloes.practice_plugin.integration;

import com.intellij.execution.ExecutionListener;
import com.intellij.execution.ExecutionManager;
import com.intellij.execution.ExecutionResult;
import com.intellij.execution.RunManager;
import com.intellij.execution.application.ApplicationConfiguration;
import com.intellij.execution.configurations.ConfigurationTypeUtil;
import com.intellij.execution.executors.DefaultRunExecutor;
import com.intellij.execution.process.OSProcessHandler;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.runners.ExecutionEnvironmentBuilder;
import com.intellij.execution.runners.ProgramRunner;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.WriteAction;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.projectRoots.JavaSdk;
import com.intellij.openapi.projectRoots.ProjectJdkTable;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.testFramework.EdtTestUtil;
import com.intellij.testFramework.fixtures.IdeaProjectTestFixture;
import com.intellij.testFramework.fixtures.IdeaTestFixtureFactory;
import com.jinloes.practice_plugin.catalog.ExerciseCatalog;
import com.jinloes.practice_plugin.run.PracticeConfigurationType;
import com.jinloes.practice_plugin.run.PracticeRunConfiguration;
import com.jinloes.practice_plugin.run.PracticeRunner;
import com.jinloes.practice_plugin.state.ManagedPracticeProgress;
import com.jinloes.practice_plugin.workspace.ManagedPracticeWorkspace;
import com.jinloes.practice_plugin.workspace.PracticeModuleWorkspace;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import javax.swing.SwingUtilities;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Tag("gradle-integration")
class PracticeExecutionIntegrationTest {
    @TempDir Path temporary;
    private IdeaProjectTestFixture fixture;
    private ManagedPracticeWorkspace workspace;
    private ManagedPracticeWorkspace.Attempt attempt;

    @BeforeEach
    void setUp() throws Exception {
        Path host = temporary.resolve("host");
        Files.createDirectories(host);
        EdtTestUtil.runInEdtAndWait(() -> {
            fixture = IdeaTestFixtureFactory.getFixtureFactory()
                    .createFixtureBuilder("managed-practice-execution", host, false).getFixture();
            fixture.setUp();
            WriteAction.run(() -> ProjectRootManager.getInstance(fixture.getProject()).setProjectSdk(null));
        });
        workspace = new ManagedPracticeWorkspace();
        attempt = workspace.create(ExerciseCatalog.find("pair-sum"));
        Files.writeString(attempt.solution(), referencePairSum(), StandardCharsets.UTF_8);
        var limits = ManagedPracticeProgress.get().getState();
        limits.testSeconds = 5;
        limits.suiteSeconds = 60;
        limits.heapMb = 256;
    }

    @AfterEach
    void tearDown() throws Exception {
        if (fixture != null) {
            fixture.getProject().getService(PracticeRunner.class).stop();
            EdtTestUtil.runInEdtAndWait(() -> {
                PracticeModuleWorkspace.get(fixture.getProject()).dispose();
                fixture.tearDown();
            });
        }
    }

    @Test
    void plainSwingEdtCanLaunchVisibleExamples() throws Exception {
        CountDownLatch completed = new CountDownLatch(1);
        AtomicReference<String> result = new AtomicReference<>("");
        PracticeRunner runner = fixture.getProject().getService(PracticeRunner.class);
        runner.setListener(text -> {
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
    void plainSwingEdtCanStartFullCheck() throws Exception {
        CountDownLatch completed = new CountDownLatch(1);
        AtomicReference<String> result = new AtomicReference<>("");
        PracticeRunner runner = fixture.getProject().getService(PracticeRunner.class);
        runner.setListener(text -> {
            result.set(text);
            if (text.startsWith("Managed attempt:") && text.contains("Full check:")) {
                completed.countDown();
            }
        });

        runOnPlainSwingEdt(() -> runner.check(attempt.id()));

        assertThat(completed.await(360, TimeUnit.SECONDS)).as(result.get()).isTrue();
        ManagedPracticeProgress.Entry entry = ManagedPracticeProgress.get().entry(attempt.id(), attempt.exerciseId());
        assertThat(entry.status).as(entry.details).isEqualTo("PASSED");
    }

    @Test
    void savedDocumentContinuationRetainsReadAndWriteIntentAccess() throws Exception {
        PracticeRunner runner = fixture.getProject().getService(PracticeRunner.class);
        CountDownLatch completed = new CountDownLatch(1);
        AtomicReference<Boolean> readAccess = new AtomicReference<>();
        AtomicReference<Boolean> writeIntent = new AtomicReference<>();
        Class<?> actionType = Class.forName(PracticeRunner.class.getName() + "$ExecutionAction");
        Method runAfterSavingDocuments = PracticeRunner.class.getDeclaredMethod(
                "runAfterSavingDocuments", actionType);
        runAfterSavingDocuments.setAccessible(true);
        Method setDispatcher = PracticeRunner.class.getDeclaredMethod(
                "setContinuationDispatcherForTests", java.util.function.Consumer.class);
        setDispatcher.setAccessible(true);
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

        invoke(setDispatcher, null, (java.util.function.Consumer<Runnable>) executor::execute);
        try {
            runOnPlainSwingEdt(() -> invoke(runAfterSavingDocuments, runner, continuation));

            assertThat(completed.await(30, TimeUnit.SECONDS)).isTrue();
            assertThat(readAccess.get()).isTrue();
            assertThat(writeIntent.get()).isTrue();
        } finally {
            invoke(setDispatcher, null, null);
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
        runner.setListener(text -> {
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
    void nativeDebugPausesAtABreakpointInDurableSolution() throws Exception {
        var solution = LocalFileSystem.getInstance().refreshAndFindFileByNioFile(attempt.solution());
        assertThat(solution).isNotNull();
        CountDownLatch paused = new CountDownLatch(1);
        CountDownLatch stopped = new CountDownLatch(1);
        AtomicReference<com.intellij.xdebugger.XDebugSession> session = new AtomicReference<>();
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
                runner.debugExamples(attempt.id());
            });
            assertThat(paused.await(180, TimeUnit.SECONDS)).isTrue();
            assertThat(session.get()).isNotNull();
            EdtTestUtil.runInEdtAndWait(() -> {
                assertThat(session.get().isSuspended()).isTrue();
                assertThat(session.get().getCurrentPosition().getFile()).isEqualTo(solution);
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
            runner.setListener(text -> {
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
            runner.setListener(text -> {
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

    @Test
    void isolatedFullCheckRecordsOnlyFreshSavedResults() throws Exception {
        ManagedPracticeProgress.Entry entry = awaitFullCheck(launchFullCheck());
        assertThat(entry.status).as(entry.details).isEqualTo("PASSED");
        assertThat(entry.tests).isEqualTo(ExerciseCatalog.find(attempt.exerciseId()).fullCount());
        assertThat(entry.checkedFingerprint).isEqualTo(workspace.fingerprint(attempt));
    }

    @ParameterizedTest(name = "{0} cannot pass the full check")
    @MethodSource("nonPassingSources")
    void fullCheckRejectsNegativeControls(String ignored, String source) throws Exception {
        Files.writeString(attempt.solution(), source, StandardCharsets.UTF_8);
        ManagedPracticeProgress.Entry entry = awaitFullCheck(launchFullCheck());
        assertThat(entry.status).as(entry.details).isNotEqualTo("PASSED");
        assertThat(entry.lastPassedAt).isEmpty();
    }

    @Test
    void changedSavedSolutionIsReportedAsStale() throws Exception {
        ManagedCheck check = launchFullCheck();
        assertThat(check.started.await(90, TimeUnit.SECONDS)).isTrue();
        Files.writeString(attempt.solution(), "\n// changed while checking\n",
                StandardCharsets.UTF_8, java.nio.file.StandardOpenOption.APPEND);

        ManagedPracticeProgress.Entry entry = awaitFullCheck(check);

        assertThat(entry.status).isEqualTo("RUNNER_ERROR");
        assertThat(entry.details).contains("STALE:");
        assertThat(entry.checkedFingerprint).isNotEqualTo(workspace.fingerprint(attempt));
    }

    @Test
    void cancellationStopsOnlyTheOwnedCheckProcess() throws Exception {
        Files.writeString(attempt.solution(), nonCooperativePairSum(), StandardCharsets.UTF_8);
        ManagedCheck check = launchFullCheck();
        assertThat(check.started.await(90, TimeUnit.SECONDS)).isTrue();
        assertThat(check.handler.get()).isInstanceOf(OSProcessHandler.class);
        Process process = ((OSProcessHandler) check.handler.get()).getProcess();
        Set<Long> owned = java.util.concurrent.ConcurrentHashMap.newKeySet();
        owned.add(process.pid());
        process.descendants().map(ProcessHandle::pid).forEach(owned::add);

        EdtTestUtil.runInEdtAndWait(() -> fixture.getProject().getService(PracticeRunner.class).stop());
        ManagedPracticeProgress.Entry entry = awaitFullCheck(check);

        assertThat(entry.status).isEqualTo("CANCELLED");
        assertThat(check.handler.get().isProcessTerminated()).isTrue();
        assertThat(owned).allSatisfy(pid ->
                assertThat(ProcessHandle.of(pid).map(ProcessHandle::isAlive).orElse(false)).isFalse());
    }

    @Test
    void fullCheckTimesOutAndDoesNotRecordAPass() throws Exception {
        Files.writeString(attempt.solution(), nonCooperativePairSum(), StandardCharsets.UTF_8);
        var limits = ManagedPracticeProgress.get().getState();
        limits.testSeconds = 1;
        limits.suiteSeconds = 2;

        ManagedPracticeProgress.Entry entry = awaitFullCheck(launchFullCheck());

        assertThat(entry.status).as(entry.details).isEqualTo("TIMED_OUT");
        assertThat(entry.lastPassedAt).isEmpty();
    }

    @Test
    void oneExecutionAtATimeIsEnforced() throws Exception {
        Files.writeString(attempt.solution(), nonCooperativePairSum(), StandardCharsets.UTF_8);
        ManagedCheck check = launchFullCheck();
        assertThat(check.started.await(90, TimeUnit.SECONDS)).isTrue();
        PracticeRunner runner = fixture.getProject().getService(PracticeRunner.class);
        assertThatThrownBy(() -> runner.runExamples(attempt.id()))
                .isInstanceOf(com.intellij.execution.ExecutionException.class)
                .hasMessageContaining("already active");
        runner.stop();
        awaitFullCheck(check);
    }

    private ManagedCheck launchFullCheck() throws Exception {
        ManagedCheck check = new ManagedCheck();
        PracticeRunner runner = fixture.getProject().getService(PracticeRunner.class);
        runner.setListener(text -> {
            check.status.set(text);
            if (text.startsWith("Managed attempt:") && text.contains("Full check:")) {
                check.completed.countDown();
            }
        });
        ExecutionResult execution = start(attempt.id());
        Disposer.register(fixture.getTestRootDisposable(), execution.getExecutionConsole());
        check.handler.set(execution.getProcessHandler());
        check.started.countDown();
        execution.getProcessHandler().startNotify();
        return check;
    }

    private ManagedPracticeProgress.Entry awaitFullCheck(ManagedCheck check) throws Exception {
        assertThat(check.completed.await(360, TimeUnit.SECONDS)).as(check.status.get()).isTrue();
        return ManagedPracticeProgress.get().entry(attempt.id(), attempt.exerciseId());
    }

    private ExecutionResult start(String attemptId) throws Exception {
        var environment = EdtTestUtil.runInEdtAndGet(() -> {
            var factory = ConfigurationTypeUtil.findConfigurationType(PracticeConfigurationType.class)
                    .getConfigurationFactories()[0];
            var settings = RunManager.getInstance(fixture.getProject()).createConfiguration("Full check", factory);
            var configuration = (PracticeRunConfiguration) settings.getConfiguration();
            configuration.attemptId = attemptId;
            return ExecutionEnvironmentBuilder.create(DefaultRunExecutor.getRunExecutorInstance(), settings).build();
        });
        var executor = DefaultRunExecutor.getRunExecutorInstance();
        return environment.getState().execute(
                executor, ProgramRunner.getRunner(executor.getId(), environment.getRunProfile()));
    }

    private static Stream<Arguments> nonPassingSources() {
        return Stream.of(
                Arguments.of("starter", ExerciseCatalog.resource(
                        ExerciseCatalog.find("pair-sum"), "Solution.java")),
                Arguments.of("wrong answer", """
                        package com.jinloes.practice;
                        public class Solution {
                            public static int[] findPair(int[] numbers, int target) { return new int[0]; }
                        }
                        """),
                Arguments.of("compile failure", """
                        package com.jinloes.practice;
                        public class Solution {
                            public static int[] findPair(int[] numbers, int target) { return; }
                        }
                        """),
                Arguments.of("runtime failure", """
                        package com.jinloes.practice;
                        public class Solution {
                            public static int[] findPair(int[] numbers, int target) {
                                throw new IllegalStateException("failure");
                            }
                        }
                        """));
    }

    private static String referencePairSum() throws Exception {
        try (var input = PracticeExecutionIntegrationTest.class
                .getResourceAsStream("/reference/pair-sum/Solution.java")) {
            assertThat(input).isNotNull();
            return new String(input.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    private static String nonCooperativePairSum() {
        return """
                package com.jinloes.practice;
                public class Solution {
                    public static int[] findPair(int[] numbers, int target) {
                        while (true) { Thread.onSpinWait(); }
                    }
                }
                """;
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

    private static void runOnPlainSwingEdt(CheckedRunnable action) throws Exception {
        AtomicReference<Throwable> failure = new AtomicReference<>();
        SwingUtilities.invokeAndWait(() -> {
            try {
                action.run();
            } catch (Throwable exception) {
                failure.set(exception);
            }
        });
        Throwable exception = failure.get();
        if (exception == null) {
            return;
        }
        if (exception instanceof Exception checked) {
            throw checked;
        }
        if (exception instanceof Error error) {
            throw error;
        }
        throw new RuntimeException(exception);
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

    @FunctionalInterface
    private interface CheckedRunnable {
        void run() throws Exception;
    }

    private static final class ManagedCheck {
        private final CountDownLatch started = new CountDownLatch(1);
        private final CountDownLatch completed = new CountDownLatch(1);
        private final AtomicReference<String> status = new AtomicReference<>("");
        private final AtomicReference<ProcessHandler> handler = new AtomicReference<>();
    }
}
