package com.jinloes.practice_plugin.run;

import com.intellij.testFramework.EdtTestUtil;
import com.intellij.testFramework.fixtures.IdeaProjectTestFixture;
import com.intellij.testFramework.fixtures.IdeaTestFixtureFactory;
import com.jinloes.practice_plugin.catalog.ExerciseCatalog;
import com.jinloes.practice_plugin.workspace.ManagedPracticeWorkspace;
import com.jinloes.practice_plugin.workspace.VerificationWorkspace;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.*;

class FullCheckWorkspaceCleanupPathsTest {
    private IdeaProjectTestFixture fixture;
    private Path host;

    @BeforeEach
    void setUpApplication() throws Exception {
        host = Path.of("build", "agent-fixtures", "full-check-cleanup-" + System.nanoTime())
                .toAbsolutePath().normalize();
        Files.createDirectories(host);
        EdtTestUtil.runInEdtAndWait(() -> {
            fixture = IdeaTestFixtureFactory.getFixtureFactory()
                    .createFixtureBuilder("full-check-cleanup", host, false).getFixture();
            fixture.setUp();
        });
    }

    @AfterEach
    void tearDownApplication() throws Exception {
        if (fixture != null) {
            EdtTestUtil.runInEdtAndWait(() -> fixture.tearDown());
        }
        if (host != null && Files.exists(host)) {
            try (var stream = Files.walk(host)) {
                stream.sorted(Comparator.reverseOrder()).forEach(path -> {
                    try {
                        Files.deleteIfExists(path);
                    } catch (Exception ignored) {
                    }
                });
            }
        }
    }

    @Test
    void failureAfterListenerRegistrationReleasesHandlerWithoutCompletingCheck() throws Exception {
        var project = fixture.getProject();
        var attempts = ManagedPracticeWorkspace.get();
        var attempt = attempts.create(ExerciseCatalog.find("pair-sum"));
        var acquired = new java.util.concurrent.atomic.AtomicReference<Process>();
        var completed = new java.util.concurrent.atomic.AtomicBoolean();
        var control = new RunSession.RunControl();
        project.putUserData(FullCheck.COMMAND_SETUP, command -> command.setProcessCreator(builder -> {
            builder.command(TestChildProcess.command("waiting"));
            builder.environment().remove("JAVA_TOOL_OPTIONS");
            builder.environment().remove("JDK_JAVA_OPTIONS");
            try {
                Process process = builder.start();
                acquired.set(process);
                return process;
            } catch (java.io.IOException exception) {
                throw new java.io.UncheckedIOException(exception);
            }
        }));
        try {
            FullCheck check = new FullCheck(project, attempts,
                    text -> {
                        throw new IllegalArgumentException("launch notification failed");
                    },
                    () -> {
                    }, (finishedAttempt, fingerprint, result) -> completed.set(true));

            assertThatThrownBy(() -> EdtTestUtil.runInEdtAndWait(() -> check.start(attempt.id(), control)))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("launch notification failed");

            assertThat(acquired.get().isAlive()).isFalse();
            assertThat(control.active()).isNull();
            assertThat(control.stopReason()).isNull();
            EdtTestUtil.runInEdtAndWait(com.intellij.testFramework.PlatformTestUtil::dispatchAllInvocationEventsInIdeEventQueue);
            assertThat(completed).isFalse();

            FullCheck runtimeFailureCheck = new FullCheck(project, attempts,
                    text -> {
                        throw new IllegalStateException("unexpected listener failure");
                    },
                    () -> {
                    }, (finishedAttempt, fingerprint, result) -> completed.set(true));
            assertThatThrownBy(() -> EdtTestUtil.runInEdtAndWait(
                    () -> runtimeFailureCheck.start(attempt.id(), control)))
                    .isInstanceOf(com.intellij.execution.ExecutionException.class)
                    .hasCauseInstanceOf(IllegalStateException.class);
            assertThat(acquired.get().isAlive()).isFalse();

            FullCheck canceledCheck = new FullCheck(project, attempts,
                    text -> {
                        throw new com.intellij.openapi.progress.ProcessCanceledException();
                    },
                    () -> {
                    }, (finishedAttempt, fingerprint, result) -> completed.set(true));
            assertThatThrownBy(() -> EdtTestUtil.runInEdtAndWait(
                    () -> canceledCheck.start(attempt.id(), control)))
                    .isInstanceOf(com.intellij.openapi.progress.ProcessCanceledException.class);
            assertThat(acquired.get().isAlive()).isFalse();
            assertThat(completed).isFalse();
        } finally {
            project.putUserData(FullCheck.COMMAND_SETUP, null);
            destroyProcessTree(acquired.get());
            com.intellij.openapi.util.io.NioFiles.deleteRecursively(attempt.directory());
        }
    }

    @Test
    void releaseFailedHandlerSkipsLiveProcessPreservesInterruptAndSuppressesDrainFailure() {
        var original = new java.io.IOException("launch failed");
        FullCheckLaunchCleanup.releaseFailedHandler(null, null, original);
        FullCheckLaunchCleanup.releaseFailedHandler(new StubProcessHandler(), null, original);

        var liveHandler = new StubProcessHandler();
        FullCheckLaunchCleanup.releaseFailedHandler(liveHandler, new RefusingProcess(false), original);
        assertThat(liveHandler.isStartNotified()).isFalse();
        assertThat(original.getSuppressed()).isEmpty();

        Process deadProcess = new RefusingProcess(false) {
            @Override
            public boolean isAlive() {
                return false;
            }
        };
        var terminatedHandler = new StubProcessHandler();
        Thread.currentThread().interrupt();
        try {
            FullCheckLaunchCleanup.releaseFailedHandler(terminatedHandler, deadProcess, original);
            assertThat(Thread.interrupted()).isTrue();
        } finally {
            Thread.interrupted();
        }
        assertThat(terminatedHandler.isStartNotified()).isTrue();
        assertThat(terminatedHandler.isProcessTerminated()).isTrue();

        var timedOutHandler = new StubProcessHandler() {
            @Override
            public boolean waitFor(long timeoutInMilliseconds) {
                return false;
            }
        };
        FullCheckLaunchCleanup.releaseFailedHandler(timedOutHandler, deadProcess, original);
        assertThat(original.getSuppressed()).hasSize(1);
        assertThat(original.getSuppressed()[0]).hasCauseInstanceOf(IllegalStateException.class);
    }

    private static class StubProcessHandler extends com.intellij.execution.process.ProcessHandler {
        @Override
        public void startNotify() {
            super.startNotify();
            notifyProcessTerminated(0);
        }

        @Override
        protected void destroyProcessImpl() {
        }

        @Override
        protected void detachProcessImpl() {
        }

        @Override
        public boolean detachIsDefault() {
            return false;
        }

        @Override
        public java.io.OutputStream getProcessInput() {
            return null;
        }
    }

    @Test
    void markerRegistrationFailureTerminatesAcquiredRootAndDescendantAndPreservesUncertainWorkspace()
            throws Exception {
        Path solution = host.resolve("attempt/src/main/java/com/jinloes/practice/Solution.java");
        Files.createDirectories(solution.getParent());
        Files.writeString(solution, referencePairSum(), StandardCharsets.UTF_8);
        var attempt = new ManagedPracticeWorkspace.Attempt("attempt", "pair-sum", "1", solution);
        VerificationWorkspace workspace = VerificationWorkspace.create(
                attempt, ExerciseCatalog.find("pair-sum"), "fingerprint");
        Path marker = workspace.root().resolve(".algorithm-practice-verification");
        String validMarker = Files.readString(marker);
        Path ready = host.resolve("child-ready");
        Process process = TestChildProcess.start(RegistrationChild.class, ready.toString());
        ProcessHandle child = null;
        try {
            child = awaitChildPid(ready);
            assertThat(process.descendants().map(ProcessHandle::pid).toList()).contains(child.pid());
            Files.writeString(marker, "schema=broken");
            java.io.IOException failure = catchIOException(
                    () -> workspace.markProcess(process.pid(), "fingerprint"));
            assertThat(failure).isNotNull();

            RunSession.RunControl control = new RunSession.RunControl();
            FullCheckLaunchCleanup.cleanupLaunchFailure(workspace, process, control, failure);

            assertThat(process.isAlive()).isFalse();
            assertThat(child.isAlive()).isFalse();
            assertThat(control.active()).isNull();
            assertThat(control.stopReason()).isNull();
            assertThat(failure.getSuppressed()).isEmpty();
            assertThat(workspace.root()).isDirectory();
        } finally {
            if (child != null) {
                child.destroyForcibly();
                child.onExit().get(5, TimeUnit.SECONDS);
            }
            destroyProcessTree(process);
            Files.writeString(marker, validMarker);
            FullCheckLaunchCleanup.cleanupLaunchFailure(workspace, null, new RunSession.RunControl(), new java.io.IOException("launch"));
        }
    }

    static ProcessHandle awaitChildPid(Path ready) throws InterruptedException {
        long deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(10);
        while (System.nanoTime() < deadline) {
            if (Files.isRegularFile(ready)) {
                try {
                    String text = Files.readString(ready, StandardCharsets.UTF_8).trim();
                    if (!text.isEmpty()) {
                        return ProcessHandle.of(Long.parseLong(text)).orElseThrow();
                    }
                } catch (java.io.IOException | NumberFormatException ignored) {
                }
            }
            Thread.sleep(10);
        }
        throw new AssertionError("Timed out waiting for child PID in " + ready);
    }

    static void destroyProcessTree(Process process) throws Exception {
        if (process == null) {
            return;
        }
        var descendants = process.descendants().toList();
        process.destroyForcibly();
        descendants.forEach(ProcessHandle::destroyForcibly);
        process.descendants().forEach(ProcessHandle::destroyForcibly);
        assertThat(process.waitFor(5, TimeUnit.SECONDS)).isTrue();
        for (ProcessHandle descendant : descendants) {
            descendant.onExit().get(5, TimeUnit.SECONDS);
        }
    }

    public static final class RegistrationChild {
        public static void main(String[] args) throws Exception {
            Process child = TestChildProcess.start(TestChildProcess.class, "waiting");
            try {
                Path ready = Path.of(args[0]);
                Path temp = ready.resolveSibling(ready.getFileName() + ".tmp");
                Files.writeString(temp, Long.toString(child.pid()), StandardCharsets.UTF_8);
                try {
                    Files.move(temp, ready,
                            java.nio.file.StandardCopyOption.REPLACE_EXISTING,
                            java.nio.file.StandardCopyOption.ATOMIC_MOVE);
                } catch (java.nio.file.AtomicMoveNotSupportedException ignored) {
                    Files.move(temp, ready, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                }
                child.waitFor();
            } finally {
                child.destroyForcibly();
                child.waitFor(5, TimeUnit.SECONDS);
            }
        }
    }

    @Test
    void failedOrInterruptedTerminationPreservesWorkspaceAndOriginalLaunchError() throws Exception {
        Path solution = host.resolve("attempt/src/main/java/com/jinloes/practice/Solution.java");
        Files.createDirectories(solution.getParent());
        Files.writeString(solution, referencePairSum(), StandardCharsets.UTF_8);
        var attempt = new ManagedPracticeWorkspace.Attempt("attempt", "pair-sum", "1", solution);
        for (boolean interrupted : java.util.List.of(false, true)) {
            VerificationWorkspace workspace = VerificationWorkspace.create(
                    attempt, ExerciseCatalog.find("pair-sum"), "fingerprint");
            RefusingProcess process = new RefusingProcess(interrupted);
            java.io.IOException original = new java.io.IOException("marker registration failed");
            try {
                FullCheckLaunchCleanup.cleanupLaunchFailure(workspace, process, new RunSession.RunControl(), original);
                assertThat(Thread.interrupted()).isEqualTo(interrupted);
                assertThat(process.destroyed).isTrue();
                assertThat(workspace.root()).isDirectory();
                assertThat(original).hasMessage("marker registration failed");
                assertThat(original.getSuppressed()).hasSize(1);
            } finally {
                Thread.interrupted();
                FullCheckLaunchCleanup.cleanupLaunchFailure(workspace);
            }
        }
        FullCheckLaunchCleanup.cleanupLaunchFailure(null, null, new RunSession.RunControl(), new java.io.IOException("launch"));
    }

    @Test
    void waitsForRecordedDescendantsWhoseExitIsAsynchronous() {
        AwaitedHandle child = new AwaitedHandle();
        RefusingProcess process = new RefusingProcess(false) {
            @Override
            public boolean waitFor(long timeout, TimeUnit unit) {
                return true;
            }

            @Override
            public java.util.stream.Stream<ProcessHandle> descendants() {
                return java.util.stream.Stream.of(child);
            }
        };
        java.io.IOException original = new java.io.IOException("marker registration failed");

        FullCheckLaunchCleanup.cleanupLaunchFailure(null, process, new RunSession.RunControl(), original);

        assertThat(process.destroyed).isTrue();
        assertThat(child.exitAwaited).isTrue();
        assertThat(child.isAlive()).isFalse();
        assertThat(original.getSuppressed()).isEmpty();
    }

    private static final class AwaitedHandle implements ProcessHandle {
        private boolean killRequested;
        private boolean exitAwaited;

        @Override
        public long pid() {
            return -1;
        }

        @Override
        public java.util.Optional<ProcessHandle> parent() {
            return java.util.Optional.empty();
        }

        @Override
        public java.util.stream.Stream<ProcessHandle> children() {
            return java.util.stream.Stream.empty();
        }

        @Override
        public java.util.stream.Stream<ProcessHandle> descendants() {
            return children();
        }

        @Override
        public Info info() {
            throw new UnsupportedOperationException("No OS process in this scheduling fixture");
        }

        @Override
        public boolean supportsNormalTermination() {
            return false;
        }

        @Override
        public boolean isAlive() {
            return !exitAwaited;
        }

        @Override
        public boolean destroy() {
            return destroyForcibly();
        }

        @Override
        public boolean destroyForcibly() {
            killRequested = true;
            return true;
        }

        @Override
        public int compareTo(ProcessHandle other) {
            return Long.compare(pid(), other.pid());
        }

        @Override
        public java.util.concurrent.CompletableFuture<ProcessHandle> onExit() {
            assertThat(killRequested).isTrue();
            exitAwaited = true;
            return java.util.concurrent.CompletableFuture.completedFuture(this);
        }
    }

    private static class RefusingProcess extends Process {
        private final boolean interrupted;
        private boolean destroyed;

        private RefusingProcess(boolean interrupted) {
            this.interrupted = interrupted;
        }

        @Override
        public java.io.OutputStream getOutputStream() {
            return java.io.OutputStream.nullOutputStream();
        }

        @Override
        public java.io.InputStream getInputStream() {
            return java.io.InputStream.nullInputStream();
        }

        @Override
        public java.io.InputStream getErrorStream() {
            return java.io.InputStream.nullInputStream();
        }

        @Override
        public int waitFor() {
            return 0;
        }

        @Override
        public boolean waitFor(long timeout, TimeUnit unit) throws InterruptedException {
            if (interrupted) {
                throw new InterruptedException("test interruption");
            }
            return false;
        }

        @Override
        public int exitValue() {
            throw new IllegalThreadStateException("fixture remains alive");
        }

        @Override
        public boolean isAlive() {
            return true;
        }

        @Override
        public void destroy() {
            destroyed = true;
        }

        @Override
        public Process destroyForcibly() {
            destroy();
            return this;
        }

        @Override
        public java.util.stream.Stream<ProcessHandle> descendants() {
            return java.util.stream.Stream.empty();
        }
    }

    @Test
    void launchFailureCleanupMarksAndDeletesCreatedWorkspace() throws Exception {
        Path solution = host.resolve("attempt/src/main/java/com/jinloes/practice/Solution.java");
        Files.createDirectories(solution.getParent());
        Files.writeString(solution, referencePairSum(), StandardCharsets.UTF_8);
        var attempt = new ManagedPracticeWorkspace.Attempt("attempt", "pair-sum", "1", solution);

        VerificationWorkspace workspace = VerificationWorkspace.create(
                attempt, ExerciseCatalog.find("pair-sum"), "fingerprint");
        Path root = workspace.root();

        FullCheckLaunchCleanup.cleanupLaunchFailure(workspace);

        assertThat(root).doesNotExist();
    }

    private static String referencePairSum() throws Exception {
        try (var input = FullCheckWorkspaceCleanupPathsTest.class
                .getResourceAsStream("/reference/pair-sum/Solution.java")) {
            assertThat(input).isNotNull();
            return new String(input.readAllBytes(), StandardCharsets.UTF_8);
        }
    }
}
