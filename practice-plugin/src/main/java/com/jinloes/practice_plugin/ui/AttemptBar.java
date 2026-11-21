package com.jinloes.practice_plugin.ui;

import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.ui.components.JBLabel;
import com.jinloes.practice_plugin.catalog.ExerciseCatalog.Exercise;
import com.jinloes.practice_plugin.state.ManagedPracticeProgress;
import com.jinloes.practice_plugin.workspace.ManagedPracticeWorkspace;
import com.jinloes.practice_plugin.workspace.ManagedPracticeWorkspace.Attempt;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.IntSupplier;
import java.util.function.Supplier;
import java.util.stream.IntStream;

/**
 * The durable-attempt selector row at the bottom of the Practice tool window: the saved-attempts
 * dropdown plus the Start / Resume and New Attempt buttons and their background creation/opening
 * coordination.
 */
final class AttemptBar {
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
    private final Runnable onSelect;
    private boolean updating;
    private boolean busy;
    private int stateVersion;

    AttemptBar(Runnable onSelect, Runnable onStartOrResume, Runnable onNewAttempt) {
        this.onSelect = onSelect;
        attempts.getAccessibleContext().setAccessibleName("Saved attempts");
        attempts.setRenderer(PracticeViews.labelled(Attempt.class, Attempt::id));
        attempts.addActionListener(ignored -> {
            if (!updating) {
                onSelect.run();
            }
        });

        panel.add(new JBLabel("Attempt:"), BorderLayout.WEST);
        panel.add(attempts, BorderLayout.CENTER);
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        addButton(buttons, "Start / Resume", onStartOrResume);
        addButton(buttons, "New Attempt", onNewAttempt);
        panel.add(buttons, BorderLayout.EAST);
    }

    JComponent component() {
        return panel;
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
        busy = false;
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
        }
    }

    void addAndSelect(Attempt attempt) {
        stateVersion++;
        updating = true;
        try {
            Attempt target = attempt;
            boolean present = false;
            for (int index = 0; index < attempts.getItemCount(); index++) {
                Attempt candidate = attempts.getItemAt(index);
                if (candidate != null && candidate.id().equals(attempt.id())) {
                    target = candidate;
                    present = true;
                    break;
                }
            }
            if (!present) {
                attempts.addItem(target);
            }
            attempts.setSelectedItem(target);
        } finally {
            updating = false;
        }
        onSelect.run();
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
        busy = true;
        runner.run("Starting practice attempt", () -> resolveOrCreate(store, running, exercise), existing -> {
            busy = false;
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
        busy = true;
        runner.run("Creating managed attempt", () -> {
            Attempt created = store.create(exercise);
            return new Resolved(store.attempts(exercise.id()), created);
        }, resolved -> {
            busy = false;
            progress.getState().selectedAttempts.put(exercise.id(), resolved.selected().id());
            if (exercise.equals(currentExercise.get())) {
                setAttempts(resolved.all(), resolved.selected().id());
                addAndSelect(resolved.selected());
            }
            opener.accept(resolved.selected());
        });
    }

    private void openSolution(Project project, BackgroundTasks tasks, Attempt attempt) {
        busy = true;
        tasks.run("Opening solution", () -> AttemptFiles.prepare(project, attempt), file -> {
            busy = false;
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

    private static void addButton(JPanel container, String title, Runnable action) {
        JButton button = new JButton(title);
        button.addActionListener(ignored -> action.run());
        container.add(button);
    }

    private record Resolved(List<Attempt> all, Attempt selected) {}
}
