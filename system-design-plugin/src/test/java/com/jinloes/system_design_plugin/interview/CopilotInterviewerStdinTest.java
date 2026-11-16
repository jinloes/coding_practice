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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CopilotInterviewerStdinTest {
    private static final String MISSING_NAME = "no-such-copilot-cli-for-tests";

    @TempDir Path temporary;
    private Path base;
    private Path record;
    private List<String> prefix;

    @BeforeEach
    void setUp() throws IOException {
        base = temporary.resolve("interviewer");
        record = temporary.resolve("record");
        Path argFile = temporary.resolve("fake.args");
        String classpath = System.getProperty("design.test.classpath");
        Files.writeString(argFile, "-cp \"" + classpath.replace("\\", "\\\\") + "\"\n");
        prefix = List.of(Path.of(System.getProperty("java.home"), "bin", "java").toString(), "@" + argFile,
                FakeCopilotCli.class.getName());
    }

    private CopilotInterviewer interviewer() {
        return new CopilotInterviewer(base, prefix, () -> SystemInfo.isWindows, MISSING_NAME);
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
    void stopsAtTimeoutEvenWhenProcessHangsBeforeReadingLargeStdin() throws IOException {
        Path script = script("hangBeforeStdin", "true", "sleepMillis", "20000");
        String largePrompt = "p".repeat(2_000_000);
        long started = System.nanoTime();

        assertThatThrownBy(() -> interviewer().run(largePrompt, new Settings(script.toString(), "", 1), () -> false))
                .isInstanceOf(InterviewException.class)
                .hasMessageContaining("did not answer within 1 seconds");
        assertThat(System.nanoTime() - started).isLessThan(15_000_000_000L);
        assertThat(CopilotInterviewer.isRunning()).isFalse();
    }

    @Test
    void stopsOnCancelEvenWhenProcessHangsBeforeReadingLargeStdin() throws IOException {
        Path script = script("hangBeforeStdin", "true", "sleepMillis", "20000");
        String largePrompt = "p".repeat(2_000_000);
        long started = System.nanoTime();

        assertThatThrownBy(() -> interviewer().run(largePrompt, new Settings(script.toString(), "", 60),
                () -> System.nanoTime() - started > 300_000_000L))
                .isInstanceOf(InterviewException.class)
                .hasMessage("Feedback request cancelled.");
        assertThat(System.nanoTime() - started).isLessThan(15_000_000_000L);
        assertThat(CopilotInterviewer.isRunning()).isFalse();
    }

    @Test
    void normalizesRelativeCliPathAgainstProcessWorkingDirectory() throws Exception {
        Path cwdRelative = Path.of("build", "test-relative-cli-" + System.nanoTime() + ".properties");
        Files.createDirectories(cwdRelative.toAbsolutePath().getParent());
        try {
            Properties properties = new Properties();
            properties.setProperty("record", record.toString());
            properties.setProperty("stdout", InterviewFeedbackTest.valid().toString());
            try (Writer writer = Files.newBufferedWriter(cwdRelative, StandardCharsets.UTF_8)) {
                properties.store(writer, null);
            }
            String resolved = interviewer().resolveExecutable(cwdRelative.toString());
            assertThat(Path.of(resolved)).isAbsolute().isEqualTo(cwdRelative.toAbsolutePath().normalize());

            InterviewFeedback feedback = interviewer().run("notes", new Settings(cwdRelative.toString(), "", 60),
                    () -> false);
            assertThat(feedback.parsed()).isTrue();
        } finally {
            Files.deleteIfExists(cwdRelative);
        }
    }
}
