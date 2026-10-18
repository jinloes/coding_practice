package com.jinloes.practice_plugin.run;

import com.intellij.execution.process.ProcessHandler;
import com.intellij.testFramework.EdtTestUtil;
import com.intellij.testFramework.fixtures.IdeaProjectTestFixture;
import com.intellij.testFramework.fixtures.IdeaTestFixtureFactory;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;

class PracticeRunnerLifecyclePathsTest {
    private IdeaProjectTestFixture fixture;
    private Path host;

    @BeforeEach
    void setUpProject() throws Exception {
        host = Path.of("build", "agent-fixtures", "practice-runner-lifecycle-" + System.nanoTime())
                .toAbsolutePath().normalize();
        Files.createDirectories(host);
        EdtTestUtil.runInEdtAndWait(() -> {
            fixture = IdeaTestFixtureFactory.getFixtureFactory()
                    .createFixtureBuilder("practice-runner-lifecycle", host, false).getFixture();
            fixture.setUp();
        });
    }

    @AfterEach
    void tearDownProject() throws Exception {
        if (fixture != null) {
            fixture.getProject().getService(PracticeRunner.class).stop();
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
    void exampleProcessStartedCancelsAlreadyStoppedSessions() {
        PracticeRunner runner = fixture.getProject().getService(PracticeRunner.class);
        RunSession.RunControl control = new RunSession.RunControl();
        control.requestCancel();
        InMemoryProcessHandler handler = new InMemoryProcessHandler();

        runner.handleExampleProcessStarted(
                new RunSession.ExampleSession(null, "", false, "fp", null, null, control), handler);

        assertThat(control.active()).isSameAs(handler);
    }

    @Test
    void exampleProcessStartedAndNotStartedReportLifecycleMessages() throws Exception {
        PracticeRunner runner = fixture.getProject().getService(PracticeRunner.class);
        CountDownLatch running = new CountDownLatch(1);
        CountDownLatch notStarted = new CountDownLatch(1);
        AtomicReference<String> last = new AtomicReference<>("");
        runner.addListener(text -> {
            last.set(text);
            if (text.equals("Running visible examples.")) {
                running.countDown();
            }
            if (text.startsWith("Example launch did not complete")) {
                notStarted.countDown();
            }
        });

        runner.handleExampleProcessStarted(new RunSession.ExampleSession(
                null, "", false, "fp", null, null, new RunSession.RunControl()), new InMemoryProcessHandler());
        runner.handleExampleProcessNotStarted(new RunSession.ExampleSession(
                null, "", false, "fp", null, null, new RunSession.RunControl()));

        assertThat(running.await(30, TimeUnit.SECONDS)).as(last.get()).isTrue();
        assertThat(notStarted.await(30, TimeUnit.SECONDS)).as(last.get()).isTrue();
    }

    @Test
    void disposeCancelsActiveWatchdog() {
        PracticeRunner runner = fixture.getProject().getService(PracticeRunner.class);
        RunSession.RunControl control = new RunSession.RunControl();
        RecordingFuture future = new RecordingFuture();
        control.watchdog(future);
        runner.replaceSessionForTest(new RunSession.CheckSession("attempt", null, control));

        runner.dispose();

        assertThat(future.cancelled).isTrue();
    }

    @Test
    void asynchronousSaveContinuationReportsStartFailures() throws Exception {
        PracticeRunner runner = fixture.getProject().getService(PracticeRunner.class);
        CountDownLatch message = new CountDownLatch(1);
        AtomicReference<String> last = new AtomicReference<>("");
        runner.addListener(text -> {
            last.set(text);
            if (text.startsWith("Cannot start practice execution:")) {
                message.countDown();
            }
        });

        runner.runExamples("missing-attempt");

        assertThat(message.await(30, TimeUnit.SECONDS)).as(last.get()).isTrue();
        assertThat(last.get()).contains("missing-attempt");
    }

    @Test
    void injectedContinuationReportsStartFailures() throws Exception {
        PracticeRunner runner = new PracticeRunner(fixture.getProject(), Runnable::run);
        try {
            CountDownLatch message = new CountDownLatch(1);
            AtomicReference<String> last = new AtomicReference<>("");
            runner.addListener(text -> {
                last.set(text);
                if (text.startsWith("Cannot start practice execution:")) {
                    message.countDown();
                }
            });

            runner.runExamples("missing-attempt");

            assertThat(message.await(30, TimeUnit.SECONDS)).as(last.get()).isTrue();
            assertThat(last.get()).contains("missing-attempt");
        } finally {
            com.intellij.openapi.util.Disposer.dispose(runner);
        }
    }

    private static final class InMemoryProcessHandler extends ProcessHandler {
        private boolean destroyed;

        @Override
        protected void destroyProcessImpl() {
            destroyed = true;
            notifyProcessTerminated(0);
        }

        @Override
        protected void detachProcessImpl() {
            notifyProcessDetached();
        }

        @Override
        public boolean detachIsDefault() {
            return false;
        }

        @Override
        public OutputStream getProcessInput() {
            return null;
        }
    }

    private static final class RecordingFuture implements ScheduledFuture<Object> {
        private boolean cancelled;

        @Override
        public long getDelay(@NotNull TimeUnit unit) {
            return 0;
        }

        @Override
        public int compareTo(@NotNull java.util.concurrent.Delayed other) {
            return 0;
        }

        @Override
        public boolean cancel(boolean mayInterruptIfRunning) {
            cancelled = true;
            return true;
        }

        @Override
        public boolean isCancelled() {
            return cancelled;
        }

        @Override
        public boolean isDone() {
            return cancelled;
        }

        @Override
        public Object get() {
            return null;
        }

        @Override
        public Object get(long timeout, @NotNull TimeUnit unit) {
            return null;
        }
    }
}
