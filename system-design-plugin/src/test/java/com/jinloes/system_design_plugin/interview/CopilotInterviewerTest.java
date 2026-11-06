package com.jinloes.system_design_plugin.interview;

import com.intellij.openapi.util.SystemInfo;
import com.jinloes.system_design_plugin.interview.CopilotInterviewer.InterviewException;
import com.jinloes.system_design_plugin.interview.CopilotInterviewer.Settings;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CopilotInterviewerTest {
    private static final String MISSING_NAME = "no-such-copilot-cli-for-tests";

    @TempDir Path temporary;
    private Path base;
    private Path record;
    private List<String> prefix;

    @BeforeEach
    void setUp() throws IOException {
        base = temporary.resolve("interviewer");
        record = temporary.resolve("record");
        // An argument file keeps the long test classpath off the command line (Windows limits its length).
        Path argFile = temporary.resolve("fake.args");
        String classpath = System.getProperty("design.test.classpath");
        Files.writeString(argFile, "-cp \"" + classpath.replace("\\", "\\\\") + "\"\n");
        prefix = List.of(Path.of(System.getProperty("java.home"), "bin", "java").toString(), "@" + argFile,
                FakeCopilotCli.class.getName());
    }

    private CopilotInterviewer interviewer() {
        return new CopilotInterviewer(base, prefix, () -> SystemInfo.isWindows, MISSING_NAME);
    }

    private CopilotInterviewer server() {
        List<String> serverPrefix = List.of(prefix.get(0), prefix.get(1), FakeCopilotServer.class.getName());
        return new CopilotInterviewer(base, serverPrefix, () -> SystemInfo.isWindows, MISSING_NAME);
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

    private static Settings settings(Path script, int timeoutSeconds) {
        return new Settings(script.toString(), "", timeoutSeconds);
    }

    @Test
    void argumentsAreExactAndNeverEnableTools() {
        assertThat(CopilotInterviewer.arguments("copilot", null)).containsExactly("copilot", "-s",
                "--no-custom-instructions", "--disable-builtin-mcps", "--available-tools=practice_no_tools",
                "--no-ask-user");
        assertThat(CopilotInterviewer.arguments("copilot", "  ")).hasSize(6);
        List<String> withModel = CopilotInterviewer.arguments("copilot", " gpt-x ");
        assertThat(withModel).endsWith("--model=gpt-x").hasSize(7);
        assertThat(withModel).noneMatch(argument -> argument.contains("allow") || argument.contains("yolo"));
    }

    @Test
    void sendsThePromptOnStdinFromAnOtherwiseEmptyOwnedDirectory() throws Exception {
        String json = InterviewFeedbackTest.valid().toString();
        Path script = script("stdout", "Here:\n```json\n" + json + "\n```\n");
        String prompt = "Design notes ✓ — " + "x".repeat(70_000);

        InterviewFeedback feedback = interviewer().run(prompt, new Settings(script.toString(), "my-model", 60),
                () -> false);

        assertThat(feedback.parsed()).isTrue();
        assertThat(feedback.verdict()).isEqualTo("Hire");
        assertThat(Files.readAllLines(record.resolve("argv.txt")))
                .containsExactlyElementsOf(CopilotInterviewer.arguments(script.toString(), "my-model"));
        assertThat(Files.readString(record.resolve("stdin.txt"), StandardCharsets.UTF_8)).isEqualTo(prompt);
        List<String> cwd = Files.readAllLines(record.resolve("cwd.txt"));
        assertThat(Path.of(cwd.get(0)).getParent().toRealPath()).isEqualTo(base.toRealPath());
        assertThat(cwd.subList(1, cwd.size())).containsExactly(CopilotInterviewer.MARKER);
        try (var remaining = Files.list(base)) {
            assertThat(remaining).as("the run directory is deleted afterwards").isEmpty();
        }
        assertThat(CopilotInterviewer.isRunning()).isFalse();
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
                .isEqualTo(com.intellij.util.EnvironmentUtil.getEnvironmentMap().get(envName)).isNotEqualTo("null");
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
        assertThatThrownBy(() -> server().listModels(hang.toString(), () -> false, java.time.Duration.ofSeconds(1)))
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
    void listFailureMessagesAreUnwrappedAndBounded() {
        assertThat(CopilotInterviewer.listFailure(new java.util.concurrent.CompletionException(
                new java.util.concurrent.ExecutionException(new IOException("boom")))))
                .hasMessage("Could not list models: boom");
        assertThat(CopilotInterviewer.listFailure(new IllegalStateException())).hasMessage(
                "Could not list models: IllegalStateException");
        assertThat(CopilotInterviewer.listFailure(null)).hasMessage("Could not list models: unknown error");
        assertThat(CopilotInterviewer.listFailure(new IOException("a\n  b " + "x".repeat(400))).getMessage())
                .startsWith("Could not list models: a b x").hasSize(300).endsWith("...");
    }

    @Test
    void unparseableOutputKeepsTheRawText() throws Exception {
        InterviewFeedback feedback = interviewer().run("p", settings(script("stdout", "I think it is fine."), 60),
                () -> false);
        assertThat(feedback.parsed()).isFalse();
        assertThat(feedback.raw()).isEqualTo("I think it is fine.");
    }

    @Test
    void nonZeroExitReportsAnActionableMessageWithStderr() throws IOException {
        Path script = script("exit", "1", "stderr", "Error: not authenticated", "stdout", "partial");
        assertThatThrownBy(() -> interviewer().run("p", settings(script, 60), () -> false))
                .isInstanceOf(InterviewException.class)
                .hasMessageContaining("exited with code 1")
                .hasMessageContaining("signed in")
                .hasMessageContaining("Error: not authenticated");

        Path longError = script("exit", "2", "stderr", "e".repeat(5_000));
        assertThatThrownBy(() -> interviewer().run("p", settings(longError, 60), () -> false))
                .hasMessageEndingWith("e".repeat(2_000) + "...");
    }

    @Test
    void anEarlyExitBeforeReadingStdinIsReportedByExitCode() throws IOException {
        Path script = script("exitImmediately", "true");
        assertThatThrownBy(() -> interviewer().run("p".repeat(2_000_000), settings(script, 60), () -> false))
                .hasMessageContaining("exited with code 3");
    }

    @Test
    void stopsAtTheTimeout() throws IOException {
        Path script = script("sleepMillis", "20000");
        long started = System.nanoTime();
        assertThatThrownBy(() -> interviewer().run("p", settings(script, 1), () -> false))
                .isInstanceOf(InterviewException.class)
                .hasMessageContaining("did not answer within 1 seconds");
        assertThat(System.nanoTime() - started).isLessThan(15_000_000_000L);
        assertThat(CopilotInterviewer.isRunning()).isFalse();
    }

    @Test
    void stopsWhenCancelled() throws IOException {
        Path script = script("sleepMillis", "20000");
        long started = System.nanoTime();
        assertThatThrownBy(() -> interviewer().run("p", settings(script, 60),
                () -> System.nanoTime() - started > 300_000_000L))
                .isInstanceOf(InterviewException.class)
                .hasMessage("Feedback request cancelled.");
        assertThat(System.nanoTime() - started).isLessThan(15_000_000_000L);
    }

    @Test
    void stopsOversizedOutput() throws IOException {
        Path script = script("stdout", "x".repeat(1024), "stdoutRepeat", "600", "sleepMillis", "0");
        assertThatThrownBy(() -> interviewer().run("p", settings(script, 60), () -> false))
                .isInstanceOf(InterviewException.class)
                .hasMessageContaining("more than 512 KiB");
    }

    @Test
    void allowsOnlyOneRunPerApplication() throws Exception {
        Path script = script("sleepMillis", "20000");
        AtomicBoolean cancel = new AtomicBoolean();
        AtomicReference<Throwable> first = new AtomicReference<>();
        Thread thread = new Thread(() -> {
            try {
                interviewer().run("p", settings(script, 60), cancel::get);
            } catch (Throwable throwable) {
                first.set(throwable);
            }
        });
        thread.start();
        long deadline = System.nanoTime() + 10_000_000_000L;
        while (!CopilotInterviewer.isRunning() && System.nanoTime() < deadline) {
            Thread.sleep(10);
        }
        CopilotInterviewer other = new CopilotInterviewer(temporary.resolve("other"), prefix,
                () -> SystemInfo.isWindows, MISSING_NAME);
        assertThatThrownBy(() -> other.run("p", settings(script, 60), () -> false))
                .hasMessageContaining("already running");
        cancel.set(true);
        thread.join(20_000);
        assertThat(first.get()).hasMessage("Feedback request cancelled.");
    }

    @Test
    void reportsMissingExecutablesWithSetupInstructions() throws IOException {
        Path missing = temporary.resolve("nope").resolve("copilot.exe");
        assertThatThrownBy(() -> interviewer().run("p", new Settings(missing.toString(), "", 60), () -> false))
                .hasMessageContaining("not found at " + missing).hasMessageContaining("Interviewer settings");
        for (boolean windows : new boolean[]{true, false}) {
            CopilotInterviewer onPath = new CopilotInterviewer(base, prefix, () -> windows, MISSING_NAME);
            assertThatThrownBy(() -> onPath.resolveExecutable(null)).hasMessageContaining("not found on PATH")
                    .hasMessageContaining("npm install -g @github/copilot");
            assertThatThrownBy(() -> onPath.resolveExecutable(" ")).isInstanceOf(InterviewException.class);
        }
        assertThat(CopilotInterviewer.isRunning()).isFalse();
    }

    @Test
    void findsExecutablesOnPath() throws InterviewException {
        String name = SystemInfo.isWindows ? "cmd" : "sh";
        String fileName = SystemInfo.isWindows ? "cmd.exe" : "sh";
        assertThat(Path.of(new CopilotInterviewer(base, prefix, () -> SystemInfo.isWindows, name)
                .resolveExecutable("")).getFileName().toString()).isEqualToIgnoringCase(fileName);
        if (SystemInfo.isWindows) {
            assertThat(new CopilotInterviewer(base, prefix, () -> false, fileName).resolveExecutable(null))
                    .endsWithIgnoringCase(fileName);
        }
    }

    @Test
    void reportsLaunchAndDirectoryFailures() throws IOException {
        Path script = script();
        CopilotInterviewer unstartable = new CopilotInterviewer(base, List.of(temporary.resolve("missing-program")
                .toString()), () -> SystemInfo.isWindows, MISSING_NAME);
        assertThatThrownBy(() -> unstartable.run("p", settings(script, 60), () -> false))
                .hasMessageStartingWith("Could not start Copilot CLI");

        Path fileBase = temporary.resolve("file-base");
        Files.writeString(fileBase, "not a directory");
        CopilotInterviewer blocked = new CopilotInterviewer(fileBase, prefix, () -> SystemInfo.isWindows, MISSING_NAME);
        assertThatThrownBy(() -> blocked.run("p", settings(script, 60), () -> false))
                .hasMessageStartingWith("Could not prepare the interviewer directory");
    }

    @Test
    void cleanupDeletesOnlyDirectoriesOfDeadProcesses() throws IOException {
        Path dead = marked("run-dead", "pid=2147483000\n");
        Path live = marked("run-live", "pid=" + ProcessHandle.current().pid() + "\n");
        Path malformed = marked("run-malformed", "pid=abc\n");
        Path unmarked = base.resolve("run-unmarked");
        Files.createDirectories(unmarked);

        interviewer().cleanupAbandoned();

        assertThat(dead).doesNotExist();
        assertThat(live).as("a concurrent run's directory survives").isDirectory();
        assertThat(malformed).isDirectory();
        assertThat(unmarked).isDirectory();
    }

    private Path marked(String name, String marker) throws IOException {
        Path directory = base.resolve(name);
        Files.createDirectories(directory);
        Files.writeString(directory.resolve(CopilotInterviewer.MARKER), marker);
        Files.writeString(directory.resolve("leftover.txt"), "x");
        return directory;
    }
}
