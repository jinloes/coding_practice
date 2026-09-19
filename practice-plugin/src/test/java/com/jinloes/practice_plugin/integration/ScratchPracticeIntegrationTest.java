package com.jinloes.practice_plugin.integration;

import com.intellij.codeInsight.daemon.impl.HighlightInfo;
import com.intellij.codeInsight.daemon.impl.MainPassesRunner;
import com.intellij.execution.ExecutionListener;
import com.intellij.execution.ExecutionManager;
import com.intellij.execution.process.OSProcessHandler;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.openapi.application.WriteAction;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.application.PathManager;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.util.ProgressIndicatorBase;
import com.intellij.openapi.projectRoots.ProjectJdkTable;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.profile.codeInspection.InspectionProjectProfileManager;
import com.intellij.testFramework.EdtTestUtil;
import com.intellij.testFramework.fixtures.IdeaProjectTestFixture;
import com.intellij.testFramework.fixtures.IdeaTestFixtureFactory;
import com.jinloes.practice_plugin.catalog.ExerciseCatalog;
import com.jinloes.practice_plugin.run.PracticeRunConfiguration;
import com.jinloes.practice_plugin.run.PracticeRunner;
import com.jinloes.practice_plugin.state.ScratchPracticeProgress;
import com.jinloes.practice_plugin.workspace.PracticeSupportEnvironment;
import com.jinloes.practice_plugin.workspace.ScratchAttemptStore;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("gradle-integration")
class ScratchPracticeIntegrationTest {
    @TempDir
    Path temporary;
    private IdeaProjectTestFixture fixture;
    private Path host;
    private ScratchAttemptStore store;
    private ScratchAttemptStore.Attempt attempt;

    @BeforeEach
    void setUp() throws Exception {
        host = temporary.resolve("host");
        Files.createDirectories(host);
        EdtTestUtil.runInEdtAndWait(() -> {
            fixture = IdeaTestFixtureFactory.getFixtureFactory()
                    .createFixtureBuilder("scratch-practice", host, false).getFixture();
            fixture.setUp();
            WriteAction.run(() -> ProjectRootManager.getInstance(fixture.getProject()).setProjectSdk(null));
        });
        store = new ScratchAttemptStore(fixture.getProject());
        attempt = store.create(ExerciseCatalog.find("pair-sum"));
        Files.writeString(attempt.solution(), workingPairSumScratch(), StandardCharsets.UTF_8);
        var limits = ScratchPracticeProgress.get().getState();
        limits.testSeconds = 5;
        limits.suiteSeconds = 60;
        limits.heapMb = 256;
    }

    @AfterEach
    void tearDown() throws Exception {
        if (fixture != null) {
            fixture.getProject().getService(PracticeRunner.class).stop();
            PracticeSupportEnvironment.get(fixture.getProject()).dispose();
            EdtTestUtil.runInEdtAndWait(fixture::tearDown);
        }
    }

    @Test
    void scratchSupportAssignsAndRestoresTheProjectSdk() throws Exception {
        var support = PracticeSupportEnvironment.get(fixture.getProject());
        var module = EdtTestUtil.runInEdtAndGet(support::module);
        var moduleSdk = ModuleRootManager.getInstance(module).getSdk();

        assertThat(ProjectRootManager.getInstance(fixture.getProject()).getProjectSdk()).isSameAs(moduleSdk);
        assertThat(ModuleRootManager.getInstance(module).getContentEntries()).isEmpty();
        assertThat(moduleSdk).isNotNull();
        assertThat(ModuleManager.getInstance(fixture.getProject()).getModules()).contains(module);
        assertThat(ProjectJdkTable.getInstance().getAllJdks()).isNotEmpty();

        support.dispose();

        assertThat(ProjectRootManager.getInstance(fixture.getProject()).getProjectSdk()).isNull();
    }

    @Test
    void openingScratchSupportProducesNoUnresolvedJavaLangHighlights() throws Exception {
        VirtualFile solution = LocalFileSystem.getInstance().refreshAndFindFileByNioFile(attempt.solution());
        assertThat(solution).isNotNull();

        EdtTestUtil.runInEdtAndWait(() -> {
            PracticeSupportEnvironment.get(fixture.getProject()).module();
            FileEditorManager.getInstance(fixture.getProject()).openFile(solution, true);
        });

        var highlights = ProgressManager.getInstance().runProcess(
                () -> new MainPassesRunner(
                        fixture.getProject(),
                        "Checking scratch Java highlighting",
                        InspectionProjectProfileManager.getInstance(fixture.getProject()).getCurrentProfile())
                        .runMainPasses(List.of(solution)),
                new ProgressIndicatorBase());

        assertThat(ProjectRootManager.getInstance(fixture.getProject()).getProjectSdk()).isNotNull();
        assertThat(highlights.values().stream()
                .flatMap(List::stream)
                .map(HighlightInfo::getDescription)
                .filter(description -> description != null && description.contains("Cannot resolve symbol"))
                .toList()).isEmpty();
    }

    @Test
    void nativeRunUsesTheScratchMainWithoutAProjectImport() throws Exception {
        CountDownLatch completed = new CountDownLatch(1);
        AtomicReference<String> status = new AtomicReference<>("");
        PracticeRunner runner = fixture.getProject().getService(PracticeRunner.class);
        runner.setListener(text -> {
            status.set(text);
            if (text.startsWith("Scratch example session finished")) {
                completed.countDown();
            }
        });

        EdtTestUtil.runInEdtAndWait(() -> runner.runScratch(attempt.id(), false));

        assertThat(completed.await(90, TimeUnit.SECONDS)).as(status.get()).isTrue();
        assertThat(runner.isRunning()).isFalse();
        assertThat(host.resolve("attempts")).doesNotExist();
    }

    @Test
    void isolatedFullCheckRecordsOnlyFreshSavedScratchResults() throws Exception {
        ScratchPracticeProgress.Entry entry = awaitFullCheck(launchFullCheck());
        assertThat(entry.status).as(entry.details).isEqualTo("PASSED");
        assertThat(entry.tests).isEqualTo(ExerciseCatalog.find(attempt.exerciseId()).fullCount());
        assertThat(entry.checkedFingerprint).isEqualTo(store.fingerprint(attempt));
    }

    @Test
    void nativeScratchDebugPausesInLearnerCodeWithoutPluginImplementationClasses() throws Exception {
        Files.writeString(attempt.solution(), debuggablePairSumScratch(), StandardCharsets.UTF_8);
        VirtualFile solution = LocalFileSystem.getInstance().refreshAndFindFileByNioFile(attempt.solution());
        assertThat(solution).isNotNull();

        CountDownLatch paused = new CountDownLatch(1);
        CountDownLatch stopped = new CountDownLatch(1);
        StringBuffer debugOutput = new StringBuffer();
        AtomicReference<com.intellij.xdebugger.XDebugSession> session = new AtomicReference<>();
        AtomicReference<ProcessHandler> process = new AtomicReference<>();
        fixture.getProject().getMessageBus().connect(fixture.getTestRootDisposable()).subscribe(
                com.intellij.xdebugger.XDebuggerManager.TOPIC, new com.intellij.xdebugger.XDebuggerManagerListener() {
                    @Override
                    public void processStarted(com.intellij.xdebugger.XDebugProcess debugProcess) {
                        session.set(debugProcess.getSession());
                        process.set(debugProcess.getProcessHandler());
                        debugProcess.getProcessHandler().addProcessListener(
                                new com.intellij.execution.process.ProcessListener() {
                                    @Override
                                    public void onTextAvailable(com.intellij.execution.process.ProcessEvent event,
                                                                com.intellij.openapi.util.Key outputType) {
                                        debugOutput.append(event.getText());
                                    }
                                });
                        debugProcess.getSession().addSessionListener(new com.intellij.xdebugger.XDebugSessionListener() {
                            @Override
                            public void sessionPaused() {
                                paused.countDown();
                            }

                            @Override
                            public void sessionStopped() {
                                stopped.countDown();
                            }
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
        try {
            EdtTestUtil.runInEdtAndWait(() -> {
                ((com.intellij.execution.impl.ExecutionManagerImpl) ExecutionManager.getInstance(fixture.getProject()))
                        .setForceCompilationInTests(true);
                runner.debugScratch(attempt.id());
            });

            assertThat(paused.await(180, TimeUnit.SECONDS)).as("scratch debugger pauses in Solution.java; output=%s",
                    debugOutput).isTrue();
            assertThat(session.get()).isNotNull();
            EdtTestUtil.runInEdtAndWait(() -> {
                assertThat(session.get().isSuspended()).as("scratch breakpoint hit; output=%s", debugOutput).isTrue();
                assertThat(session.get().getCurrentPosition().getFile()).isEqualTo(solution);
            });
            assertThat(debugOutput.toString()).contains("PLUGIN_CLASS_VISIBLE=false")
                    .doesNotContain("com.jinloes.practice_plugin.");
            assertThat(process.get()).isNotNull();
            if (process.get() instanceof OSProcessHandler osProcess) {
                String arguments = osProcess.getProcess().info().arguments()
                        .map(values -> String.join(" ", values)).orElse("");
                assertThat(arguments).doesNotContain("com.jinloes.practice_plugin.",
                        "practice-plugin/build/classes", "practice-plugin-");
            }
        } finally {
            EdtTestUtil.runInEdtAndWait(() -> {
                runner.stop();
                WriteAction.run(() -> com.intellij.xdebugger.XDebuggerManager.getInstance(fixture.getProject())
                        .getBreakpointManager().removeBreakpoint(breakpoint));
            });
            if (session.get() != null) {
                assertThat(stopped.await(30, TimeUnit.SECONDS)).as("scratch debug session stopped").isTrue();
            }
        }
    }

    @ParameterizedTest(name = "{0} scratch cannot pass the full check")
    @MethodSource("nonPassingScratchSources")
    void scratchFullCheckRejectsEveryNegativeControl(String ignored, String source) throws Exception {
        Files.writeString(attempt.solution(), source, StandardCharsets.UTF_8);

        ScratchPracticeProgress.Entry entry = awaitFullCheck(launchFullCheck());

        assertThat(entry.status).as(entry.details).isNotEqualTo("PASSED");
        assertThat(entry.lastPassedAt).isEmpty();
    }

    private static Stream<Arguments> nonPassingScratchSources() throws Exception {
        return Stream.of(
                Arguments.of("unmodified starter", ExerciseCatalog.resource(
                        ExerciseCatalog.find("pair-sum"), "Solution.java")),
                Arguments.of("wrong but compiling", """
                        package com.jinloes.practice;
                        public class Solution {
                            public static int[] findPair(int[] numbers, int target) {
                                return new int[0];
                            }
                        }
                        """),
                Arguments.of("compile failure", """
                        package com.jinloes.practice;
                        public class Solution {
                            public static int[] findPair(int[] numbers, int target) {
                                return;
                            }
                        }
                        """),
                Arguments.of("runtime exception", """
                        package com.jinloes.practice;
                        public class Solution {
                            public static int[] findPair(int[] numbers, int target) {
                                throw new IllegalStateException("scratch runtime failure");
                            }
                        }
                        """),
                Arguments.of("assertion failure", """
                        package com.jinloes.practice;
                        public class Solution {
                            public static int[] findPair(int[] numbers, int target) {
                                return new int[]{0, 0};
                            }
                        }
                        """));
    }

    @Test
    void changedSavedScratchIsRecordedAsStaleRunnerError() throws Exception {
        ScratchCheck check = launchFullCheck();
        assertThat(check.started.await(90, TimeUnit.SECONDS)).as("scratch full check starts").isTrue();
        Files.writeString(attempt.solution(), "\n// changed after the check fingerprint was captured\n",
                StandardCharsets.UTF_8, java.nio.file.StandardOpenOption.APPEND);

        ScratchPracticeProgress.Entry entry = awaitFullCheck(check);

        assertThat(entry.status).as(entry.details).isEqualTo("RUNNER_ERROR");
        assertThat(entry.details).contains("STALE:");
        assertThat(entry.checkedFingerprint).isNotEqualTo(store.fingerprint(attempt));
    }

    @Test
    void scratchFullCheckRejectsAReportWithTheWrongTestCount() throws Exception {
        Files.writeString(attempt.solution(), extraReportPairSumScratch(), StandardCharsets.UTF_8);

        ScratchPracticeProgress.Entry entry = awaitFullCheck(launchFullCheck());

        assertThat(entry.status).as(entry.details).isEqualTo("RUNNER_ERROR");
        assertThat(entry.tests).isEqualTo(ExerciseCatalog.find(attempt.exerciseId()).fullCount() + 1);
        assertThat(entry.lastPassedAt).isEmpty();
    }

    @Test
    void scratchFullCheckCancellationStopsTheOwnedProcessTree() throws Exception {
        Files.writeString(attempt.solution(), nonCooperativePairSumScratch(), StandardCharsets.UTF_8);
        ScratchCheck check = launchFullCheck();
        assertThat(check.started.await(90, TimeUnit.SECONDS)).as("scratch checker wrapper starts").isTrue();
        awaitVerificationFile("learner-entered");
        captureOwnedProcesses(check);

        EdtTestUtil.runInEdtAndWait(() -> fixture.getProject().getService(PracticeRunner.class).stop());
        ScratchPracticeProgress.Entry entry = awaitFullCheck(check);

        assertThat(entry.status).as(entry.details).isEqualTo("CANCELLED");
        assertThat(entry.lastPassedAt).isEmpty();
        assertNoOwnedProcessRemains(check);
    }

    @Test
    void scratchFullCheckTimeoutStopsANonCooperativeOwnedProcessTree() throws Exception {
        Files.writeString(attempt.solution(), nonCooperativePairSumScratch(), StandardCharsets.UTF_8);
        var limits = ScratchPracticeProgress.get().getState();
        limits.testSeconds = 1;
        limits.suiteSeconds = 10;
        ScratchCheck check = launchFullCheck();
        assertThat(check.started.await(90, TimeUnit.SECONDS)).as("scratch checker wrapper starts").isTrue();
        awaitVerificationFile("learner-entered");
        captureOwnedProcesses(check);

        ScratchPracticeProgress.Entry entry = awaitFullCheck(check);

        assertThat(entry.status).as(entry.details).isEqualTo("TIMED_OUT");
        assertThat(entry.lastPassedAt).isEmpty();
        assertNoOwnedProcessRemains(check);
    }

    private ScratchCheck launchFullCheck() throws Exception {
        ScratchCheck check = new ScratchCheck();
        PracticeRunner runner = fixture.getProject().getService(PracticeRunner.class);
        runner.setListener(text -> {
            check.status.set(text);
            if (text.startsWith("Scratch attempt:") && text.contains("Full check:")) {
                check.completed.countDown();
            }
        });
        fixture.getProject().getMessageBus().connect(fixture.getTestRootDisposable()).subscribe(
                ExecutionManager.EXECUTION_TOPIC, new ExecutionListener() {
                    @Override
                    public void processStarted(String executorId, ExecutionEnvironment environment,
                                               ProcessHandler handler) {
                        if (environment.getRunProfile() instanceof PracticeRunConfiguration configuration
                                && configuration.kind == PracticeRunConfiguration.Kind.SCRATCH
                                && attempt.id().equals(configuration.attemptId)) {
                            check.handler.set(handler);
                            check.started.countDown();
                        }
                    }
                });
        EdtTestUtil.runInEdtAndWait(() -> runner.runScratch(attempt.id(), true));
        return check;
    }

    private ScratchPracticeProgress.Entry awaitFullCheck(ScratchCheck check) throws Exception {
        assertThat(check.completed.await(360, TimeUnit.SECONDS)).as(check.status.get()).isTrue();
        return ScratchPracticeProgress.get().entry(attempt.id(), attempt.exerciseId());
    }

    private Path awaitVerificationFile(String name) throws Exception {
        Path verifierRoot = Path.of(PathManager.getSystemPath(), "algorithm-practice", "verification");
        long deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(90);
        while (System.nanoTime() < deadline) {
            if (Files.isDirectory(verifierRoot)) {
                try (var workspaces = Files.list(verifierRoot)) {
                    for (Path workspace : workspaces.toList()) {
                        Path marker = workspace.resolve(".practice-results").resolve(name);
                        if (Files.isRegularFile(marker)) {
                            return marker;
                        }
                    }
                }
            }
            Thread.sleep(100);
        }
        throw new AssertionError("The scratch checker did not reach " + name + " within 90 seconds.");
    }

    private static void captureOwnedProcesses(ScratchCheck check) {
        assertThat(check.handler.get()).isInstanceOf(OSProcessHandler.class);
        Process process = ((OSProcessHandler) check.handler.get()).getProcess();
        check.ownedPids.add(process.pid());
        process.descendants().map(ProcessHandle::pid).forEach(check.ownedPids::add);
    }

    private static void assertNoOwnedProcessRemains(ScratchCheck check) {
        assertThat(check.handler.get()).isNotNull();
        assertThat(check.handler.get().isProcessTerminated()).isTrue();
        if (check.handler.get() instanceof OSProcessHandler osProcess) {
            assertThat(osProcess.getProcess().isAlive()).isFalse();
        }
        assertThat(check.ownedPids).allSatisfy(pid ->
                assertThat(ProcessHandle.of(pid).map(ProcessHandle::isAlive).orElse(false))
                        .as("owned process %s is stopped", pid).isFalse());
    }

    private static final class ScratchCheck {
        private final CountDownLatch started = new CountDownLatch(1);
        private final CountDownLatch completed = new CountDownLatch(1);
        private final AtomicReference<String> status = new AtomicReference<>("");
        private final AtomicReference<ProcessHandler> handler = new AtomicReference<>();
        private final Set<Long> ownedPids = java.util.concurrent.ConcurrentHashMap.newKeySet();
    }

    private static String workingPairSumScratch() {
        return """
                package com.jinloes.practice;

                public class Solution {
                    public static int[] findPair(int[] numbers, int target) {
                        for (int left = 0; left < numbers.length; left++) {
                            for (int right = left + 1; right < numbers.length; right++) {
                                if ((long) numbers[left] + numbers[right] == target) {
                                    return new int[]{left, right};
                                }
                            }
                        }
                        return new int[0];
                    }

                    public static void main(String[] args) {
                        assertPair(findPair(new int[]{2, 7, 11, 15}, 9), new int[]{2, 7, 11, 15}, 9);
                        assertPair(findPair(new int[]{3, 3}, 6), new int[]{3, 3}, 6);
                        assertPair(findPair(new int[]{1, 2, 3}, 7), new int[]{1, 2, 3}, 7);
                        System.out.println("Examples passed: 3");
                    }

                    private static void assertPair(int[] pair, int[] numbers, int target) {
                        if (pair.length == 0 && target == 7) return;
                        if (pair.length != 2 || pair[0] == pair[1] || pair[0] < 0 || pair[1] < 0
                                || pair[0] >= numbers.length || pair[1] >= numbers.length
                                || (long) numbers[pair[0]] + numbers[pair[1]] != target) {
                            throw new AssertionError("Expected a valid pair");
                        }
                    }
                }
                """;
    }

    private static String debuggablePairSumScratch() {
        return """
                package com.jinloes.practice;

                public class Solution {
                    public static int[] findPair(int[] numbers, int target) {
                        for (int left = 0; left < numbers.length; left++) {
                            for (int right = left + 1; right < numbers.length; right++) {
                                if ((long) numbers[left] + numbers[right] == target) {
                                    return new int[]{left, right};
                                }
                            }
                        }
                        return new int[0];
                    }

                    public static void main(String[] args) throws Exception {
                        boolean pluginClassVisible;
                        try {
                            Class.forName("com.jinloes.practice_plugin.run.PracticeRunner");
                            pluginClassVisible = true;
                        } catch (ClassNotFoundException expected) {
                            pluginClassVisible = false;
                        }
                        System.out.println("PLUGIN_CLASS_VISIBLE=" + pluginClassVisible);
                        Thread.sleep(500);
                        findPair(new int[]{2, 7}, 9);
                    }
                }
                """;
    }

    private static String extraReportPairSumScratch() {
        return """
                package com.jinloes.practice;

                import java.io.IOException;
                import java.nio.file.Files;
                import java.nio.file.Path;
                import java.util.concurrent.atomic.AtomicBoolean;

                public class Solution {
                    private static final AtomicBoolean REPORT_WRITER_STARTED = new AtomicBoolean();

                    public static int[] findPair(int[] numbers, int target) {
                        startUnexpectedReportWriter();
                        for (int left = 0; left < numbers.length; left++) {
                            for (int right = left + 1; right < numbers.length; right++) {
                                if ((long) numbers[left] + numbers[right] == target) {
                                    return new int[]{left, right};
                                }
                            }
                        }
                        return new int[0];
                    }

                    public static void main(String[] arguments) {
                        if (arguments.length == 1 && "write-wrong-report".equals(arguments[0])) {
                            writeUnexpectedReportAfterGradleReports();
                        }
                    }

                    private static void startUnexpectedReportWriter() {
                        if (!REPORT_WRITER_STARTED.compareAndSet(false, true)) {
                            return;
                        }
                        try {
                            String java = Path.of(System.getProperty("java.home"), "bin", "java").toString();
                            new ProcessBuilder(java, "-classpath", System.getProperty("java.class.path"),
                                    Solution.class.getName(), "write-wrong-report").start();
                        } catch (IOException failure) {
                            throw new AssertionError(failure);
                        }
                    }

                    private static void writeUnexpectedReportAfterGradleReports() {
                        Path reports = Path.of(".practice-results");
                        Path examples = reports.resolve("TEST-com.jinloes.practice.ExamplesTest.xml");
                        Path correctness = reports.resolve("TEST-com.jinloes.practice.CorrectnessTest.xml");
                        for (int attempt = 0; attempt < 300; attempt++) {
                            if (Files.isRegularFile(examples) && Files.isRegularFile(correctness)) {
                                try {
                                    Files.writeString(reports.resolve("TEST-wrong-count.xml"),
                                            "<testsuite name=\\"wrong-count\\"><testcase name=\\"unexpected\\"/></testsuite>");
                                    return;
                                } catch (IOException ignored) {
                                    return;
                                }
                            }
                            try {
                                Thread.sleep(10);
                            } catch (InterruptedException interrupted) {
                                Thread.currentThread().interrupt();
                                return;
                            }
                        }
                    }
                }
                """;
    }

    private static String nonCooperativePairSumScratch() {
        return """
                package com.jinloes.practice;

                import java.io.IOException;
                import java.nio.file.Files;
                import java.nio.file.Path;

                public class Solution {
                    public static int[] findPair(int[] numbers, int target) {
                        try {
                            Path reports = Path.of(".practice-results");
                            Files.createDirectories(reports);
                            Files.writeString(reports.resolve("learner-entered"), "entered");
                        } catch (IOException failure) {
                            throw new AssertionError(failure);
                        }
                        while (true) {
                            Thread.onSpinWait();
                        }
                    }
                }
                """;
    }
}
