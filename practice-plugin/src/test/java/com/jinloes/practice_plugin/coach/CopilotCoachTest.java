package com.jinloes.practice_plugin.coach;

import com.jinloes.plugin_common.copilot.CopilotModels;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CopilotCoachTest {
    @TempDir Path temporary;

    @Test
    void buildsToolFreeArgumentsWithOptionalModel() {
        assertThat(CopilotCoach.arguments("copilot", null)).containsExactly(
                "copilot", "-s", "--no-custom-instructions", "--disable-builtin-mcps",
                "--available-tools=" + CopilotCoach.NO_TOOLS, "--no-ask-user");
        assertThat(CopilotCoach.arguments("copilot", "  ")).containsExactly(
                "copilot", "-s", "--no-custom-instructions", "--disable-builtin-mcps",
                "--available-tools=" + CopilotCoach.NO_TOOLS, "--no-ask-user");
        assertThat(CopilotCoach.arguments("copilot", " gpt-5 ")).containsExactly(
                "copilot", "-s", "--no-custom-instructions", "--disable-builtin-mcps",
                "--available-tools=" + CopilotCoach.NO_TOOLS, "--no-ask-user", "--model=gpt-5");
    }

    @Test
    void runsSandboxedPromptOverStdinAndCleansUpRunDirectory() throws Exception {
        Path base = temporary.resolve("runs");
        Path record = temporary.resolve("record");
        Path script = writeScript(MapOf(
                "record", record.toString(),
                "stdout", "  ## Diagnosis\nCheck base case.\n  "));
        CopilotCoach coach = fakeCoach(base);

        String reply = coach.run("Prompt body\nwith line 2",
                new CopilotCoach.Settings(script.toString(), "gpt-5", 30), () -> false);

        assertThat(reply).isEqualTo("## Diagnosis\nCheck base case.");
        assertThat(CopilotCoach.isRunning()).isFalse();
        assertThat(Files.readString(record.resolve("stdin.txt"))).isEqualTo("Prompt body\nwith line 2");
        assertThat(Files.readString(record.resolve("argv.txt")))
                .contains("--available-tools=" + CopilotCoach.NO_TOOLS)
                .contains("--no-custom-instructions")
                .contains("--disable-builtin-mcps")
                .contains("--no-ask-user")
                .contains("--model=gpt-5");
        List<String> cwdLines = Files.readAllLines(record.resolve("cwd.txt"));
        Path runDir = Path.of(cwdLines.get(0));
        assertThat(cwdLines.subList(1, cwdLines.size())).containsExactly(CopilotCoach.MARKER);
        assertThat(Files.exists(runDir)).isFalse();
    }

    @Test
    void rejectsConcurrentRunWhileAnotherRequestIsActive() throws Exception {
        Path base = temporary.resolve("runs");
        Path record = temporary.resolve("record");
        Path script = writeScript(MapOf(
                "record", record.toString(),
                "hangBeforeStdin", "true",
                "sleepMillis", "600",
                "stdout", "done"));
        CopilotCoach coach = fakeCoach(base);
        CountDownLatch started = new CountDownLatch(1);
        AtomicBoolean cancel = new AtomicBoolean();

        CompletableFuture<String> first = CompletableFuture.supplyAsync(() -> {
            try {
                started.countDown();
                return coach.run("prompt", new CopilotCoach.Settings(script.toString(), "", 10), cancel::get);
            } catch (CopilotCoach.CoachException exception) {
                throw new CompletionException(exception);
            }
        });
        assertThat(started.await(5, TimeUnit.SECONDS)).isTrue();
        long waitDeadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(5);
        while (!CopilotCoach.isRunning() && System.nanoTime() < waitDeadline) {
            Thread.sleep(10);
        }
        assertThat(CopilotCoach.isRunning()).isTrue();

        assertThatThrownBy(() -> coach.run("second",
                new CopilotCoach.Settings(script.toString(), "", 10), () -> false))
                .isInstanceOf(CopilotCoach.CoachException.class)
                .hasMessageContaining("already running");

        cancel.set(true);
        assertThatThrownBy(first::join).hasCauseInstanceOf(CopilotCoach.CoachException.class);
        assertThat(CopilotCoach.isRunning()).isFalse();
    }

    @Test
    void reportsEmptyResponseExitCodeLongStderrTimeoutCancelAndOverflow() throws Exception {
        Path base = temporary.resolve("runs");
        CopilotCoach coach = fakeCoach(base);

        Path emptyScript = writeScript(MapOf("record", temporary.resolve("r-empty").toString(), "stdout", "   \n"));
        assertThatThrownBy(() -> coach.run("p", new CopilotCoach.Settings(emptyScript.toString(), "", 10), () -> false))
                .isInstanceOf(CopilotCoach.CoachException.class)
                .hasMessageContaining("empty response");

        Path exitImmediate = writeScript(MapOf("exitImmediately", "true"));
        assertThatThrownBy(() -> coach.run("p", new CopilotCoach.Settings(exitImmediate.toString(), "", 10), () -> false))
                .isInstanceOf(CopilotCoach.CoachException.class)
                .hasMessageContaining("exited with code 3");

        Path longErr = writeScript(MapOf(
                "record", temporary.resolve("r-err").toString(),
                "exit", "2",
                "stderr", "E".repeat(2_500)));
        assertThatThrownBy(() -> coach.run("p", new CopilotCoach.Settings(longErr.toString(), "", 10), () -> false))
                .isInstanceOf(CopilotCoach.CoachException.class)
                .hasMessageContaining("exited with code 2")
                .hasMessageContaining("...");

        Path slow = writeScript(MapOf(
                "record", temporary.resolve("r-slow").toString(),
                "sleepMillis", "3000",
                "stdout", "late"));
        assertThatThrownBy(() -> coach.run("p", new CopilotCoach.Settings(slow.toString(), "", 1), () -> false))
                .isInstanceOf(CopilotCoach.CoachException.class)
                .hasMessageContaining("did not answer within 1 seconds");

        AtomicBoolean cancelFlag = new AtomicBoolean(true);
        assertThatThrownBy(() -> coach.run("p", new CopilotCoach.Settings(slow.toString(), "", 10), cancelFlag::get))
                .isInstanceOf(CopilotCoach.CoachException.class)
                .hasMessageContaining("cancelled");

        Path overflow = writeScript(MapOf(
                "record", temporary.resolve("r-over").toString(),
                "stdout", "A".repeat(4096),
                "stdoutRepeat", "150",
                "sleepAfterStdoutMillis", "1500"));
        assertThatThrownBy(() -> coach.run("p", new CopilotCoach.Settings(overflow.toString(), "", 10), () -> false))
                .isInstanceOf(CopilotCoach.CoachException.class)
                .hasMessageContaining("512 KiB");

        Path fastOverflow = writeScript(MapOf(
                "record", temporary.resolve("r-fast-over").toString(),
                "stdout", "B".repeat(4096),
                "stdoutRepeat", "150"));
        CopilotCoach slowPollCoach = new CopilotCoach(base, javaPrefix(), "missing-binary-for-test", 5_000);
        assertThatThrownBy(() -> slowPollCoach.run("p",
                new CopilotCoach.Settings(fastOverflow.toString(), "", 10), () -> false))
                .isInstanceOf(CopilotCoach.CoachException.class)
                .hasMessageContaining("512 KiB");
    }

    @Test
    void validatesConfiguredAndPathExecutablesAndRunDirectoryFailures() throws Exception {
        Path base = temporary.resolve("runs");
        CopilotCoach coach = fakeCoach(base);

        assertThatThrownBy(() -> coach.resolveExecutable("bad\0path"))
                .isInstanceOf(CopilotCoach.CoachException.class)
                .hasMessageContaining("not a valid path");

        Path missing = temporary.resolve("does-not-exist");
        assertThatThrownBy(() -> coach.resolveExecutable(missing.toString()))
                .isInstanceOf(CopilotCoach.CoachException.class)
                .hasMessageContaining("was not found at");

        assertThatThrownBy(() -> coach.resolveExecutable(""))
                .isInstanceOf(CopilotCoach.CoachException.class)
                .hasMessageContaining("was not found on PATH");
        assertThatThrownBy(() -> coach.resolveExecutable(null))
                .isInstanceOf(CopilotCoach.CoachException.class)
                .hasMessageContaining("was not found on PATH");

        CopilotCoach foundOnPath = new CopilotCoach(base, List.of(), "java");
        assertThat(foundOnPath.resolveExecutable("")).isNotBlank();

        Path fileAsBase = temporary.resolve("not-a-dir");
        Files.writeString(fileAsBase, "occupied");
        CopilotCoach brokenBase = new CopilotCoach(fileAsBase, javaPrefix(), "missing");
        Path validScript = writeScript(MapOf("record", temporary.resolve("r-broken").toString()));
        assertThatThrownBy(() -> brokenBase.run("p",
                new CopilotCoach.Settings(validScript.toString(), "", 10), () -> false))
                .isInstanceOf(CopilotCoach.CoachException.class)
                .hasMessageContaining("Could not prepare the coach directory");

        CopilotCoach badLauncher = new CopilotCoach(base, List.of("/definitely/missing/launcher"), "missing");
        assertThatThrownBy(() -> badLauncher.run("p",
                new CopilotCoach.Settings(validScript.toString(), "", 10), () -> false))
                .isInstanceOf(CopilotCoach.CoachException.class)
                .hasMessageContaining("Could not start Copilot CLI");

        // Pre-canceled request never launches the CLI or transmits the prompt.
        Path neverLaunchedRecord = temporary.resolve("r-precanceled");
        Path fastScript = writeScript(MapOf("record", neverLaunchedRecord.toString(), "stdout", "reply"));
        assertThatThrownBy(() -> coach.run("secret prompt",
                new CopilotCoach.Settings(fastScript.toString(), "", 10), () -> true))
                .isInstanceOf(CopilotCoach.CoachException.class)
                .hasMessageContaining("cancelled");
        assertThat(neverLaunchedRecord).doesNotExist();

        // Request canceled while a fast-exiting CLI completes still reports cancellation.
        var checks = new java.util.concurrent.atomic.AtomicInteger();
        CopilotCoach slowPollCoach = new CopilotCoach(base, javaPrefix(), "missing", 5_000L);
        assertThatThrownBy(() -> slowPollCoach.run("prompt",
                new CopilotCoach.Settings(fastScript.toString(), "", 10),
                () -> checks.incrementAndGet() > 1))
                .isInstanceOf(CopilotCoach.CoachException.class)
                .hasMessageContaining("cancelled");
    }

    @Test
    void cleansUpAbandonedRunDirectoriesAndIgnoresUnownedOrAliveOnes() throws Exception {
        Path base = temporary.resolve("abandoned-runs");
        CopilotCoach coach = fakeCoach(base);

        Path deadDir = base.resolve("run-dead");
        Files.createDirectories(deadDir);
        Files.writeString(deadDir.resolve(CopilotCoach.MARKER), "pid=99999999\n");

        Path malformedDir = base.resolve("run-malformed");
        Files.createDirectories(malformedDir);
        Files.writeString(malformedDir.resolve(CopilotCoach.MARKER), "pid=not-a-number\n");

        Path aliveDir = base.resolve("run-alive");
        Files.createDirectories(aliveDir);
        Files.writeString(aliveDir.resolve(CopilotCoach.MARKER), "pid=" + ProcessHandle.current().pid() + "\n");

        coach.cleanupAbandoned();

        assertThat(Files.exists(deadDir)).isFalse();
        assertThat(Files.exists(malformedDir)).isTrue();
        assertThat(Files.exists(aliveDir)).isTrue();

        // deleteRunDirectory swallows IOException when target is outside the owned base.
        coach.deleteRunDirectory(temporary);
    }

    @Test
    void listsModelsViaSdkAndHandlesErrorsCancellationTimeoutAndInterruption() throws Exception {
        Path base = temporary.resolve("model-runs");
        CopilotCoach coach = fakeCoach(base);

        Path okRecord = temporary.resolve("srv-ok");
        Path okScript = writeScript(MapOf(
                "record", okRecord.toString(),
                "modelsResult", "{\"models\":[{\"id\":\"gpt-5\",\"name\":\"GPT-5\"}]}"));
        List<CopilotModels.Model> models = coach.listModels(okScript.toString(), () -> false);
        assertThat(models).containsExactly(new CopilotModels.Model("gpt-5", "GPT-5"));
        assertThat(Files.readString(okRecord.resolve("argv.txt")))
                .contains("--no-custom-instructions")
                .contains("--disable-builtin-mcps");
        assertThat(Files.readString(okRecord.resolve("methods.txt")))
                .contains("connect")
                .contains("models.list");

        Path errScript = writeScript(MapOf(
                "record", temporary.resolve("srv-err").toString(),
                "modelsError", "unauthorized"));
        assertThatThrownBy(() -> coach.listModels(errScript.toString(), () -> false))
                .isInstanceOf(CopilotCoach.CoachException.class)
                .hasMessageContaining(CopilotCoach.LIST_PREFIX);

        Path hangScript = writeScript(MapOf(
                "record", temporary.resolve("srv-hang").toString(),
                "mode", "hang"));
        assertThatThrownBy(() -> coach.listModels(hangScript.toString(), () -> true, Duration.ofSeconds(5)))
                .isInstanceOf(CopilotCoach.CoachException.class)
                .hasMessageContaining("cancelled");

        assertThatThrownBy(() -> coach.listModels(hangScript.toString(), () -> false, Duration.ofMillis(200)))
                .isInstanceOf(CopilotCoach.CoachException.class)
                .hasMessageContaining("did not answer within");

        Thread.currentThread().interrupt();
        try {
            assertThatThrownBy(() -> coach.listModels(hangScript.toString(), () -> false, Duration.ofSeconds(5)))
                    .isInstanceOf(CopilotCoach.CoachException.class)
                    .hasMessageContaining("interrupted");
            assertThat(Thread.currentThread().isInterrupted()).isTrue();
        } finally {
            Thread.interrupted();
        }

        // Direct-launcher path (empty commandPrefix) fails cleanly when script is not executable.
        CopilotCoach directCoach = new CopilotCoach(base, List.of(), "missing");
        assertThatThrownBy(() -> directCoach.listModels(okScript.toString(), () -> false, Duration.ofSeconds(2)))
                .isInstanceOf(CopilotCoach.CoachException.class)
                .hasMessageContaining(CopilotCoach.LIST_PREFIX);
    }

    @Test
    void formatsListFailuresAndSwallowsStopExceptions() {
        assertThat(CopilotCoach.listFailure(null).getMessage())
                .isEqualTo(CopilotCoach.LIST_PREFIX + "unknown error");
        assertThat(CopilotCoach.listFailure(new IllegalStateException("  ")).getMessage())
                .isEqualTo(CopilotCoach.LIST_PREFIX + "IllegalStateException");
        assertThat(CopilotCoach.listFailure(new CompletionException(
                new ExecutionException(new IOException("  broken\npipe  ")))).getMessage())
                .isEqualTo(CopilotCoach.LIST_PREFIX + "broken pipe");
        assertThat(CopilotCoach.listFailure(new CompletionException(null)).getMessage())
                .isEqualTo(CopilotCoach.LIST_PREFIX + "CompletionException");
        String longMessage = CopilotCoach.listFailure(new RuntimeException("X".repeat(500))).getMessage();
        assertThat(longMessage).hasSize(CopilotCoach.MAX_LIST_ERROR_CHARS).endsWith("...");

        CopilotCoach.stopQuietly(() -> {});
        CopilotCoach.stopQuietly(() -> {
            throw new IOException("ignored");
        });
        CopilotCoach.writePrompt(new java.io.OutputStream() {
            @Override
            public void write(int b) throws IOException {
                throw new IOException("broken pipe");
            }
        }, "hello".getBytes(StandardCharsets.UTF_8));
    }

    private CopilotCoach fakeCoach(Path base) {
        return new CopilotCoach(base, javaPrefix(), "missing-binary-for-test", 25);
    }

    private static List<String> javaPrefix() {
        Path java = Path.of(System.getProperty("java.home"), "bin", "java");
        return List.of(java.toString(), "-cp", System.getProperty("java.class.path"),
                FakeCopilotCli.class.getName());
    }

    private Path writeScript(Properties props) throws IOException {
        Path file = Files.createTempFile(temporary, "coach-script-", ".properties");
        try (Writer writer = Files.newBufferedWriter(file, StandardCharsets.UTF_8)) {
            props.store(writer, null);
        }
        return file;
    }

    private static Properties MapOf(String... pairs) {
        Properties props = new Properties();
        for (int i = 0; i < pairs.length; i += 2) {
            props.setProperty(pairs[i], pairs[i + 1]);
        }
        return props;
    }
}
