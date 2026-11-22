package com.jinloes.practice_plugin.ui;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.progress.ProcessCanceledException;
import com.intellij.testFramework.EdtTestUtil;
import com.intellij.testFramework.PlatformTestUtil;
import com.jinloes.practice_plugin.catalog.ExerciseCatalog;
import com.jinloes.practice_plugin.workspace.ManagedPracticeWorkspace;
import org.junit.jupiter.api.Test;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AttemptBarTest {

    @Test
    void disablesActionButtonsWhileBusyAndRestoresThemOnCompletionErrorOrCancel() throws Exception {
        var exercise = ExerciseCatalog.find("pair-sum");
        var progress = new com.jinloes.practice_plugin.state.ManagedPracticeProgress();
        List<ManagedPracticeWorkspace.Attempt> disk = new ArrayList<>();
        AttemptBar.AttemptStore store = new AttemptBar.AttemptStore() {
            @Override
            public List<ManagedPracticeWorkspace.Attempt> attempts(String exerciseId) {
                return List.copyOf(disk);
            }

            @Override
            public ManagedPracticeWorkspace.Attempt create(ExerciseCatalog.Exercise target) {
                var created = new ManagedPracticeWorkspace.Attempt(
                        target.id() + "-" + "%032x".formatted(disk.size() + 1), target.id(), "1",
                        Path.of("attempts", target.id(), "Solution.java"));
                disk.add(created);
                return created;
            }
        };
        ArrayDeque<Runnable> pendingCompletions = new ArrayDeque<>();
        AttemptBar.TaskRunner manualTasks = new AttemptBar.TaskRunner() {
            @Override
            public <T> void run(String title, BackgroundTasks.IoTask<T> operation, Consumer<T> completed) {
                try {
                    T result = operation.run();
                    pendingCompletions.addLast(() -> completed.accept(result));
                } catch (IOException error) {
                    throw new AssertionError(error);
                }
            }
        };
        AtomicInteger onSelectCalls = new AtomicInteger();
        AttemptBar bar = new AttemptBar(_ -> "NOT_RUN", onSelectCalls::incrementAndGet, () -> {}, () -> {});
        JButton startButton = findButton(bar, "Start Attempt");
        JButton newAttemptButton = findButton(bar, "New Attempt");

        bar.startOrResume(store, progress, manualTasks, () -> false, () -> exercise, _ -> {});
        assertThat(startButton.isEnabled()).as("disabled while attempt creation is in flight").isFalse();
        assertThat(newAttemptButton.isEnabled()).isFalse();

        bar.newAttempt(store, progress, manualTasks, () -> false, () -> exercise, _ -> {});
        assertThat(disk).as("re-entrant click while busy does not create another attempt").hasSize(1);

        pendingCompletions.removeFirst().run();
        assertThat(startButton.isEnabled()).isTrue();
        assertThat(newAttemptButton.isEnabled()).isTrue();
        assertThat(startButton.getText()).isEqualTo("Open Solution");

        int selectionsBeforeNew = onSelectCalls.get();
        bar.newAttempt(store, progress, manualTasks, () -> false, () -> exercise, _ -> {});
        assertThat(startButton.isEnabled()).isFalse();
        pendingCompletions.removeFirst().run();
        assertThat(startButton.isEnabled()).isTrue();
        assertThat(onSelectCalls.get() - selectionsBeforeNew)
                .as("newAttempt notifies onSelect once instead of twice")
                .isEqualTo(1);

        List<String> errors = new ArrayList<>();
        AtomicInteger cancellations = new AtomicInteger();
        BackgroundTasks tasks = new BackgroundTasks(null, () -> true, message -> {
            errors.add(message);
            bar.resetBusy();
        }, () -> {
            cancellations.incrementAndGet();
            bar.resetBusy();
        });
        AttemptBar.TaskRunner directTasks = new AttemptBar.TaskRunner() {
            @Override
            public <T> void run(String title, BackgroundTasks.IoTask<T> operation, Consumer<T> completed) {
                try {
                    tasks.execute(operation, completed, new java.util.concurrent.atomic.AtomicBoolean());
                } finally {
                    if (ApplicationManager.getApplication() != null) {
                        EdtTestUtil.runInEdtAndWait(PlatformTestUtil::dispatchAllInvocationEventsInIdeEventQueue);
                    }
                }
            }
        };

        bar.clear();
        bar.startOrResume(new AttemptBar.AttemptStore() {
            @Override
            public List<ManagedPracticeWorkspace.Attempt> attempts(String exerciseId) throws IOException {
                throw new IOException("disk unavailable");
            }

            @Override
            public ManagedPracticeWorkspace.Attempt create(ExerciseCatalog.Exercise target) throws IOException {
                throw new IOException("disk unavailable");
            }
        }, progress, directTasks, () -> false, () -> exercise, _ -> {});
        assertThat(errors).containsExactly("disk unavailable");
        assertThat(startButton.isEnabled()).isTrue();

        bar.startOrResume(new AttemptBar.AttemptStore() {
            @Override
            public List<ManagedPracticeWorkspace.Attempt> attempts(String exerciseId) {
                throw new IllegalStateException();
            }

            @Override
            public ManagedPracticeWorkspace.Attempt create(ExerciseCatalog.Exercise target) {
                throw new IllegalStateException();
            }
        }, progress, directTasks, () -> false, () -> exercise, _ -> {});
        assertThat(errors).containsExactly("disk unavailable", "IllegalStateException");
        assertThat(startButton.isEnabled()).isTrue();

        assertThatThrownBy(() -> bar.startOrResume(new AttemptBar.AttemptStore() {
            @Override
            public List<ManagedPracticeWorkspace.Attempt> attempts(String exerciseId) {
                throw new ProcessCanceledException();
            }

            @Override
            public ManagedPracticeWorkspace.Attempt create(ExerciseCatalog.Exercise target) {
                throw new ProcessCanceledException();
            }
        }, progress, directTasks, () -> false, () -> exercise, _ -> {}))
                .isInstanceOf(ProcessCanceledException.class);
        assertThat(cancellations).hasValue(1);
        assertThat(startButton.isEnabled()).isTrue();
    }

    @Test
    void formatsAttemptLabelsWithIndexStatusAndShortSuffixAndUpdatesPrimaryButtonText() {
        Map<String, String> statuses = new HashMap<>();
        var first = new ManagedPracticeWorkspace.Attempt(
                "pair-sum-00000000000000000000000000abcdef", "pair-sum", "1",
                Path.of("attempts/pair-sum/first/Solution.java"));
        var second = new ManagedPracticeWorkspace.Attempt(
                "pair-sum-00000000000000000000000000123456", "pair-sum", "1",
                Path.of("attempts/pair-sum/second/Solution.java"));
        statuses.put(first.id(), "NOT_RUN");
        statuses.put(second.id(), "PASSED");

        AttemptBar bar = new AttemptBar(attempt -> statuses.get(attempt.id()), () -> {}, () -> {}, () -> {});
        JButton startButton = findButton(bar, "Start Attempt");
        @SuppressWarnings("unchecked")
        JComboBox<ManagedPracticeWorkspace.Attempt> combo = findCombo(bar);

        assertThat(startButton.getText()).isEqualTo("Start Attempt");
        assertThat(AttemptBar.attemptLabel(first, 0, "NOT_RUN")).isEqualTo("#1 · Not checked yet (abcdef)");
        assertThat(AttemptBar.attemptLabel(second, 1, "PASSED")).isEqualTo("#2 · Passed (123456)");

        combo.addItem(first);
        combo.addItem(second);
        combo.setSelectedItem(second);
        assertThat(startButton.getText()).isEqualTo("Open Solution");

        var list = new JList<>(new ManagedPracticeWorkspace.Attempt[]{first, second});
        JLabel popupLabel = (JLabel) combo.getRenderer()
                .getListCellRendererComponent(list, second, 1, false, false);
        JLabel closedLabel = (JLabel) combo.getRenderer()
                .getListCellRendererComponent(list, second, -1, false, false);
        assertThat(popupLabel.getText()).isEqualTo("#2 · Passed (123456)");
        assertThat(closedLabel.getText()).isEqualTo("#2 · Passed (123456)");
        assertThat(closedLabel.getToolTipText()).isEqualTo(second.id());

        bar.clear();
        assertThat(startButton.getText()).isEqualTo("Start Attempt");
    }

    @Test
    void statusLookupReadsPersistedStatusWithoutMutatingOrThrowingOnMismatchedEntries() {
        var progress = new com.jinloes.practice_plugin.state.ManagedPracticeProgress();
        var unchecked = new ManagedPracticeWorkspace.Attempt(
                "pair-sum-00000000000000000000000000aaaa11", "pair-sum", "1",
                Path.of("attempts/pair-sum/unchecked/Solution.java"));
        var passed = new ManagedPracticeWorkspace.Attempt(
                "pair-sum-00000000000000000000000000bbbb22", "pair-sum", "1",
                Path.of("attempts/pair-sum/passed/Solution.java"));
        var blankExercise = new ManagedPracticeWorkspace.Attempt(
                "pair-sum-00000000000000000000000000cccc33", "pair-sum", "1",
                Path.of("attempts/pair-sum/blank/Solution.java"));
        var wrongExercise = new ManagedPracticeWorkspace.Attempt(
                "pair-sum-00000000000000000000000000dddd44", "pair-sum", "1",
                Path.of("attempts/pair-sum/wrong/Solution.java"));
        var nullStatus = new ManagedPracticeWorkspace.Attempt(
                "pair-sum-00000000000000000000000000eeee55", "pair-sum", "1",
                Path.of("attempts/pair-sum/null-status/Solution.java"));

        var passedEntry = new com.jinloes.practice_plugin.state.ManagedPracticeProgress.Entry();
        passedEntry.exerciseId = "pair-sum";
        passedEntry.status = "PASSED";
        progress.getState().attempts.put(passed.id(), passedEntry);

        var blankEntry = new com.jinloes.practice_plugin.state.ManagedPracticeProgress.Entry();
        blankEntry.exerciseId = "";
        blankEntry.status = "PASSED";
        progress.getState().attempts.put(blankExercise.id(), blankEntry);

        var wrongEntry = new com.jinloes.practice_plugin.state.ManagedPracticeProgress.Entry();
        wrongEntry.exerciseId = "binary-search";
        wrongEntry.status = "PASSED";
        progress.getState().attempts.put(wrongExercise.id(), wrongEntry);

        var nullStatusEntry = new com.jinloes.practice_plugin.state.ManagedPracticeProgress.Entry();
        nullStatusEntry.exerciseId = "pair-sum";
        nullStatusEntry.status = null;
        progress.getState().attempts.put(nullStatus.id(), nullStatusEntry);

        assertThat(AttemptBar.statusOf(null, unchecked)).isEqualTo("NOT_RUN");
        assertThat(AttemptBar.statusOf(progress, null)).isEqualTo("NOT_RUN");
        assertThat(AttemptBar.statusOf(progress, unchecked)).isEqualTo("NOT_RUN");
        assertThat(progress.getState().attempts)
                .as("reading status for an unchecked attempt must not mutate persisted attempts")
                .doesNotContainKey(unchecked.id());
        assertThat(AttemptBar.statusOf(progress, passed)).isEqualTo("PASSED");
        assertThat(AttemptBar.statusOf(progress, blankExercise)).isEqualTo("NOT_RUN");
        assertThat(AttemptBar.statusOf(progress, wrongExercise)).isEqualTo("NOT_RUN");
        assertThat(AttemptBar.statusOf(progress, nullStatus)).isEqualTo("NOT_RUN");

        AttemptBar bar = new AttemptBar(attempt -> AttemptBar.statusOf(progress, attempt), () -> {}, () -> {}, () -> {});
        JComboBox<ManagedPracticeWorkspace.Attempt> combo = findCombo(bar);
        var items = new ManagedPracticeWorkspace.Attempt[]{unchecked, passed, blankExercise, wrongExercise, nullStatus};
        for (var item : items) {
            combo.addItem(item);
        }
        var list = new JList<>(items);
        var renderer = combo.getRenderer();
        assertThat(((JLabel) renderer.getListCellRendererComponent(list, unchecked, 0, false, false)).getText())
                .isEqualTo("#1 · Not checked yet (aaaa11)");
        assertThat(((JLabel) renderer.getListCellRendererComponent(list, passed, 1, false, false)).getText())
                .isEqualTo("#2 · Passed (bbbb22)");
        assertThat(((JLabel) renderer.getListCellRendererComponent(list, blankExercise, 2, false, false)).getText())
                .isEqualTo("#3 · Not checked yet (cccc33)");
        assertThat(((JLabel) renderer.getListCellRendererComponent(list, wrongExercise, 3, false, false)).getText())
                .isEqualTo("#4 · Not checked yet (dddd44)");
        assertThat(((JLabel) renderer.getListCellRendererComponent(list, nullStatus, 4, false, false)).getText())
                .isEqualTo("#5 · Not checked yet (eeee55)");
        assertThat(progress.getState().attempts).doesNotContainKey(unchecked.id());
    }

    private static JButton findButton(AttemptBar bar, String text) {
        for (var child : bar.component().getComponents()) {
            if (child instanceof javax.swing.JPanel nested) {
                for (var button : nested.getComponents()) {
                    if (button instanceof JButton candidate && text.equals(candidate.getText())) {
                        return candidate;
                    }
                }
            }
        }
        throw new AssertionError("Missing button: " + text);
    }

    @SuppressWarnings("unchecked")
    private static JComboBox<ManagedPracticeWorkspace.Attempt> findCombo(AttemptBar bar) {
        for (var child : bar.component().getComponents()) {
            if (child instanceof JComboBox<?> combo) {
                return (JComboBox<ManagedPracticeWorkspace.Attempt>) combo;
            }
        }
        throw new AssertionError("Missing attempt ComboBox");
    }
}
