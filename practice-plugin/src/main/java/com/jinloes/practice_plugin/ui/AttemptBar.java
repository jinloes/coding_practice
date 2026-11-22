package com.jinloes.practice_plugin.ui;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.ui.components.JBLabel;
import com.jinloes.practice_plugin.catalog.ExerciseCatalog.Exercise;
import com.jinloes.practice_plugin.state.ManagedPracticeProgress;
import com.jinloes.practice_plugin.workspace.ManagedPracticeWorkspace;
import com.jinloes.practice_plugin.workspace.ManagedPracticeWorkspace.Attempt;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntSupplier;
import java.util.function.Supplier;
import java.util.stream.IntStream;

/**
 * The durable-attempt selector row at the bottom of the Practice tool window: the saved-attempts
 * dropdown plus the Start Attempt / Open Solution and New Attempt buttons and their background
 * creation/opening coordination.
 */
final class AttemptBar {
    static final String START_TEXT = "Start Attempt";
    static final String OPEN_TEXT = "Open Solution";

    @FunctionalInterface
    interface TaskRunner {
        <T> void run(String title, BackgroundTasks.IoTask<T> operation, Consumer<T> completed);
    }

    interface AttemptStore {
        List<Attempt> attempts(String exerciseId) throws IOException;

        Attempt create(Exercise exercise) throws IOException;

        static AttemptStore of(ManagedPracticeWorkspace workspace) {
            return new AttemptStore() {
                @Override public List<Attempt> attempts(String exerciseId) throws IOException {
                    return workspace.attempts(exerciseId);
                }

                @Override public Attempt create(Exercise exercise) throws IOException {
                    return workspace.create(exercise);
                }
            };
        }
    }

    private final ComboBox<Attempt> attempts = new ComboBox<>();
    private final JPanel panel = new JPanel(new BorderLayout(4, 0));
    private final Function<Attempt, String> statusLookup;
    private final Runnable onSelect;
    private final JButton startButton;
    private final JButton newAttemptButton;
    private boolean updating;
    private boolean busy;
    private int stateVersion;

    AttemptBar(Runnable onSelect, Runnable onStartOrResume, Runnable onNewAttempt) {
        this(defaultStatusLookup(), onSelect, onStartOrResume, onNewAttempt);
    }

    AttemptBar(
            Function<Attempt, String> statusLookup,
            Runnable onSelect,
            Runnable onStartOrResume,
            Runnable onNewAttempt
    ) {
        this.statusLookup = Objects.requireNonNull(statusLookup, "statusLookup");
        this.onSelect = onSelect;
        attempts.getAccessibleContext().setAccessibleName("Saved attempts");
        attempts.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(
                    JList<?> list, Object value, int index, boolean selected, boolean focus
            ) {
                super.getListCellRendererComponent(list, value, index, selected, focus);
                if (value instanceof Attempt attempt) {
                    setText(formatAttempt(attempt, index));
                    setToolTipText(attempt.id());
                } else {
                    setToolTipText(null);
                }
                return this;
            }
        });
        startButton = createButton(START_TEXT, onStartOrResume);
        newAttemptButton = createButton("New Attempt", onNewAttempt);
        attempts.addActionListener(_ -> {
            syncStartButtonText();
            if (!updating) {
                onSelect.run();
            }
        });

        panel.add(new JBLabel("Attempt:"), BorderLayout.WEST);
        panel.add(attempts, BorderLayout.CENTER);
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        buttons.add(startButton);
        buttons.add(newAttemptButton);
        panel.add(buttons, BorderLayout.EAST);
    }

    private static Function<Attempt, String> defaultStatusLookup() {
        return attempt -> ApplicationManager.getApplication() == null
                ? "NOT_RUN"
                : statusOf(ManagedPracticeProgress.get(), attempt);
    }

    static String statusOf(ManagedPracticeProgress progress, Attempt attempt) {
        if (progress == null || attempt == null) {
            return "NOT_RUN";
        }
        var entry = progress.getState().attempts.get(attempt.id());
        return entry != null && attempt.exerciseId().equals(entry.exerciseId) && entry.status != null
                ? entry.status
                : "NOT_RUN";
    }

    JComponent component() {
        return panel;
    }

    JButton startButton() {
        return startButton;
    }

    JButton newAttemptButton() {
        return newAttemptButton;
    }

    ComboBox<Attempt> comboBox() {
        return attempts;
    }

    boolean isBusy() {
        return busy;
    }

    Attempt selected() {
        return (Attempt) attempts.getSelectedItem();
    }

    Attempt selectedOrThrow() throws IOException {
        Attempt current = selected();
        if (current == null) {
            throw new IOException("Start or resume an attempt first.");
        }
        return current;
    }

    boolean matches(String attemptId) {
        Attempt current = selected();
        return current != null && current.id().equals(attemptId);
    }

    void resetBusy() {
        setBusy(false);
    }

    private void setBusy(boolean next) {
        busy = next;
        startButton.setEnabled(!next);
        newAttemptButton.setEnabled(!next);
    }

    private void syncStartButtonText() {
        startButton.setText(selected() != null ? OPEN_TEXT : START_TEXT);
    }

    String formatAttempt(Attempt attempt, int index) {
        int position = index >= 0 ? index : indexOf(attempt);
        return attemptLabel(attempt, position, statusLookup.apply(attempt));
    }

    static String attemptLabel(Attempt attempt, int index, String status) {
        int number = index >= 0 ? index + 1 : 1;
        String id = attempt.id();
        String shortId = id.length() <= 6 ? id : id.substring(id.length() - 6);
        return "#" + number + " \u00b7 " + ResultsView.label(status) + " (" + shortId + ")";
    }

    private int indexOf(Attempt target) {
        for (int index = 0; index < attempts.getItemCount(); index++) {
            Attempt candidate = attempts.getItemAt(index);
            if (candidate != null && candidate.id().equals(target.id())) {
                return index;
            }
        }
        return -1;
    }

    List<Attempt> items() {
        return IntStream.range(0, attempts.getItemCount()).mapToObj(attempts::getItemAt).toList();
    }

    void clear() {
        stateVersion++;
        updating = true;
        try {
            attempts.removeAllItems();
        } finally {
            updating = false;
            syncStartButtonText();
        }
    }

    void loadFor(
            Exercise exercise,
            int generation,
            IntSupplier currentGeneration,
            Supplier<Exercise> currentExercise,
            ManagedPracticeWorkspace workspace,
            ManagedPracticeProgress progress,
            BackgroundTasks tasks
    ) {
        loadFor(exercise, generation, currentGeneration, currentExercise,
                AttemptStore.of(workspace), progress, tasks::run);
    }

    void loadFor(
            Exercise exercise,
            int generation,
            IntSupplier currentGeneration,
            Supplier<Exercise> currentExercise,
            AttemptStore store,
            ManagedPracticeProgress progress,
            TaskRunner runner
    ) {
        clear();
        int loadVersion = stateVersion;
        if (exercise == null) {
            return;
        }
        runner.run("Loading practice attempts", () -> store.attempts(exercise.id()), loaded -> {
            if (loadVersion == stateVersion
                    && generation == currentGeneration.getAsInt()
                    && exercise.equals(currentExercise.get())) {
                setAttempts(loaded, progress.getState().selectedAttempts.get(exercise.id()));
                onSelect.run();
            }
        });
    }

    void setAttempts(List<Attempt> loaded, String selectedId) {
        stateVersion++;
        Map<String, Attempt> unique = new LinkedHashMap<>();
        for (Attempt attempt : loaded) {
            unique.putIfAbsent(attempt.id(), attempt);
        }
        updating = true;
        try {
            attempts.removeAllItems();
            unique.values().forEach(attempts::addItem);
            if (selectedId != null && unique.containsKey(selectedId)) {
                attempts.setSelectedItem(unique.get(selectedId));
            }
        } finally {
            updating = false;
            syncStartButtonText();
        }
    }

    void startOrResume(
            Project project,
            ManagedPracticeWorkspace workspace,
            ManagedPracticeProgress progress,
            BackgroundTasks tasks,
            BooleanSupplier running,
            Supplier<Exercise> currentExercise
    ) throws IOException {
        startOrResume(AttemptStore.of(workspace), progress, tasks::run, running, currentExercise,
                attempt -> openSolution(project, tasks, attempt));
    }

    void startOrResume(
            AttemptStore store,
            ManagedPracticeProgress progress,
            TaskRunner runner,
            BooleanSupplier running,
            Supplier<Exercise> currentExercise,
            Consumer<Attempt> opener
    ) throws IOException {
        if (busy) {
            return;
        }
        Attempt current = selected();
        if (current != null) {
            opener.accept(current);
            return;
        }
        Exercise exercise = requireExercise(currentExercise.get());
        setBusy(true);
        runner.run("Starting practice attempt", () -> resolveOrCreate(store, running, exercise), existing -> {
            setBusy(false);
            String preferred = progress.getState().selectedAttempts.get(exercise.id());
            Attempt chosen = existing.stream()
                    .filter(candidate -> candidate.id().equals(preferred))
                    .findFirst()
                    .orElseGet(existing::getFirst);
            progress.getState().selectedAttempts.put(exercise.id(), chosen.id());
            if (exercise.equals(currentExercise.get())) {
                setAttempts(existing, chosen.id());
                onSelect.run();
            }
            opener.accept(chosen);
        });
    }

    void newAttempt(
            Project project,
            ManagedPracticeWorkspace workspace,
            ManagedPracticeProgress progress,
            BackgroundTasks tasks,
            BooleanSupplier running,
            Supplier<Exercise> currentExercise
    ) throws IOException {
        newAttempt(AttemptStore.of(workspace), progress, tasks::run, running, currentExercise,
                attempt -> openSolution(project, tasks, attempt));
    }

    void newAttempt(
            AttemptStore store,
            ManagedPracticeProgress progress,
            TaskRunner runner,
            BooleanSupplier running,
            Supplier<Exercise> currentExercise,
            Consumer<Attempt> opener
    ) throws IOException {
        if (busy) {
            return;
        }
        if (running.getAsBoolean()) {
            throw new IOException("Stop the active run before creating another attempt.");
        }
        Exercise exercise = requireExercise(currentExercise.get());
        setBusy(true);
        runner.run("Creating managed attempt", () -> {
            Attempt created = store.create(exercise);
            return new Resolved(store.attempts(exercise.id()), created);
        }, resolved -> {
            setBusy(false);
            progress.getState().selectedAttempts.put(exercise.id(), resolved.selected().id());
            if (exercise.equals(currentExercise.get())) {
                List<Attempt> all = new ArrayList<>(resolved.all());
                if (all.stream().noneMatch(candidate -> candidate.id().equals(resolved.selected().id()))) {
                    all.add(resolved.selected());
                }
                setAttempts(all, resolved.selected().id());
                onSelect.run();
            }
            opener.accept(resolved.selected());
        });
    }

    private void openSolution(Project project, BackgroundTasks tasks, Attempt attempt) {
        setBusy(true);
        tasks.run("Opening solution", () -> AttemptFiles.prepare(project, attempt), file -> {
            setBusy(false);
            FileEditorManager.getInstance(project).openFile(file, true);
        });
    }

    private static List<Attempt> resolveOrCreate(
            AttemptStore store,
            BooleanSupplier running,
            Exercise exercise
    ) throws IOException {
        List<Attempt> existing = store.attempts(exercise.id());
        if (!existing.isEmpty()) {
            return existing;
        }
        if (running.getAsBoolean()) {
            throw new IOException("Stop the active run before creating another attempt.");
        }
        return List.of(store.create(exercise));
    }

    private static Exercise requireExercise(Exercise exercise) throws IOException {
        if (exercise == null) {
            throw new IOException("Select an exercise first.");
        }
        return exercise;
    }

    private static JButton createButton(String title, Runnable action) {
        JButton button = new JButton(title);
        button.addActionListener(_ -> action.run());
        return button;
    }

    private record Resolved(List<Attempt> all, Attempt selected) {}
}
