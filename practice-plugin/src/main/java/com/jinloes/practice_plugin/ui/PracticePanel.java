package com.jinloes.practice_plugin.ui;

import com.intellij.execution.ExecutionException;
import com.intellij.notification.NotificationGroupManager;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.editor.event.DocumentEvent;
import com.intellij.openapi.editor.event.DocumentListener;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.ui.components.JBList;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.components.JBTextArea;
import com.intellij.util.ui.JBUI;
import com.jinloes.practice_plugin.app.LegacyImportService;
import com.jinloes.practice_plugin.catalog.ExerciseCatalog;
import com.jinloes.practice_plugin.catalog.ExerciseCatalog.Exercise;
import com.jinloes.practice_plugin.run.PracticeRunner;
import com.jinloes.practice_plugin.state.ManagedPracticeProgress;
import com.jinloes.practice_plugin.state.PracticeProgress;
import com.jinloes.practice_plugin.workspace.ManagedPracticeWorkspace;
import com.jinloes.practice_plugin.workspace.PracticeModuleWorkspace;
import com.jinloes.practice_plugin.workspace.PracticeWorkspace;
import org.jetbrains.annotations.NotNull;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

final class PracticePanel extends JPanel implements Disposable {
    private final Project project;
    private final ManagedPracticeProgress progress;
    private final ManagedPracticeWorkspace workspace;
    private final LegacyImportService legacyImport;
    private final PracticeRunner runner;
    private final DefaultListModel<Exercise> model = new DefaultListModel<>();
    private final JBList<Exercise> exercises = new JBList<>(model);
    private final ExerciseFilters filters = new ExerciseFilters();
    private final JComboBox<ManagedPracticeWorkspace.Attempt> attempts = new JComboBox<>();
    private final JEditorPane statement = PracticeViews.htmlPane();
    private final JBTextArea hints = PracticeViews.textArea();
    private final JBTextArea results = PracticeViews.textArea();
    private final JLabel status = new JLabel("Choose an exercise.");
    private final JButton runExamples = new JButton("Run Examples");
    private final JButton runWithInput = new JButton("Run With Input...");
    private final JButton debugWithInput = new JButton("Debug With Input...");
    private final JButton check = new JButton("Check Solution");
    private final JButton stop = new JButton("Stop");
    private final Map<String, String> lastInput = new HashMap<>();
    private boolean updatingAttempts;
    private boolean disposed;
    private int selectionGeneration;

    /**
     * Held as a field so {@link #dispose()} can remove exactly this registration. The runner
     * outlives the panel, so a lambda created inline could never be removed.
     */
    private final Consumer<String> runOutput = this::showRunOutput;

    PracticePanel(Project project) {
        super(new BorderLayout(6, 6));
        this.project = project;
        progress = ManagedPracticeProgress.get();
        workspace = ManagedPracticeWorkspace.get();
        legacyImport = new LegacyImportService(workspace);
        runner = project.getService(PracticeRunner.class);
        setBorder(JBUI.Borders.empty(8));

        JPanel header = new JPanel(new GridLayout(0, 1, 4, 4));
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT));
        button(actions, "Limits", () -> LimitsDialog.edit(project, progress));
        button(actions, "Clean Generated Artifacts", this::cleanGeneratedArtifacts);
        if (isLegacyWorkspace()) {
            button(actions, "Import Legacy Attempts", this::importLegacyAttempts);
        }
        header.add(actions);
        filters.addTo(header);
        add(header, BorderLayout.NORTH);

        exercises.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        exercises.setCellRenderer(PracticeViews.labelled(Exercise.class,
                exercise -> exercise.title() + "  [" + exercise.difficulty() + "]"));
        exercises.getAccessibleContext().setAccessibleName("Exercises");
        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Problem", new JBScrollPane(statement));
        JPanel hintPanel = new JPanel(new BorderLayout());
        hintPanel.add(new JBScrollPane(hints), BorderLayout.CENTER);
        button(hintPanel, "Reveal Next Hint", this::revealHint);
        tabs.addTab("Hints", hintPanel);
        tabs.addTab("Results", new JBScrollPane(results));
        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, new JBScrollPane(exercises), tabs);
        split.setResizeWeight(0.3);
        add(split, BorderLayout.CENTER);

        attempts.getAccessibleContext().setAccessibleName("Saved attempts");
        attempts.setRenderer(PracticeViews.labelled(
                ManagedPracticeWorkspace.Attempt.class, ManagedPracticeWorkspace.Attempt::id));
        JPanel footer = new JPanel(new GridLayout(0, 1, 4, 4));
        footer.add(attempts);
        JPanel editing = new JPanel(new FlowLayout(FlowLayout.LEFT));
        button(editing, "Start / Resume", this::startOrResume);
        button(editing, "New Attempt", this::newAttempt);
        footer.add(editing);
        JPanel executions = new JPanel(new FlowLayout(FlowLayout.LEFT));
        executions.add(runExamples);
        executions.add(runWithInput);
        executions.add(debugWithInput);
        executions.add(check);
        executions.add(stop);
        footer.add(executions);
        footer.add(status);
        add(footer, BorderLayout.SOUTH);

        runExamples.addActionListener(event -> guarded(() -> runner.runExamples(selectedAttempt().id())));
        runWithInput.addActionListener(event -> promptForInput(false));
        debugWithInput.addActionListener(event -> promptForInput(true));
        check.addActionListener(event -> guarded(() -> runner.check(selectedAttempt().id())));
        stop.addActionListener(event -> runner.stop());
        runner.addListener(runOutput);
        exercises.addListSelectionListener(event -> {
            if (!event.getValueIsAdjusting()) {
                showExercise();
            }
        });
        attempts.addActionListener(event -> {
            if (!updatingAttempts) {
                showAttempt();
            }
        });
        filters.onChange(this::filter);
        EditorFactory.getInstance().getEventMulticaster().addDocumentListener(new DocumentListener() {
            @Override
            public void documentChanged(@NotNull DocumentEvent event) {
                var attempt = selectedAttemptOrNull();
                var file = FileDocumentManager.getInstance().getFile(event.getDocument());
                if (attempt != null && file != null
                        && Path.of(file.getPath()).toAbsolutePath().normalize().equals(attempt.solution())) {
                    status.setText("Edited since displayed result; check again.");
                }
            }
        }, this);
        filter();
    }

    private void filter() {
        Exercise previous = exercises.getSelectedValue();
        model.clear();
        for (Exercise exercise : ExerciseCatalog.all()) {
            boolean passed = progress.getState().attempts.values().stream()
                    .anyMatch(entry -> entry.exerciseId.equals(exercise.id()) && !entry.lastPassedAt.isEmpty());
            if (filters.matches(exercise, passed)) {
                model.addElement(exercise);
            }
        }
        if (previous != null && model.contains(previous)) {
            exercises.setSelectedValue(previous, true);
        } else if (!model.isEmpty()) {
            exercises.setSelectedIndex(0);
        }
    }

    private void showExercise() {
        int generation = ++selectionGeneration;
        Exercise exercise = exercises.getSelectedValue();
        updatingAttempts = true;
        attempts.removeAllItems();
        updatingAttempts = false;
        showStatement(exercise == null
                ? "<p>No exercises match these filters.</p>"
                : PracticeViews.problemText(exercise));
        hints.setText("Start or resume an attempt to reveal hints.");
        results.setText("");
        updateButtons();
        if (exercise != null) {
            background("Loading practice attempts", () -> workspace.attempts(exercise.id()), loaded -> {
                if (generation == selectionGeneration && exercise.equals(exercises.getSelectedValue())) {
                    updatingAttempts = true;
                    loaded.forEach(attempts::addItem);
                    String selected = progress.getState().selectedAttempts.get(exercise.id());
                    loaded.stream().filter(attempt -> attempt.id().equals(selected)).findFirst()
                            .ifPresent(attempts::setSelectedItem);
                    updatingAttempts = false;
                    showAttempt();
                }
            });
        }
    }

    private void showAttempt() {
        var attempt = selectedAttemptOrNull();
        Exercise exercise = exercises.getSelectedValue();
        updateButtons();
        if (attempt == null || exercise == null) {
            status.setText("Choose Start / Resume to create your first managed attempt.");
            return;
        }
        progress.getState().selectedAttempts.put(exercise.id(), attempt.id());
        var entry = progress.entry(attempt.id(), exercise.id());
        hints.setText(String.join("\n\n", exercise.hints().subList(
                0, Math.min(entry.hintsRevealed, exercise.hints().size()))));
        if (hints.getText().isEmpty()) {
            hints.setText("Hints are optional. Reveal one only when you want help.");
        }
        status.setText("Managed attempt ready. " + entry.status);
        results.setText(entry.status + "\n" + entry.details + "\nLast full check: " + entry.checkedAt
                + "\nMost recent pass: " + entry.lastPassedAt
                + (entry.complexity == null || entry.complexity.isBlank()
                        ? "" : "\n\n" + entry.complexity));
        if (!entry.checkedFingerprint.isEmpty()) {
            background("Checking result freshness", () -> workspace.fingerprint(attempt), hash -> {
                if (attempt.equals(selectedAttemptOrNull())
                        && (!hash.equals(entry.checkedFingerprint) || hasUnsavedAttemptFile())) {
                    status.setText("Edited since last check; previous result is stale.");
                    results.append("\nSTALE: check the current solution again.");
                }
            });
        }
    }

    private void startOrResume() throws IOException {
        var selected = selectedAttemptOrNull();
        if (selected == null) {
            newAttempt();
        } else {
            openSolution(selected);
        }
    }

    private void newAttempt() throws IOException {
        if (runner.isRunning()) {
            throw new IOException("Stop the active run before creating another attempt.");
        }
        Exercise exercise = exercises.getSelectedValue();
        if (exercise == null) {
            throw new IOException("Select an exercise first.");
        }
        background("Creating managed attempt", () -> workspace.create(exercise), attempt -> {
            progress.getState().selectedAttempts.put(exercise.id(), attempt.id());
            if (exercise.equals(exercises.getSelectedValue())) {
                attempts.addItem(attempt);
                attempts.setSelectedItem(attempt);
            }
            openSolution(attempt);
        });
    }

    private void importLegacyAttempts() throws IOException {
        if (!isLegacyWorkspace()) {
            throw new IOException("The current project is not a marked legacy practice project.");
        }
        Path legacyRoot = root().toRealPath();
        background("Importing legacy attempts", () -> legacyImport.importLegacy(
                legacyRoot, PracticeProgress.get(project), progress), imported -> {
            results.setText(imported.summary());
            showExercise();
        });
    }

    private void openSolution(ManagedPracticeWorkspace.Attempt attempt) {
        background("Opening solution", () -> {
            try {
                PracticeModuleWorkspace.get(project).open(attempt);
            } catch (ExecutionException exception) {
                throw new IOException(exception.getMessage(), exception);
            }
            var file = LocalFileSystem.getInstance().refreshAndFindFileByNioFile(attempt.solution());
            if (file == null) {
                throw new IOException("Solution file is missing: " + attempt.solution());
            }
            return file;
        }, file -> FileEditorManager.getInstance(project).openFile(file, true));
    }

    private void promptForInput(boolean debug) {
        ManagedPracticeWorkspace.Attempt attempt = selectedAttemptOrNull();
        if (attempt == null) {
            error("Start or resume an attempt first.");
            return;
        }
        Exercise exercise = ExerciseCatalog.find(attempt.exerciseId());
        String previous = lastInput.getOrDefault(exercise.id(), exercise.sampleInput());
        String input = Messages.showInputDialog(
                project,
                exercise.inputSyntax(),
                (debug ? "Debug " : "Run ") + exercise.title() + " With Input",
                null,
                previous,
                null);
        if (input == null || input.isBlank()) {
            return;
        }
        String trimmed = input.trim();
        lastInput.put(exercise.id(), trimmed);
        guarded(() -> {
            if (debug) {
                runner.debugExamples(attempt.id(), trimmed);
            } else {
                runner.runExamples(attempt.id(), trimmed);
            }
        });
    }

    private void revealHint() throws IOException {
        var attempt = selectedAttempt();
        Exercise exercise = Objects.requireNonNull(exercises.getSelectedValue());
        var entry = progress.entry(attempt.id(), exercise.id());
        entry.hintsRevealed = Math.min(entry.hintsRevealed + 1, exercise.hints().size());
        showAttempt();
    }

    private void cleanGeneratedArtifacts() {
        if (runner.isRunning()) {
            error("Stop the active practice run before cleaning generated artifacts.");
            return;
        }
        PracticeModuleWorkspace.get(project).cleanupGeneratedArtifacts();
        status.setText("Removed inactive generated practice artifacts.");
    }

    private void updateButtons() {
        boolean ready = selectedAttemptOrNull() != null && !runner.isRunning();
        runExamples.setEnabled(ready);
        runWithInput.setEnabled(ready);
        debugWithInput.setEnabled(ready);
        check.setEnabled(ready);
        stop.setEnabled(runner.isRunning());
    }

    private boolean hasUnsavedAttemptFile() {
        var attempt = selectedAttemptOrNull();
        if (attempt == null) {
            return false;
        }
        for (var document : FileDocumentManager.getInstance().getUnsavedDocuments()) {
            var file = FileDocumentManager.getInstance().getFile(document);
            if (file != null && Path.of(file.getPath()).toAbsolutePath().normalize().equals(attempt.solution())) {
                return true;
            }
        }
        return false;
    }

    private ManagedPracticeWorkspace.Attempt selectedAttempt() throws IOException {
        var attempt = selectedAttemptOrNull();
        if (attempt == null) {
            throw new IOException("Start or resume an attempt first.");
        }
        return attempt;
    }

    private ManagedPracticeWorkspace.Attempt selectedAttemptOrNull() {
        return (ManagedPracticeWorkspace.Attempt) attempts.getSelectedItem();
    }

    private boolean isLegacyWorkspace() {
        return project.getBasePath() != null && PracticeWorkspace.isWorkspace(root());
    }

    private Path root() {
        return Path.of(Objects.requireNonNull(project.getBasePath()));
    }

    private void showStatement(String html) {
        statement.setText("<html><body>" + html + "</body></html>");
        statement.setCaretPosition(0);
    }

    private void button(JPanel panel, String title, Action action) {
        JButton button = new JButton(title);
        button.addActionListener(event -> guarded(action));
        if (panel.getLayout() instanceof BorderLayout) {
            panel.add(button, BorderLayout.SOUTH);
        } else {
            panel.add(button);
        }
    }

    private void guarded(Action action) {
        try {
            action.run();
        } catch (IOException | ExecutionException | IllegalArgumentException exception) {
            error(exception.getMessage());
        }
    }

    private <T> void background(String title, IoTask<T> operation, Consumer<T> completed) {
        new Task.Backgroundable(project, title, false) {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                try {
                    T result = operation.run();
                    ApplicationManager.getApplication().invokeLater(() -> {
                        if (!disposed && !project.isDisposed()) {
                            completed.accept(result);
                        }
                    });
                } catch (IOException | IllegalArgumentException exception) {
                    ApplicationManager.getApplication().invokeLater(() -> {
                        if (!disposed && !project.isDisposed()) {
                            error(exception.getMessage());
                        }
                    });
                }
            }
        }.queue();
    }

    private void error(String text) {
        status.setText("Action failed; see Results.");
        results.setText(text);
        NotificationGroupManager.getInstance().getNotificationGroup("Algorithm Practice")
                .createNotification(text, NotificationType.ERROR).notify(project);
    }

    private void showRunOutput(String text) {
        if (disposed) {
            return;
        }
        results.setText(text);
        if (hasUnsavedAttemptFile()) {
            results.append("\nSTALE: there are unsaved changes. Check again after saving.");
        }
        results.setCaretPosition(0);
        status.setText(runner.isRunning() ? "Running..." : "Run finished; see Results.");
        updateButtons();
    }

    @Override
    public void dispose() {
        disposed = true;
        runner.removeListener(runOutput);
    }

    @FunctionalInterface
    private interface Action {
        void run() throws IOException, ExecutionException;
    }

    @FunctionalInterface
    private interface IoTask<T> {
        T run() throws IOException;
    }
}
