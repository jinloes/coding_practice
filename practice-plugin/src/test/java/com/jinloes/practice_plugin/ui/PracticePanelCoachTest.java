package com.jinloes.practice_plugin.ui;

import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.testFramework.EdtTestUtil;
import com.intellij.testFramework.fixtures.IdeaProjectTestFixture;
import com.intellij.testFramework.fixtures.IdeaTestFixtureFactory;
import com.intellij.ui.components.JBTabbedPane;
import com.intellij.util.ui.UIUtil;
import com.jinloes.practice_plugin.catalog.ExerciseCatalog;
import com.jinloes.practice_plugin.catalog.ExerciseCatalog.Exercise;
import com.jinloes.practice_plugin.coach.CoachPrompt.Mode;
import com.jinloes.practice_plugin.run.CheckResult;
import com.jinloes.practice_plugin.state.ManagedPracticeProgress;
import com.jinloes.practice_plugin.workspace.ManagedPracticeWorkspace;
import com.jinloes.practice_plugin.workspace.PracticeModuleWorkspace;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import javax.swing.JComponent;
import javax.swing.JPanel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

class PracticePanelCoachTest {
    @TempDir Path temporary;
    private IdeaProjectTestFixture fixture;

    @BeforeEach
    void setUp() throws Exception {
        Path host = temporary.resolve("host");
        Files.createDirectories(host);
        EdtTestUtil.runInEdtAndWait(() -> {
            fixture = IdeaTestFixtureFactory.getFixtureFactory()
                    .createFixtureBuilder("algorithm-practice-coach-ui", host, false).getFixture();
            fixture.setUp();
        });
    }

    @AfterEach
    void tearDown() throws Exception {
        if (fixture != null) {
            EdtTestUtil.runInEdtAndWait(() -> {
                PracticeModuleWorkspace.get(fixture.getProject()).dispose();
                fixture.tearDown();
            });
        }
    }

    @Test
    void coachPanelTabButtonUnsavedBufferAndStaleReset() throws Exception {
        ManagedPracticeProgress.get().loadState(new ManagedPracticeProgress.Data());
        var workspace = ManagedPracticeWorkspace.get();
        Exercise pairSum = ExerciseCatalog.find("pair-sum");
        var attempt = workspace.create(pairSum);
        var progress = new ManagedPracticeProgress();
        progress.record(attempt.id(), pairSum.id(), "fp-fail",
                new CheckResult(CheckResult.Status.ASSERTION_FAILED, 5, 1, "wrong answer"));
        var rendered = new AtomicReference<PracticePage.Coach>();
        PageView fakeView = new PageView() {
            @Override public JComponent component() { return new JPanel(); }
            @Override public void show(PracticePage page) { rendered.set((PracticePage.Coach) page); }
        };
        var captured = new ArrayList<String>();
        var controller = new CoachController(
                fixture.getProject(), progress, fakeView, () -> pairSum, () -> attempt,
                Set::of, () -> false, () -> true,
                (p, s, c) -> { captured.add(p); return "ok"; },
                AttemptFiles::readCurrentSource, Runnable::run, Runnable::run);

        EdtTestUtil.runInEdtAndWait(() -> {
            PracticePanel panel = new PracticePanel(fixture.getProject());
            try {
                assertThat(panel.titleActions())
                        .extracting(a -> a.getTemplatePresentation().getText()).contains("AI Coach Settings");
                var tabs = UIUtil.findComponentsOfType(panel, JBTabbedPane.class).stream().findFirst().orElseThrow();
                assertThat(IntStream.range(0, tabs.getTabCount()).mapToObj(tabs::getTitleAt)).contains("Coach");
                assertThat(panel.resultsView().isAskCoachEnabled()).isFalse();
            } finally {
                Disposer.dispose(panel);
            }
            var vf = LocalFileSystem.getInstance().refreshAndFindFileByNioFile(attempt.solution());
            var doc = FileDocumentManager.getInstance().getDocument(vf);
            WriteCommandAction.runWriteCommandAction(fixture.getProject(),
                    () -> doc.setText("// UNSAVED_EDITOR_BUFFER_MARKER\nclass Solution {}"));
            controller.onAttemptEdited(attempt, pairSum);
            assertThat(rendered.get().stale()).isTrue();
            controller.ask(Mode.TRANSLATE, "");
            assertThat(captured).singleElement().asString().contains("UNSAVED_EDITOR_BUFFER_MARKER");

            FileDocumentManager.getInstance().saveDocument(doc);
            progress.record(attempt.id(), pairSum.id(), workspace.fingerprint(attempt),
                    new CheckResult(CheckResult.Status.PASSED, 5, 0, "all passed"));
            controller.onCheckFinished(attempt, pairSum, workspace,
                    new BackgroundTasks(fixture.getProject(), () -> true, i -> {}, () -> {}));
            assertThat(rendered.get().stale()).isFalse();
        });
    }
}
