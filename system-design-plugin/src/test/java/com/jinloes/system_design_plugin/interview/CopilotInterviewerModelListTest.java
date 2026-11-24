package com.jinloes.system_design_plugin.interview;

import com.intellij.openapi.util.SystemInfo;
import com.intellij.util.EnvironmentUtil;
import com.jinloes.plugin_common.copilot.CopilotModels;
import com.jinloes.system_design_plugin.interview.CopilotInterviewer.InterviewException;
import org.junit.jupiter.api.BeforeEach;
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
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CopilotInterviewerModelListTest {
    private static final String MISSING_NAME = "no-such-copilot-cli-for-tests";

    @TempDir Path temporary;
    private Path base;
    private Path record;
    private List<String> serverPrefix;

    @BeforeEach
    void setUp() throws IOException {
        base = temporary.resolve("interviewer");
        record = temporary.resolve("record");
        Path argFile = temporary.resolve("fake.args");
        String classpath = System.getProperty("design.test.classpath");
        Files.writeString(argFile, "-cp \"" + classpath.replace("\\", "\\\\") + "\"\n");
        serverPrefix = List.of(Path.of(System.getProperty("java.home"), "bin", "java").toString(),
                "@" + argFile, FakeCopilotServer.class.getName());
    }

    private CopilotInterviewer server() {
        return new CopilotInterviewer(base, serverPrefix, MISSING_NAME);
    }

    private void assertServerGone() throws IOException {
        long pid = Long.parseLong(Files.readString(record.resolve("pid.txt")).strip());
        assertThat(ProcessHandle.of(pid).map(ProcessHandle::isAlive).orElse(false))
                .as("the CLI process is stopped").isFalse();
        try (var remaining = Files.list(base)) {
            assertThat(remaining).as("the run directory is deleted afterwards").isEmpty();
        }
    }

    private Path script(String... pairs) throws IOException {
        Properties properties = new Properties();
        properties.setProperty("record", record.toString());
        for (int index = 0; index < pairs.length; index += 2) {
            properties.setProperty(pairs[index], pairs[index + 1]);
        }
        Path script = temporary.resolve("copilot-" + System.nanoTime() + ".properties");
        try (Writer writer = Files.newBufferedWriter(script, StandardCharsets.UTF_8)) {
            properties.store(writer, null);
        }
        return script;
    }

    @Test
    void listsModelsThroughTheSdkWithoutAnySession() throws Exception {
        String envName = SystemInfo.isWindows ? "SystemRoot" : "HOME";
        Path script = script("envName", envName, "modelsResult", "{\"models\":["
                + "{\"id\":\"gpt-x\",\"name\":\"GPT X\",\"policy\":{\"state\":\"enabled\"}},"
                + "{\"id\":\"off\",\"name\":\"Off\",\"policy\":{\"state\":\"disabled\"}},"
                + "{\"id\":\"plain\",\"name\":\"\"}]}");
        long started = System.nanoTime();

        List<CopilotModels.Model> models = server().listModels(script.toString(), () -> false);

        assertThat(System.nanoTime() - started).as("success-path latency").isLessThan(5_000_000_000L);
        assertThat(models).containsExactly(new CopilotModels.Model("gpt-x", "GPT X"),
                new CopilotModels.Model("plain", "plain"));
        assertThat(Files.readAllLines(record.resolve("argv.txt"))).contains("--no-custom-instructions",
                "--disable-builtin-mcps", "--server", "--stdio").noneMatch(argument -> argument.contains("allow"));
        List<String> methods = Files.readAllLines(record.resolve("methods.txt"));
        assertThat(methods).contains("connect").containsOnlyOnce("models.list")
                .noneMatch(method -> method.startsWith("session"));
        assertThat(Files.readString(record.resolve("env.txt")))
                .isEqualTo(EnvironmentUtil.getEnvironmentMap().get(envName)).isNotEqualTo("null");
        assertServerGone();
    }

    @Test
    void reportsModelListErrors() throws IOException {
        Path rpcError = script("modelsError", "Not authenticated");
        assertThatThrownBy(() -> server().listModels(rpcError.toString(), () -> false))
                .isInstanceOf(InterviewException.class)
                .hasMessageStartingWith("Could not list models: ").hasMessageContaining("Not authenticated");
        assertServerGone();

        Path oldProtocol = script("protocolVersion", "1");
        assertThatThrownBy(() -> server().listModels(oldProtocol.toString(), () -> false))
                .hasMessageStartingWith("Could not list models: ").hasMessageContaining("protocol");
        assertServerGone();

        Path early = script("mode", "exitEarly");
        assertThatThrownBy(() -> server().listModels(early.toString(), () -> false))
                .hasMessageStartingWith("Could not list models: ");
        assertServerGone();
    }

    @Test
    void modelListStopsAtTheTimeoutAndOnCancel() throws IOException {
        Path hang = script("mode", "hang");
        long started = System.nanoTime();
        assertThatThrownBy(() -> server().listModels(hang.toString(), () -> false, Duration.ofSeconds(1)))
                .isInstanceOf(InterviewException.class)
                .hasMessage("Could not list models: Copilot CLI did not answer within 1 seconds.");
        assertThat(System.nanoTime() - started).isLessThan(15_000_000_000L);
        assertServerGone();

        long cancelStart = System.nanoTime();
        assertThatThrownBy(() -> server().listModels(hang.toString(),
                () -> Files.exists(record.resolve("methods.txt"))
                        && System.nanoTime() - cancelStart > 300_000_000L))
                .hasMessage("Could not list models: cancelled.");
        assertThat(System.nanoTime() - cancelStart).isLessThan(15_000_000_000L);
        assertServerGone();
    }

    @Test
    void anInterruptStopsTheCliBeforeRestoringTheFlag() throws Exception {
        Path hang = script("mode", "hang");
        AtomicReference<Throwable> failure = new AtomicReference<>();
        AtomicBoolean interruptedAfter = new AtomicBoolean();
        Thread caller = new Thread(() -> {
            try {
                server().listModels(hang.toString(), () -> false);
            } catch (Throwable throwable) {
                failure.set(throwable);
            }
            interruptedAfter.set(Thread.currentThread().isInterrupted());
        });
        caller.start();
        long deadline = System.nanoTime() + 10_000_000_000L;
        while (!Files.exists(record.resolve("methods.txt")) && System.nanoTime() < deadline) {
            Thread.sleep(10);
        }
        caller.interrupt();
        caller.join(20_000);
        assertThat(failure.get()).isInstanceOf(InterviewException.class)
                .hasMessage("Could not list models: interrupted.");
        assertThat(interruptedAfter).isTrue();
        assertServerGone();
    }

    @Test
    void modelListReportsMissingAndInvalidExecutables() {
        Path missing = temporary.resolve("nope").resolve("copilot.exe");
        assertThatThrownBy(() -> server().listModels(missing.toString(), () -> false))
                .hasMessageContaining("not found at " + missing);
        String invalid = SystemInfo.isWindows ? "\"C:\\x\"" : "bad\0path";
        assertThatThrownBy(() -> server().listModels(invalid, () -> false))
                .isInstanceOf(InterviewException.class).hasMessageContaining("is not a valid path");
        assertThatThrownBy(() -> server().listModels("bad\0path", () -> false))
                .hasMessageContaining("is not a valid path");
        assertThat(base).doesNotExist();
    }

    @Test
    void listFailureMessagesAreUnwrappedAndBounded() throws IOException {
        assertThat(CopilotInterviewer.listFailure(new CompletionException(new ExecutionException(new IOException("boom")))))
                .hasMessage("Could not list models: boom");
        assertThat(CopilotInterviewer.listFailure(new CompletionException("bare", null)))
                .hasMessage("Could not list models: bare");
        assertThat(CopilotInterviewer.listFailure(new ExecutionException("bare", null) { }))
                .hasMessage("Could not list models: bare");
        assertThat(CopilotInterviewer.listFailure(new IllegalStateException())).hasMessage(
                "Could not list models: IllegalStateException");
        assertThat(CopilotInterviewer.listFailure(new IOException("   "))).hasMessage(
                "Could not list models: IOException");
        assertThat(CopilotInterviewer.listFailure(null)).hasMessage("Could not list models: unknown error");
        assertThat(CopilotInterviewer.listFailure(new IOException("a\n  b " + "x".repeat(400))).getMessage())
                .startsWith("Could not list models: a b x").hasSize(300).endsWith("...");

        CopilotInterviewer.stopQuietly(() -> {
            throw new IOException("stop failed");
        });
        Path dummyCli = temporary.resolve(SystemInfo.isWindows ? "dummy-cli.cmd" : "dummy-cli.sh");
        Files.writeString(dummyCli, SystemInfo.isWindows ? "@exit /b 4\r\n" : "#!/bin/sh\nexit 4\n");
        dummyCli.toFile().setExecutable(true);
        assertThatThrownBy(() -> new CopilotInterviewer().listModels(dummyCli.toString(), () -> false))
                .isInstanceOf(InterviewException.class)
                .hasMessageStartingWith("Could not list models: ");
    }
}
