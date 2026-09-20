package com.jinloes.practice_plugin.ui;

import com.intellij.openapi.util.Disposer;
import com.intellij.testFramework.EdtTestUtil;
import com.intellij.testFramework.fixtures.IdeaProjectTestFixture;
import com.intellij.testFramework.fixtures.IdeaTestFixtureFactory;
import com.jinloes.practice_plugin.run.PracticeRunner;
import com.jinloes.practice_plugin.workspace.ManagedPracticeWorkspace;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

class PracticePanelTest {
    @TempDir Path temporary;
    private IdeaProjectTestFixture fixture;

    @BeforeEach
    void setUp() throws Exception {
        Path host = temporary.resolve("host");
        Files.createDirectories(host);
        EdtTestUtil.runInEdtAndWait(() -> {
            fixture = IdeaTestFixtureFactory.getFixtureFactory()
                    .createFixtureBuilder("algorithm-practice-ui", host, false).getFixture();
            fixture.setUp();
        });
    }

    @AfterEach
    void tearDown() throws Exception {
        if (fixture != null) {
            EdtTestUtil.runInEdtAndWait(() -> fixture.tearDown());
        }
    }

    @Test
    void deregistersItsRunListenerOnDisposeSoReopeningTheToolWindowDoesNotLeakPanels() {
        EdtTestUtil.runInEdtAndWait(() -> {
            PracticeRunner runner = fixture.getProject().getService(PracticeRunner.class);
            int before = runner.listenerCount();

            PracticePanel first = new PracticePanel(fixture.getProject());
            assertThat(runner.listenerCount())
                    .as("an open panel listens for run output")
                    .isEqualTo(before + 1);

            PracticePanel second = new PracticePanel(fixture.getProject());
            assertThat(runner.listenerCount())
                    .as("the runner supports more than one listener at a time")
                    .isEqualTo(before + 2);

            Disposer.dispose(second);
            assertThat(runner.listenerCount())
                    .as("disposing one panel leaves the other registered")
                    .isEqualTo(before + 1);

            Disposer.dispose(first);
            assertThat(runner.listenerCount())
                    .as("every panel registration is released on dispose")
                    .isEqualTo(before);
        });
    }

    @Test
    void resolvesOneSharedWorkspaceForEveryCaller() {
        assertThat(ManagedPracticeWorkspace.get())
                .as("the panel and the runner must resolve the same workspace, not one each")
                .isSameAs(ManagedPracticeWorkspace.get());
    }
}
