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
import com.intellij.ui.DocumentAdapter;
import com.intellij.ui.components.JBList;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.components.JBTextArea;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.JBUI;
import com.jinloes.practice_plugin.catalog.ExerciseCatalog;
import com.jinloes.practice_plugin.catalog.ExerciseCatalog.Exercise;
import com.jinloes.practice_plugin.run.PracticeRunner;
import com.jinloes.practice_plugin.state.ManagedPracticeProgress;
import com.jinloes.practice_plugin.state.PracticeProgress;
import com.jinloes.practice_plugin.workspace.ManagedPracticeWorkspace;
import com.jinloes.practice_plugin.workspace.PracticeModuleWorkspace;
import com.jinloes.practice_plugin.workspace.PracticeWorkspace;
import org.jetbrains.annotations.NotNull;

import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.SpinnerNumberModel;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

final class PracticePanel extends JPanel implements Disposable {
    private final Project project;
    private final ManagedPracticeProgress progress;
    private final ManagedPracticeWorkspace workspace;
    private final PracticeRunner runner;
    private final DefaultListModel<Exercise> model = new DefaultListModel<>();
    private final JBList<Exercise> exercises = new JBList<>(model);
    private final JComboBox<String> topic = new JComboBox<>();
    private final JComboBox<String> difficulty = new JComboBox<>();
    private final JComboBox<String> progressFilter =
            new JComboBox<>(new String[]{"All progress", "Passed before", "Not passed"});
    private final JBTextField search = new JBTextField();
    private final JComboBox<ManagedPracticeWorkspace.Attempt> attempts = new JComboBox<>();
    private final JBTextArea statement = textArea();
    private final JBTextArea hints = textArea();
    private final JBTextArea results = textArea();
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

    PracticePanel(Project project) {
        super(new BorderLayout(6, 6));
        this.project = project;
        progress = ManagedPracticeProgress.get();
        workspace = new ManagedPracticeWorkspace();
        runner = project.getService(PracticeRunner.class);
        setBorder(JBUI.Borders.empty(8));

        JPanel header = new JPanel(new GridLayout(0, 1, 4, 4));
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT));
        button(actions, "Limits", this::configureLimits);
        button(actions, "Clean Generated Artifacts", this::cleanGeneratedArtifacts);
        if (isLegacyWorkspace()) {
            button(actions, "Import Legacy Attempts", this::importLegacyAttempts);
        }
        header.add(actions);
        search.getEmptyText().setText("Search exercises");
        search.getAccessibleContext().setAccessibleName("Search exercises");
        header.add(search);
        JPanel filters = new JPanel(new GridLayout(1, 3, 4, 0));
        topic.addItem("All topics");
        ExerciseCatalog.all().stream().map(Exercise::topic).distinct().sorted().forEach(topic::addItem);
        difficulty.addItem("All difficulties");
        ExerciseCatalog.all().stream().map(Exercise::difficulty).distinct().sorted().forEach(difficulty::addItem);
        topic.getAccessibleContext().setAccessibleName("Topic filter");
        difficulty.getAccessibleContext().setAccessibleName("Difficulty filter");
        progressFilter.getAccessibleContext().setAccessibleName("Progress filter");
        filters.add(topic);
        filters.add(difficulty);
        filters.add(progressFilter);
        header.add(filters);
        add(header, BorderLayout.NORTH);

        exercises.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        exercises.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(
                    JList<?> list, Object value, int index, boolean selected, boolean focus
            ) {
                super.getListCellRendererComponent(list, value, index, selected, focus);
                if (value instanceof Exercise exercise) {
                    setText(exercise.title() + "  [" + exercise.difficulty() + "]");
                }
                return this;
            }
        });
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
        attempts.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(
                    JList<?> list, Object value, int index, boolean selected, boolean focus
            ) {
                super.getListCellRendererComponent(list, value, index, selected, focus);
                if (value instanceof ManagedPracticeWorkspace.Attempt attempt) {
                    setText(attempt.id());
                }
                return this;
            }
        });
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
        runner.setListener(text -> {
            if (!disposed) {
                results.setText(text);
                if (hasUnsavedAttemptFile()) {
                    results.append("\nSTALE: there are unsaved changes. Check again after saving.");
                }
                results.setCaretPosition(0);
                status.setText(runner.isRunning() ? "Running..." : "Run finished; see Results.");
                updateButtons();
            }
        });
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
        topic.addActionListener(event -> filter());
        difficulty.addActionListener(event -> filter());
        progressFilter.addActionListener(event -> filter());
        search.getDocument().addDocumentListener(new DocumentAdapter() {
            @Override
            protected void textChanged(@NotNull javax.swing.event.DocumentEvent event) {
                filter();
            }
        });
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
        String query = search.getText().toLowerCase(Locale.ROOT);
        for (Exercise exercise : ExerciseCatalog.all()) {
            boolean passed = progress.getState().attempts.values().stream()
                    .anyMatch(entry -> entry.exerciseId.equals(exercise.id()) && !entry.lastPassedAt.isEmpty());
            if ((topic.getSelectedIndex() == 0 || exercise.topic().equals(topic.getSelectedItem()))
                    && (difficulty.getSelectedIndex() == 0 || exercise.difficulty().equals(difficulty.getSelectedItem()))
                    && (progressFilter.getSelectedIndex() == 0 || passed == (progressFilter.getSelectedIndex() == 1))
                    && (exercise.title() + " " + exercise.topic()).toLowerCase(Locale.ROOT).contains(query)) {
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
        statement.setText(exercise == null ? "No exercises match these filters." : problemText(exercise));
        statement.setCaretPosition(0);
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
        background("Importing legacy attempts", () -> workspace.importLegacy(
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
        if (PracticeModuleWorkspace.get(project).cleanupGeneratedArtifacts()) {
            status.setText("Removed inactive generated practice artifacts.");
        } else {
            error("Stop the active practice run before cleaning generated artifacts.");
        }
    }

    private void configureLimits() {
        JSpinner perTest = new JSpinner(new SpinnerNumberModel(progress.getState().testSeconds, 1, 300, 1));
        JSpinner perSuite = new JSpinner(new SpinnerNumberModel(progress.getState().suiteSeconds, 1, 1800, 5));
        JSpinner heap = new JSpinner(new SpinnerNumberModel(progress.getState().heapMb, 64, 2048, 64));
        JPanel fields = new JPanel(new GridLayout(0, 2, 8, 8));
        fields.add(new JLabel("Seconds per test")); fields.add(perTest);
        fields.add(new JLabel("Seconds per suite")); fields.add(perSuite);
        fields.add(new JLabel("Test heap (MiB)")); fields.add(heap);
        var dialog = new com.intellij.openapi.ui.DialogWrapper(project) {
            { setTitle("Practice Execution Limits"); init(); }
            @Override protected javax.swing.JComponent createCenterPanel() { return fields; }
            @Override protected com.intellij.openapi.ui.ValidationInfo doValidate() {
                return (int) perSuite.getValue() < (int) perTest.getValue()
                        ? new com.intellij.openapi.ui.ValidationInfo(
                        "Suite limit must be at least the per-test limit.", perSuite) : null;
            }
        };
        if (dialog.showAndGet()) {
            progress.getState().testSeconds = (int) perTest.getValue();
            progress.getState().suiteSeconds = (int) perSuite.getValue();
            progress.getState().heapMb = (int) heap.getValue();
        }
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

    private static String problemText(Exercise exercise) {
        StringBuilder text = new StringBuilder(exercise.statement().strip());
        text.append("\n\nVisible examples\n");
        for (int index = 0; index < exercise.examples().size(); index++) {
            var example = exercise.examples().get(index);
            text.append("\nExample ").append(index + 1).append("\nInput: ")
                    .append(example.input()).append("\nOutput: ").append(example.output()).append('\n');
        }
        return text.toString();
    }

    private static JBTextArea textArea() {
        JBTextArea area = new JBTextArea();
        area.setEditable(false);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setBorder(JBUI.Borders.empty(8));
        return area;
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

    @Override
    public void dispose() {
        disposed = true;
        runner.setListener(ignored -> {});
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
