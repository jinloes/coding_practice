package com.jinloes.practice_plugin.ui;

import com.jinloes.practice_plugin.catalog.ExerciseCatalog;
import com.jinloes.practice_plugin.catalog.GuideCatalog;
import com.jinloes.practice_plugin.run.CheckResult;
import org.junit.jupiter.api.Test;

import javax.swing.JLabel;
import javax.swing.JComponent;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;

class HtmlPageViewTest {

    @Test
    void rendersTheProblemWithMetadataMembershipsOrItsEmptyState() {
        var exercise = ExerciseCatalog.find("merge-sorted-lists");
        String unpassed = HtmlPageView.html(new PracticePage.Problem(exercise, Set.of()));
        String passed = HtmlPageView.html(new PracticePage.Problem(exercise, Set.of(exercise.id())));

        assertThat(unpassed)
                .isEqualTo(PracticeViews.problemText(exercise, Set.of()))
                .contains(exercise.title(), exercise.difficulty(), exercise.topic(),
                        exercise.intendedTime(), exercise.intendedSpace(), exercise.inputSyntax(),
                        "Two Pointers", "Linked Lists", "Blind 75")
                .doesNotContain("(passed)");
        assertThat(passed).contains(exercise.title() + " (passed)");
        var unguided = new ExerciseCatalog.Exercise(
                "unguided-sample", "Unguided Sample", List.of("Topic"), "Easy", "Statement",
                List.of(), List.of(), "Input: x", "x", "O(1)", "O(1)", 0, 0);
        assertThat(HtmlPageView.html(new PracticePage.Problem(unguided, Set.of())))
                .contains("Not part of any guide yet.")
                .doesNotContain("Practice tracks:");
        assertThat(HtmlPageView.html(new PracticePage.Problem(null, Set.of())))
                .isEqualTo("<p>No exercises match these filters. Use Reset filters above the exercise list to show all exercises.</p>");
    }

    @Test
    void lazyPageViewDefersCreationAndCoalescesHiddenUpdatesUntilActivated() {
        AtomicInteger created = new AtomicInteger();
        List<PracticePage> shown = new ArrayList<>();
        PageView fake = new PageView() {
            private final JLabel label = new JLabel();

            @Override
            public JComponent component() {
                return label;
            }

            @Override
            public void show(PracticePage page) {
                shown.add(page);
            }
        };

        PageView lazy = PageView.lazy(() -> {
            created.incrementAndGet();
            return fake;
        }, false);
        PracticePage first = new PracticePage.Problem(null, Set.of());
        PracticePage second = new PracticePage.Problem(ExerciseCatalog.find("pair-sum"), Set.of("pair-sum"));

        lazy.show(first);
        lazy.show(second);
        assertThat(created).hasValue(0);
        assertThat(shown).isEmpty();

        lazy.setActive(true);
        assertThat(created).hasValue(1);
        assertThat(shown).containsExactly(second);

        lazy.setActive(false);
        lazy.setActive(true);
        assertThat(created).hasValue(1);
        assertThat(shown).as("unchanged state is not re-pushed on tab revisit").containsExactly(second);
    }

    @Test
    void rendersTheStudyGuideWithTheSharedRenderer() {
        var guide = GuideCatalog.find("graphs");

        assertThat(HtmlPageView.html(new PracticePage.Study(guide, null, Set.of("course-schedule"))))
                .isEqualTo(StudyGuideView.html(guide, null, Set.of("course-schedule")));
    }

    @Test
    void rendersHintsFallbackHtmlForEmptyLockedAndRevealedStates() {
        var exercise = ExerciseCatalog.find("pair-sum");
        assertThat(HtmlPageView.html(new PracticePage.Hints(null, Set.of(), false, 0)))
                .contains("Select an exercise to view hints.");
        assertThat(HtmlPageView.html(new PracticePage.Hints(exercise, Set.of(), false, 1)))
                .contains("Start or resume an attempt to reveal hints.");
        assertThat(HtmlPageView.html(new PracticePage.Hints(exercise, Set.of(), true, 1)))
                .contains("Hint 1 of " + exercise.hints().size());
    }

    @Test
    void rendersResultsFallbackHtmlForEmptyAndPopulatedStates() {
        assertThat(HtmlPageView.html(new PracticePage.Results(
                "idle", "", "", "", false, "", false, ""))).isEmpty();
        assertThat(HtmlPageView.html(new PracticePage.Results(
                "passed", "Passed", "2026-11-24T21:34:55Z", "", false, "", true, "All 12 passed.")))
                .contains("<h2>Passed</h2>", "Last full check: 2026-11-24T21:34:55Z", "Most recent pass: never",
                        "<pre>All 12 passed.</pre>");
        assertThat(HtmlPageView.html(new PracticePage.Results(
                "failed", "Failed", "", "2026-11-24T21:34:55Z", false, "", false, "")))
                .contains("Last full check: never", "Most recent pass: 2026-11-24T21:34:55Z");
    }

    @Test
    void rendersCoachFallbackHtmlForLockedAndUnlockedTranscripts() {
        var exercise = ExerciseCatalog.find("pair-sum");
        String locked = HtmlPageView.html(new PracticePage.Coach(
                exercise, "att-1", Set.of(), false, "Run Check first.", false, "Not checked yet", false, "", "CLI default", List.of()));
        assertThat(locked).contains("AI Coach — Pair Sum", "Not checked yet", "Run Check first.");

        String unlocked = HtmlPageView.html(new PracticePage.Coach(
                exercise, "att-1", Set.of(), true, "", true, "Passed", true, "Timed out", "gpt-5",
                List.of(
                        new PracticePage.CoachTurn("user", "Diagnose my attempt", "2026-01-01T00:00:00Z"),
                        new PracticePage.CoachTurn("assistant", "## Bottleneck\n```java\nreturn 0;\n```",
                                "2026-01-01T00:00:01Z"))));
        assertThat(unlocked).contains("Edited since last check");
        assertThat(unlocked)
                .contains("Interactive AI Coach controls require JCEF.")
                .contains("AI Coach is thinking...")
                .contains("Timed out")
                .contains("<h3>You</h3>")
                .contains("<h3>Coach</h3>")
                .contains("<pre><code>return 0;</code></pre>");
    }

    @Test
    void resultsBannerHasAReadableLabelForEveryCheckStatus() {
        for (CheckResult.Status status : CheckResult.Status.values()) {
            assertThat(ResultsView.label(status.name()))
                    .as(status.name())
                    .isNotBlank()
                    .doesNotContain("_");
        }
        assertThat(ResultsView.label(null)).isEqualTo("Not checked yet");
        assertThat(ResultsView.label("PASSED")).isEqualTo("Passed");
    }

    @Test
    void htmlPaneUsesAPrivateStyleSheetAndRefreshesThemeWhilePreservingCaret() {
        javax.swing.text.html.StyleSheet globalStyles = new javax.swing.text.html.HTMLEditorKit().getStyleSheet();
        var pane = PracticeViews.htmlPane();
        var kit = (javax.swing.text.html.HTMLEditorKit) pane.getEditorKit();

        assertThat(kit.getStyleSheet())
                .as("plugin HTML panes must not mutate Swing's shared default StyleSheet")
                .isNotSameAs(globalStyles);

        AtomicReference<String> supplied = new AtomicReference<>("<p>Initial hint body</p>");
        var suppliedPane = PracticeViews.htmlPane(supplied::get);
        assertThat(suppliedPane.getText())
                .as("supplier-backed HTML panes populate their initial document during construction")
                .contains("Initial hint body");
        assertThat(suppliedPane.getDocument().getLength()).isPositive();
        supplied.set("<p>Updated hint body</p>");
        suppliedPane.updateUI();
        assertThat(suppliedPane.getText()).contains("Updated hint body");

        HtmlPageView view = new HtmlPageView();
        view.show(new PracticePage.Problem(ExerciseCatalog.find("pair-sum"), Set.of("pair-sum")));
        javax.swing.JEditorPane viewPane = (javax.swing.JEditorPane)
                ((javax.swing.JScrollPane) view.component()).getViewport().getView();
        assertThat(viewPane.getText()).contains("Pair Sum (passed)");
        int movedCaret = Math.min(12, viewPane.getDocument().getLength());
        viewPane.setCaretPosition(movedCaret);

        javax.swing.text.html.HTMLEditorKit beforeRefresh =
                (javax.swing.text.html.HTMLEditorKit) viewPane.getEditorKit();
        viewPane.updateUI();
        javax.swing.text.html.HTMLEditorKit afterRefresh =
                (javax.swing.text.html.HTMLEditorKit) viewPane.getEditorKit();

        assertThat(afterRefresh).isNotSameAs(beforeRefresh);
        assertThat(afterRefresh.getStyleSheet()).isNotSameAs(globalStyles);
        assertThat(viewPane.getText()).contains("Pair Sum (passed)");
        assertThat(viewPane.getCaretPosition()).isEqualTo(movedCaret);
    }

    @Test
    void listActivationFiresOnItemDoubleClickAndIgnoresBlankSpaceOrEmptyLists() {
        javax.swing.DefaultListModel<String> emptyModel = new javax.swing.DefaultListModel<>();
        javax.swing.JList<String> emptyList = new javax.swing.JList<>(emptyModel);
        emptyList.setSize(200, 240);
        AtomicInteger emptyActivations = new AtomicInteger();
        PracticeViews.installListActivation(emptyList, emptyActivations::incrementAndGet);
        dispatchDoubleClick(emptyList, 10, 10, 500L);
        assertThat(emptyActivations).hasValue(0);

        javax.swing.DefaultListModel<String> model = new javax.swing.DefaultListModel<>();
        model.addElement("Pair Sum");
        javax.swing.JList<String> list = new javax.swing.JList<>(model);
        list.setSize(200, 240);
        AtomicInteger activations = new AtomicInteger();
        PracticeViews.installListActivation(list, activations::incrementAndGet);

        java.awt.Rectangle rowBounds = list.getCellBounds(0, 0);
        assertThat(rowBounds).isNotNull();

        dispatchDoubleClick(list, rowBounds.x + 5, rowBounds.y + rowBounds.height + 40, 1_000L);
        assertThat(activations).as("blank space below the last row must not activate").hasValue(0);

        list.setSelectedIndex(0);
        dispatchDoubleClick(list, rowBounds.x + 5, rowBounds.y + Math.max(1, rowBounds.height / 2), 5_000L);
        assertThat(activations).as("double-clicking inside a row activates once").hasValue(1);
    }

    @Test
    void attemptBarIgnoresOutOfOrderLoadCallbacksAndConfinesProgressReadsToEdt() throws Exception {
        var exercise = ExerciseCatalog.find("pair-sum");
        java.util.concurrent.atomic.AtomicBoolean onEdt = new java.util.concurrent.atomic.AtomicBoolean(true);
        var progress = new com.jinloes.practice_plugin.state.ManagedPracticeProgress();
        progress.getState().selectedAttempts = new java.util.LinkedHashMap<>() {
            @Override
            public String get(Object key) {
                assertThat(onEdt.get())
                        .as("selectedAttempts must only be read on the EDT")
                        .isTrue();
                return super.get(key);
            }

            @Override
            public String put(String key, String value) {
                assertThat(onEdt.get())
                        .as("selectedAttempts must only be mutated on the EDT")
                        .isTrue();
                return super.put(key, value);
            }
        };
        List<com.jinloes.practice_plugin.workspace.ManagedPracticeWorkspace.Attempt> disk = new ArrayList<>();
        AttemptBar.AttemptStore store = new AttemptBar.AttemptStore() {
            @Override
            public List<com.jinloes.practice_plugin.workspace.ManagedPracticeWorkspace.Attempt> attempts(String id) {
                return List.copyOf(disk);
            }

            @Override
            public com.jinloes.practice_plugin.workspace.ManagedPracticeWorkspace.Attempt create(
                    ExerciseCatalog.Exercise target
            ) {
                String attemptId = target.id() + "-" + "%032x".formatted(disk.size() + 1);
                var created = new com.jinloes.practice_plugin.workspace.ManagedPracticeWorkspace.Attempt(
                        attemptId, target.id(), "1",
                        java.nio.file.Path.of("attempts", target.id(), attemptId,
                                "src/main/java/com/jinloes/practice/Solution.java"));
                disk.add(created);
                return created;
            }
        };
        java.util.ArrayDeque<Runnable> edtCallbacks = new java.util.ArrayDeque<>();
        AttemptBar.TaskRunner deferred = new AttemptBar.TaskRunner() {
            @Override
            public <T> void run(String title, BackgroundTasks.IoTask<T> operation,
                                java.util.function.Consumer<T> completed) {
                onEdt.set(false);
                try {
                    T result = operation.run();
                    edtCallbacks.addLast(() -> completed.accept(result));
                } catch (java.io.IOException error) {
                    throw new java.io.UncheckedIOException(error);
                } finally {
                    onEdt.set(true);
                }
            }
        };
        List<com.jinloes.practice_plugin.workspace.ManagedPracticeWorkspace.Attempt> opened = new ArrayList<>();
        AttemptBar bar = new AttemptBar(() -> {}, () -> {}, () -> {});

        bar.loadFor(exercise, 1, () -> 1, () -> exercise, store, progress, deferred);
        Runnable staleEmptyLoad = edtCallbacks.removeFirst();
        bar.startOrResume(store, progress, deferred, () -> false, () -> exercise, opened::add);
        edtCallbacks.removeFirst().run();
        var firstCreated = bar.selected();
        assertThat(firstCreated).isNotNull();
        staleEmptyLoad.run();
        assertThat(bar.items()).containsExactly(firstCreated);
        assertThat(bar.selected()).isEqualTo(firstCreated);

        bar.loadFor(exercise, 2, () -> 2, () -> exercise, store, progress, deferred);
        Runnable staleSingleLoad = edtCallbacks.removeFirst();
        bar.newAttempt(store, progress, deferred, () -> false, () -> exercise, opened::add);
        edtCallbacks.removeFirst().run();
        var secondCreated = bar.selected();
        assertThat(secondCreated).isNotNull().isNotEqualTo(firstCreated);
        staleSingleLoad.run();
        assertThat(bar.items()).containsExactly(firstCreated, secondCreated);
        assertThat(bar.selected()).isEqualTo(secondCreated);
        assertThat(opened).containsExactly(firstCreated, secondCreated);

        bar.clear();
        progress.getState().selectedAttempts.put(exercise.id(), firstCreated.id());
        bar.startOrResume(store, progress, deferred, () -> false, () -> exercise, opened::add);
        progress.getState().selectedAttempts.put(exercise.id(), secondCreated.id());
        edtCallbacks.removeFirst().run();
        assertThat(bar.selected()).isEqualTo(secondCreated);
        assertThat(opened).containsExactly(firstCreated, secondCreated, secondCreated);
    }

    static void dispatchDoubleClick(javax.swing.JComponent target, int x, int y, long startWhen) {
        Object installed = target.getClientProperty(PracticeViews.LIST_ACTIVATION);
        assertThat(installed).isInstanceOf(com.intellij.ui.DoubleClickListener.class);
        ((com.intellij.ui.DoubleClickListener) installed).onClick(new java.awt.event.MouseEvent(
                target, java.awt.event.MouseEvent.MOUSE_RELEASED, startWhen, 0, x, y,
                2, false, java.awt.event.MouseEvent.BUTTON1), 2);
    }
}
