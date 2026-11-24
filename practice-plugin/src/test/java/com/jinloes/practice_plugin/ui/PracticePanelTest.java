package com.jinloes.practice_plugin.ui;

import com.intellij.execution.ExecutionException;
import com.intellij.openapi.actionSystem.ActionUiKind;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.util.Disposer;
import com.intellij.testFramework.EdtTestUtil;
import com.intellij.testFramework.PlatformTestUtil;
import com.intellij.testFramework.fixtures.IdeaProjectTestFixture;
import com.intellij.testFramework.fixtures.IdeaTestFixtureFactory;
import com.intellij.ui.SearchTextField;
import com.intellij.ui.components.JBList;
import com.intellij.ui.components.JBTabbedPane;
import com.intellij.util.ui.UIUtil;
import com.jinloes.practice_plugin.catalog.ExerciseCatalog;
import com.jinloes.practice_plugin.catalog.ExerciseCatalog.Exercise;
import com.jinloes.practice_plugin.run.CheckResult;
import com.jinloes.practice_plugin.run.PracticeRunner;
import com.jinloes.practice_plugin.run.RunEvent;
import com.jinloes.practice_plugin.state.ManagedPracticeProgress;
import com.jinloes.practice_plugin.workspace.ManagedPracticeWorkspace;
import com.jinloes.practice_plugin.workspace.PracticeModuleWorkspace;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import javax.swing.JComboBox;
import javax.swing.JPanel;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.IntStream;

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
            EdtTestUtil.runInEdtAndWait(() -> {
                PracticeModuleWorkspace.get(fixture.getProject()).dispose();
                fixture.tearDown();
            });
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
    void hintsTabStaysUnnumberedAndCatalogToggleAndResponsiveLayoutWork() {
        EdtTestUtil.runInEdtAndWait(() -> {
            PracticePanel panel = new PracticePanel(fixture.getProject());
            try {
                @SuppressWarnings("unchecked")
                JBList<Exercise> list = UIUtil.findComponentsOfType(panel, JBList.class).stream()
                        .filter(candidate -> candidate.getModel().getSize() > 0
                                && candidate.getModel().getElementAt(0) instanceof Exercise)
                        .findFirst().orElseThrow();
                Exercise withoutAttempts = ExerciseCatalog.all().stream()
                        .filter(exercise -> attemptsOf(exercise).isEmpty())
                        .findFirst().orElseThrow();
                list.setSelectedValue(withoutAttempts, true);
                assertThat(list.getSelectedValue()).isEqualTo(withoutAttempts);
                assertThat(panel.hintsTabTitle()).isEqualTo("Hints");

                assertThat(panel.isCatalogVisible()).isTrue();
                panel.setCatalogVisible(false);
                assertThat(panel.isCatalogVisible()).isFalse();
                panel.setCatalogVisible(true);
                assertThat(panel.isCatalogVisible()).isTrue();

                assertThat(panel.isHorizontalSplit()).isFalse();
                panel.applyResponsiveLayout(PracticePanel.WIDE_LAYOUT_THRESHOLD_PX);
                assertThat(panel.isHorizontalSplit()).isTrue();
                panel.applyResponsiveLayout(PracticePanel.WIDE_LAYOUT_THRESHOLD_PX - 1);
                assertThat(panel.isHorizontalSplit()).isFalse();
            } finally {
                Disposer.dispose(panel);
            }
        });
    }

    private static List<ManagedPracticeWorkspace.Attempt> attemptsOf(Exercise exercise) {
        try {
            return ManagedPracticeWorkspace.get().attempts(exercise.id());
        } catch (IOException error) {
            throw new UncheckedIOException(error);
        }
    }

    @Test
    void resolvesOneSharedWorkspaceForEveryCaller() {
        assertThat(ManagedPracticeWorkspace.get())
                .as("the panel and the runner must resolve the same workspace, not one each")
                .isSameAs(ManagedPracticeWorkspace.get());
    }

    @Test
    void runToolbarExposesDebugExamplesAlongsideRunAndCheckActions() {
        EdtTestUtil.runInEdtAndWait(() -> {
            AtomicBoolean debuggedExamples = new AtomicBoolean();
            PracticeActions.RunControls controls = new PracticeActions.RunControls() {
                @Override public boolean canRun() { return true; }
                @Override public boolean isRunning() { return false; }
                @Override public void runExamples() {}
                @Override public void debugExamples() { debuggedExamples.set(true); }
                @Override public void runWithInput(boolean debug) {}
                @Override public void check() {}
                @Override public void stop() {}
            };
            var group = (DefaultActionGroup) PracticeActions.runToolbar(controls, new JPanel()).getActionGroup();
            var actions = Arrays.asList(group.getChildActionsOrStubs());

            assertThat(actions).extracting(action -> action.getTemplatePresentation().getText())
                    .containsExactly(
                            "Run Examples",
                            "Debug Examples",
                            "Run With Input...",
                            "Debug With Input...",
                            "Check Solution",
                            "Stop");
            var debugAction = actions.stream()
                    .filter(action -> "Debug Examples".equals(action.getTemplatePresentation().getText()))
                    .findFirst().orElseThrow();
            debugAction.actionPerformed(AnActionEvent.createEvent(
                    debugAction, DataContext.EMPTY_CONTEXT, null, "test", ActionUiKind.NONE, null));
            assertThat(debuggedExamples).isTrue();
        });
    }

    @Test
    void editingAnUncheckedAttemptDoesNotClaimAStaleResultUntilAResultExists() throws Exception {
        var attempt = ManagedPracticeWorkspace.get().create(ExerciseCatalog.find("pair-sum"));
        ManagedPracticeProgress.get().getState().selectedAttempts.put("pair-sum", attempt.id());
        EdtTestUtil.runInEdtAndWait(() -> {
            PracticePanel panel = new PracticePanel(fixture.getProject());
            try {
                @SuppressWarnings("unchecked")
                JComboBox<ManagedPracticeWorkspace.Attempt> combo =
                        UIUtil.findComponentsOfType(panel, JComboBox.class).stream()
                                .filter(box -> "Saved attempts".equals(box.getAccessibleContext().getAccessibleName()))
                                .findFirst()
                                .orElseThrow();
                combo.addItem(attempt);
                combo.setSelectedItem(attempt);

                panel.onAttemptEdited();
                assertThat(panel.statusText()).doesNotContain("Edited since displayed result");
                assertThat(panel.resultsView().bannerText()).doesNotContain("stale");

                panel.resultsView().showRunFinished(
                        "Example session finished (exit 0). Use Check Solution to update correctness progress.");
                panel.onAttemptEdited();
                panel.onAttemptEdited();

                assertThat(panel.statusText()).isEqualTo("Edited since displayed result; check again.");
                assertThat(panel.resultsView().bannerText()).isEqualTo("Examples passed (stale)");
            } finally {
                Disposer.dispose(panel);
            }
        });
    }

    @Test
    void difficultyFilterOrdersLevelsFromEasyToHard() {
        EdtTestUtil.runInEdtAndWait(() -> {
            PracticePanel panel = new PracticePanel(fixture.getProject());
            try {
                JComboBox<?> difficulty = UIUtil.findComponentsOfType(panel, JComboBox.class)
                        .stream()
                        .filter(box -> "Difficulty filter".equals(box.getAccessibleContext().getAccessibleName()))
                        .findFirst()
                        .orElseThrow();
                List<Object> items = IntStream.range(0, difficulty.getItemCount())
                        .mapToObj(index -> (Object) difficulty.getItemAt(index))
                        .toList();

                assertThat(items).containsExactly("All difficulties", "Easy", "Medium", "Hard");
            } finally {
                Disposer.dispose(panel);
            }
        });
    }

    @Test
    void asyncExecutionStartFailureShowsActionFailedInsteadOfPreviousPassedBanner() throws Exception {
        var workspace = ManagedPracticeWorkspace.get();
        var attempt = workspace.create(ExerciseCatalog.find("pair-sum"));
        var progress = ManagedPracticeProgress.get();
        progress.getState().selectedAttempts.put("pair-sum", attempt.id());
        progress.record(attempt.id(), "pair-sum", workspace.fingerprint(attempt),
                new CheckResult(CheckResult.Status.PASSED, 5, 0, "All passed"));

        EdtTestUtil.runInEdtAndWait(() -> {
            PracticePanel panel = new PracticePanel(fixture.getProject());
            try {
                PlatformTestUtil.dispatchAllInvocationEventsInIdeEventQueue();
                assertThat(panel.resultsView().bannerText()).isEqualTo("Passed");

                PracticeRunner runner = fixture.getProject().getService(PracticeRunner.class);
                CompletableFuture.runAsync(() -> {
                    try {
                        runner.runExamples("missing-attempt");
                    } catch (ExecutionException ignored) {
                    }
                }).join();
                PlatformTestUtil.dispatchAllInvocationEventsInIdeEventQueue();

                assertThat(panel.resultsView().bannerText()).isEqualTo("Action failed");
                assertThat(panel.resultsView().outputText()).contains("Cannot start practice execution:");
            } finally {
                fixture.getProject().getService(PracticeRunner.class).stop();
                Disposer.dispose(panel);
            }
        });
    }

    @Test
    void checkCompletionUpdatesSelectedAttemptOrLeavesSwitchedExerciseResultsUntouched() throws Exception {
        var pairAttempt = ManagedPracticeWorkspace.get().create(ExerciseCatalog.find("pair-sum"));
        var progress = ManagedPracticeProgress.get();
        progress.getState().selectedAttempts.put("pair-sum", pairAttempt.id());
        progress.record(pairAttempt.id(), "pair-sum", "fp-pass",
                new CheckResult(CheckResult.Status.PASSED, 5, 0, "All passed"));

        EdtTestUtil.runInEdtAndWait(() -> {
            PracticePanel panel = new PracticePanel(fixture.getProject());
            try {
                PlatformTestUtil.dispatchAllInvocationEventsInIdeEventQueue();
                panel.resultsView().clear();
                var method = PracticePanel.class.getDeclaredMethod("onRunEvent", RunEvent.class);
                method.setAccessible(true);

                method.invoke(panel, new RunEvent.CheckFinished(pairAttempt.id(), "pair-sum", "Full check: PASSED"));
                assertThat(panel.resultsView().bannerText()).isEqualTo("Passed");
                assertThat(panel.resultsView().outputText()).isEqualTo("Full check: PASSED");

                @SuppressWarnings("unchecked")
                JBList<Exercise> list = (JBList<Exercise>) panel.preferredFocusedComponent();
                list.setSelectedValue(ExerciseCatalog.find("binary-search"), true);
                PlatformTestUtil.dispatchAllInvocationEventsInIdeEventQueue();
                panel.resultsView().showMessage("Viewing binary search", "Current tab");

                method.invoke(panel, new RunEvent.Started(pairAttempt.id(), "Running pair-sum check"));
                assertThat(panel.resultsView().bannerText()).isEqualTo("Viewing binary search");

                method.invoke(panel, new RunEvent.CheckFinished(pairAttempt.id(), "pair-sum", "Full check: PASSED"));
                assertThat(panel.resultsView().bannerText()).isEqualTo("Viewing binary search");
                assertThat(panel.resultsView().outputText()).isEqualTo("Current tab");
                assertThat(panel.statusText()).contains("Check finished for " + pairAttempt.id() + ": Passed");
            } catch (ReflectiveOperationException error) {
                throw new AssertionError(error);
            } finally {
                Disposer.dispose(panel);
            }
        });
    }

    @Test
    void enterAndDoubleClickResumeExistingAttemptWhileIgnoringBlankSpace() throws Exception {
        Exercise exercise = ExerciseCatalog.find("valid-anagram");
        var existing = ManagedPracticeWorkspace.get().create(exercise);
        int countBefore = ManagedPracticeWorkspace.get().attempts(exercise.id()).size();
        ManagedPracticeProgress.get().getState().selectedAttempts.put(exercise.id(), existing.id());

        EdtTestUtil.runInEdtAndWait(() -> {
            PracticePanel panel = new PracticePanel(fixture.getProject());
            try {
                assertThat(UIUtil.findComponentsOfType(panel, JComboBox.class))
                        .isNotEmpty()
                        .allMatch(ComboBox.class::isInstance);

                @SuppressWarnings("unchecked")
                JBList<Exercise> list = (JBList<Exercise>) panel.preferredFocusedComponent();
                list.setSize(280, 400);
                list.setSelectedValue(exercise, true);
                @SuppressWarnings("unchecked")
                JComboBox<ManagedPracticeWorkspace.Attempt> attemptsBox =
                        UIUtil.findComponentsOfType(panel, JComboBox.class).stream()
                                .filter(box -> "Saved attempts".equals(box.getAccessibleContext().getAccessibleName()))
                                .findFirst()
                                .orElseThrow();
                attemptsBox.removeAllItems();

                var enterAction = list.getActionMap().get("practiceStartOrResume");
                assertThat(enterAction).isNotNull();
                enterAction.actionPerformed(new ActionEvent(list, 0, "enter"));
                PlatformTestUtil.dispatchAllInvocationEventsInIdeEventQueue();
                assertThat(attemptsOf(exercise)).hasSize(countBefore);
                assertThat(attemptsBox.getSelectedItem()).isEqualTo(existing);

                attemptsBox.removeAllItems();
                int selectedIndex = list.getSelectedIndex();
                Rectangle bounds = list.getCellBounds(selectedIndex, selectedIndex);
                assertThat(bounds).isNotNull();
                HtmlPageViewTest.dispatchDoubleClick(list, bounds.x + 8, bounds.y + bounds.height / 2, 1_000L);
                PlatformTestUtil.dispatchAllInvocationEventsInIdeEventQueue();
                assertThat(attemptsOf(exercise)).hasSize(countBefore);
                assertThat(attemptsBox.getSelectedItem()).isEqualTo(existing);

                Exercise freshExercise = ExerciseCatalog.all().stream()
                        .filter(candidate -> attemptsOf(candidate).isEmpty())
                        .findFirst()
                        .orElseThrow();
                UIUtil.findComponentsOfType(panel, SearchTextField.class).stream().findFirst().orElseThrow()
                        .setText(freshExercise.title());
                PlatformTestUtil.dispatchAllInvocationEventsInIdeEventQueue();
                Rectangle singleRow = list.getCellBounds(0, 0);
                HtmlPageViewTest.dispatchDoubleClick(list, singleRow.x + 8, singleRow.y + singleRow.height + 60, 5_000L);
                PlatformTestUtil.dispatchAllInvocationEventsInIdeEventQueue();
                assertThat(attemptsOf(freshExercise)).isEmpty();
            } finally {
                Disposer.dispose(panel);
            }
        });
    }

    @Test
    void selectingOrOpeningAProblemAutomaticallyShowsTheProblemTab() {
        EdtTestUtil.runInEdtAndWait(() -> {
            PracticePanel panel = new PracticePanel(fixture.getProject());
            try {
                @SuppressWarnings("unchecked")
                JBList<Exercise> list = (JBList<Exercise>) panel.preferredFocusedComponent();
                JBTabbedPane tabs = UIUtil.findComponentsOfType(panel, JBTabbedPane.class)
                        .stream().findFirst().orElseThrow();
                Exercise first = list.getModel().getElementAt(0);
                Exercise second = list.getModel().getElementAt(1);

                list.setSelectedValue(first, true);
                tabs.setSelectedIndex(3); // Results tab
                assertThat(tabs.getTitleAt(tabs.getSelectedIndex())).isEqualTo("Results");

                list.setSelectedValue(second, true);
                assertThat(tabs.getTitleAt(tabs.getSelectedIndex())).isEqualTo("Problem");

                tabs.setSelectedIndex(3); // Results tab again
                panel.startOrOpenAttempt();
                assertThat(tabs.getTitleAt(tabs.getSelectedIndex())).isEqualTo("Problem");
            } finally {
                Disposer.dispose(panel);
            }
        });
    }
}
