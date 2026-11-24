package com.jinloes.system_design_plugin.interview;

import com.intellij.openapi.util.SystemInfo;
import com.jinloes.plugin_common.copilot.CopilotModels;
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
        return new CopilotInterviewer(base, prefix, MISSING_NAME);
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
        Path fastExit = script("stdout", "x".repeat(600_000), "stdoutRepeat", "1", "sleepMillis", "0");
        CopilotInterviewer longPoll = new CopilotInterviewer(
                base, prefix, MISSING_NAME, 10_000);
        assertThatThrownBy(() -> longPoll.run("p", settings(fastExit, 60), () -> false))
                .isInstanceOf(InterviewException.class)
                .hasMessageContaining("more than 512 KiB");

        Path midRun = script("stdout", "x".repeat(1024), "stdoutRepeat", "600", "sleepAfterStdoutMillis", "5000");
        assertThatThrownBy(() -> interviewer().run("p", settings(midRun, 60), () -> false))
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
        CopilotInterviewer other = new CopilotInterviewer(temporary.resolve("other"), prefix, MISSING_NAME);
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
        CopilotInterviewer onPath = new CopilotInterviewer(base, prefix, MISSING_NAME);
        assertThatThrownBy(() -> onPath.resolveExecutable(null)).hasMessageContaining("not found on PATH")
                .hasMessageContaining("npm install -g @github/copilot");
        assertThatThrownBy(() -> onPath.resolveExecutable(" ")).isInstanceOf(InterviewException.class);
        assertThat(CopilotInterviewer.isRunning()).isFalse();
    }

    @Test
    void findsExecutablesOnPath() throws InterviewException {
        String name = SystemInfo.isWindows ? "cmd" : "sh";
        String fileName = SystemInfo.isWindows ? "cmd.exe" : "sh";
        assertThat(Path.of(new CopilotInterviewer(base, prefix, name)
                .resolveExecutable("")).getFileName().toString()).isEqualToIgnoringCase(fileName);
    }

    @Test
    void reportsLaunchAndDirectoryFailures() throws IOException {
        Path script = script();
        CopilotInterviewer unstartable = new CopilotInterviewer(base, List.of(temporary.resolve("missing-program")
                .toString()), MISSING_NAME);
        assertThatThrownBy(() -> unstartable.run("p", settings(script, 60), () -> false))
                .hasMessageStartingWith("Could not start Copilot CLI");

        Path fileBase = temporary.resolve("file-base");
        Files.writeString(fileBase, "not a directory");
        CopilotInterviewer blocked = new CopilotInterviewer(fileBase, prefix, MISSING_NAME);
        assertThatThrownBy(() -> blocked.run("p", settings(script, 60), () -> false))
                .hasMessageStartingWith("Could not prepare the interviewer directory");

        interviewer().deleteRunDirectory(base.resolve("already-missing"));
        try {
            new CopilotInterviewer().resolveExecutable("");
        } catch (InterviewException ignored) {
            // Exercises the default constructor whether or not copilot is installed on PATH.
        }
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
