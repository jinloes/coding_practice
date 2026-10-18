package com.jinloes.practice_plugin.run;

import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.process.ProcessAdapter;
import com.intellij.execution.process.ProcessEvent;
import com.intellij.execution.process.ProcessOutputTypes;
import com.intellij.openapi.util.Key;
import com.intellij.testFramework.EdtTestUtil;
import com.intellij.testFramework.fixtures.IdeaProjectTestFixture;
import com.intellij.testFramework.fixtures.IdeaTestFixtureFactory;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.assertj.core.api.Assertions.assertThat;

class BoundedProcessHandlerAdditionalPathsTest {
    private IdeaProjectTestFixture fixture;
    private Path host;

    @BeforeEach
    void setUpApplication() throws Exception {
        host = Path.of("build", "agent-fixtures", "bounded-process-" + System.nanoTime())
                .toAbsolutePath().normalize();
        Files.createDirectories(host);
        EdtTestUtil.runInEdtAndWait(() -> {
            fixture = IdeaTestFixtureFactory.getFixtureFactory()
                    .createFixtureBuilder("bounded-process", host, false).getFixture();
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
    void killPathReportsCancellation() throws Exception {
        AtomicBoolean cancelled = new AtomicBoolean();
        BoundedProcessHandler handler = shortLivedHandler(() -> cancelled.set(true));

        handler.startNotify();
        assertThat(handler.waitFor(5_000)).isTrue();
        handler.killProcess();

        assertThat(cancelled).isTrue();
    }

    @Test
    void coloredOutputSkipsAfterCapAndEmitsTruncationNotice() throws Exception {
        BoundedProcessHandler handler = shortLivedHandler(() -> {});
        StringBuilder console = new StringBuilder();
        handler.addProcessListener(new ProcessAdapter() {
            @Override
            public void onTextAvailable(@NotNull ProcessEvent event, @NotNull Key outputType) {
                console.append(event.getText());
            }
        });
        handler.startNotify();
        assertThat(handler.waitFor(5_000)).isTrue();

        handler.coloredTextAvailable("a".repeat(140 * 1024), ProcessOutputTypes.STDOUT);
        handler.coloredTextAvailable("ignored", ProcessOutputTypes.STDOUT);

        assertThat(console).hasSize(128 * 1024 + "\n[Practice console truncated at 128 KiB.]\n".length());
        assertThat(console).endsWith("[Practice console truncated at 128 KiB.]\n");
    }

    private static BoundedProcessHandler shortLivedHandler(Runnable cancelled) throws Exception {
        return new BoundedProcessHandler(
                new GeneralCommandLine("cmd.exe", "/c", "exit", "/b", "0"), cancelled);
    }
}
