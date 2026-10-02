package com.jinloes.practice_plugin.ui;

import com.intellij.openapi.util.Disposer;
import com.intellij.testFramework.EdtTestUtil;
import com.intellij.testFramework.fixtures.IdeaProjectTestFixture;
import com.intellij.testFramework.fixtures.IdeaTestFixtureFactory;
import com.jinloes.practice_plugin.catalog.ExerciseCatalog;
import com.jinloes.practice_plugin.run.PracticeRunner;
import com.jinloes.practice_plugin.workspace.ManagedPracticeWorkspace;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;

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
    void guideFilterListsItsProblemsEasiestFirstAndCombinesWithOtherFilters() {
        EdtTestUtil.runInEdtAndWait(() -> {
            ExerciseFilters filters = new ExerciseFilters();

            filters.selectGuide("stacks-queues");
            assertThat(filters.visible(Set.of())).extracting(ExerciseCatalog.Exercise::id)
                    .as("the guide's exercises, Easy then Medium then Hard")
                    .containsExactly("balanced-delimiters", "array-stack", "sliding-window-maximum");

            filters.selectDifficulty("Medium");
            assertThat(filters.visible(Set.of())).extracting(ExerciseCatalog.Exercise::id)
                    .as("the existing filters still narrow a guide")
                    .containsExactly("array-stack");

            filters.selectGuide(null);
            filters.selectDifficulty("All difficulties");
            assertThat(filters.selectedGuide()).isNull();
            assertThat(filters.visible(Set.of()))
                    .as("all guides keeps the catalog order")
                    .containsExactlyElementsOf(ExerciseCatalog.all());
        });
    }

    @Test
    void trackFilterListsItsProblemsAndIntersectsWithAGuide() {
        EdtTestUtil.runInEdtAndWait(() -> {
            ExerciseFilters filters = new ExerciseFilters();

            filters.selectTrack("google");
            assertThat(filters.shownGuide()).isSameAs(filters.selectedTrack());
            assertThat(filters.visible(Set.of()))
                    .as("the track's exercises, Easy then Medium then Hard")
                    .containsExactlyElementsOf(filters.selectedTrack().exercises());

            filters.selectGuide("stacks-queues");
            assertThat(filters.shownGuide()).isSameAs(filters.selectedGuide());
            assertThat(filters.visible(Set.of())).extracting(ExerciseCatalog.Exercise::id)
                    .as("a guide narrowed to the problems in the track")
                    .containsExactly("balanced-delimiters", "sliding-window-maximum");

            filters.selectGuide(null);
            filters.selectTrack(null);
            assertThat(filters.shownGuide()).isNull();
            assertThat(filters.visible(Set.of())).containsExactlyElementsOf(ExerciseCatalog.all());
        });
    }

    @Test
    void resolvesOneSharedWorkspaceForEveryCaller() {
        assertThat(ManagedPracticeWorkspace.get())
                .as("the panel and the runner must resolve the same workspace, not one each")
                .isSameAs(ManagedPracticeWorkspace.get());
    }
}
