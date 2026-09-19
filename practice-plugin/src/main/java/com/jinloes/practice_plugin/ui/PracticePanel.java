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
import com.jinloes.practice_plugin.state.PracticeProgress;
import com.jinloes.practice_plugin.state.ScratchPracticeProgress;
import com.jinloes.practice_plugin.workspace.GradleWorkspaceImport;
import com.jinloes.practice_plugin.workspace.PracticeSupportEnvironment;
import com.jinloes.practice_plugin.workspace.PracticeWorkspace;
import com.jinloes.practice_plugin.workspace.ScratchAttemptStore;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.function.Consumer;

final class PracticePanel extends JPanel implements Disposable {
    private enum AttemptKind {
        SCRATCH,
        LEGACY
    }

    private record AttemptEntry(AttemptKind kind, String id, String exerciseId, Path solution, Path directory) {
        private String displayName() {
            return (kind == AttemptKind.SCRATCH ? "Scratch: " : "Legacy: ") + id;
        }
    }

    private final Project project;
    private final PracticeProgress legacyProgress;
    private final ScratchPracticeProgress scratchProgress;
    private final ScratchAttemptStore scratchAttempts;
    private final PracticeRunner runner;
    private final DefaultListModel<Exercise> model = new DefaultListModel<>();
    private final JBList<Exercise> exercises = new JBList<>(model);
    private final JComboBox<String> topic = new JComboBox<>();
    private final JComboBox<String> difficulty = new JComboBox<>();
    private final JComboBox<String> progressFilter = new JComboBox<>(new String[]{"All progress", "Passed before", "Not passed"});
    private final JBTextField search = new JBTextField();
    private final JComboBox<AttemptEntry> attempts = new JComboBox<>();
    private final JBTextArea statement = textArea();
    private final JBTextArea hints = textArea();
    private final JBTextArea results = textArea();
    private final JLabel status = new JLabel("Choose an exercise.");
    private final JButton runExamples = new JButton("Run Examples");
    private final JButton debugExamples = new JButton("Debug Examples");
    private final JButton check = new JButton("Check Solution");
    private final JButton stop = new JButton("Stop");
    private final JButton copyLegacy = new JButton("Copy Legacy Attempt to Scratch");
    private boolean updatingAttempts;
    private boolean disposed;
    private int selectionGeneration;

    PracticePanel(Project project) {
        super(new BorderLayout(6, 6));
        this.project = project;
        legacyProgress = PracticeProgress.get(project);
        scratchProgress = ScratchPracticeProgress.get();
        scratchAttempts = new ScratchAttemptStore(project);
        runner = project.getService(PracticeRunner.class);
        setBorder(JBUI.Borders.empty(8));

        JPanel header = new JPanel(new GridLayout(0, 1, 4, 4));
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT));
        button(actions, "Limits", this::configureLimits);
        button(actions, "Clean Support Artifacts", this::cleanSupportArtifacts);
        if (isLegacyWorkspace()) {
            button(actions, "Reload Legacy Gradle", this::reloadLegacy);
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
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                           boolean selected, boolean focus) {
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
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                           boolean selected, boolean focus) {
                super.getListCellRendererComponent(list, value, index, selected, focus);
                if (value instanceof AttemptEntry attempt) {
                    setText(attempt.displayName());
                }
                return this;
            }
        });
        JPanel footer = new JPanel(new GridLayout(0, 1, 4, 4));
        footer.add(attempts);
        JPanel editing = new JPanel(new FlowLayout(FlowLayout.LEFT));
        button(editing, "Start / Resume", this::startOrResume);
        button(editing, "New Attempt", this::newAttempt);
        copyLegacy.addActionListener(event -> guarded(this::copyLegacyAttempt));
        copyLegacy.setVisible(isLegacyWorkspace());
        editing.add(copyLegacy);
        footer.add(editing);
        JPanel executions = new JPanel(new FlowLayout(FlowLayout.LEFT));
        executions.add(runExamples);
        executions.add(debugExamples);
        executions.add(check);
        executions.add(stop);
        footer.add(executions);
        footer.add(status);
        add(footer, BorderLayout.SOUTH);

        runExamples.addActionListener(event -> guarded(() -> runSelected(false)));
        debugExamples.addActionListener(event -> guarded(this::debugSelected));
        check.addActionListener(event -> guarded(() -> runSelected(true)));
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
                AttemptEntry attempt = selectedAttemptOrNull();
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
            boolean passed = hasHistoricalPass(exercise.id());
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

    private boolean hasHistoricalPass(String exerciseId) {
        return scratchProgress.getState().attempts.values().stream()
                .anyMatch(entry -> entry.exerciseId.equals(exerciseId) && !entry.lastPassedAt.isEmpty())
                || legacyProgress.getState().attempts.values().stream()
                .anyMatch(entry -> entry.exerciseId.equals(exerciseId) && !entry.lastPassedAt.isEmpty());
    }

    private void showExercise() {
        int generation = ++selectionGeneration;
        Exercise exercise = exercises.getSelectedValue();
        updatingAttempts = true;
        attempts.removeAllItems();
        updatingAttempts = false;
        statement.setText(exercise == null ? "No exercises match these filters." : exercise.statement());
        statement.setCaretPosition(0);
        hints.setText("Start or resume an attempt to reveal hints.");
        results.setText("");
        updateButtons();
        if (exercise != null) {
            background("Loading practice attempts", () -> loadAttempts(exercise), loaded -> {
                if (generation == selectionGeneration && exercise.equals(exercises.getSelectedValue())) {
                    updatingAttempts = true;
                    loaded.forEach(attempts::addItem);
                    String selected = scratchProgress.getState().selectedAttempts.get(exercise.id());
                    if (selected == null) {
                        selected = legacyProgress.getState().selectedAttempts.get(exercise.id());
                    }
                    String selectedId = selected;
                    loaded.stream().filter(attempt -> attempt.id().equals(selectedId)).findFirst()
                            .ifPresent(attempts::setSelectedItem);
                    updatingAttempts = false;
                    showAttempt();
                }
            });
        }
    }

    private List<AttemptEntry> loadAttempts(Exercise exercise) throws IOException {
        List<AttemptEntry> loaded = new ArrayList<>();
        for (ScratchAttemptStore.Attempt attempt : scratchAttempts.attempts(exercise.id())) {
            loaded.add(new AttemptEntry(AttemptKind.SCRATCH, attempt.id(), attempt.exerciseId(),
                    attempt.solution(), attempt.directory()));
        }
        if (isLegacyWorkspace()) {
            for (PracticeWorkspace.Attempt attempt : PracticeWorkspace.attempts(root(), exercise.id())) {
                loaded.add(new AttemptEntry(AttemptKind.LEGACY, attempt.id(), attempt.exerciseId(),
                        attempt.solution(), attempt.directory()));
            }
        }
        return List.copyOf(loaded);
    }

    private void showAttempt() {
        AttemptEntry attempt = selectedAttemptOrNull();
        Exercise exercise = exercises.getSelectedValue();
        updateButtons();
        if (attempt == null || exercise == null) {
            status.setText("Choose Start / Resume to create your first scratch attempt.");
            return;
        }
        selectAttempt(attempt, exercise);
        int hintsRevealed = hintsRevealed(attempt, exercise);
        hints.setText(String.join("\n\n", exercise.hints().subList(0,
                Math.min(hintsRevealed, exercise.hints().size()))));
        if (hints.getText().isEmpty()) {
            hints.setText("Hints are optional. Reveal one only when you want help.");
        }
        status.setText((attempt.kind() == AttemptKind.SCRATCH ? "Scratch" : "Legacy") + " attempt ready. "
                + status(attempt, exercise));
        results.setText(status(attempt, exercise) + "\n" + details(attempt, exercise)
                + "\nLast full check: " + checkedAt(attempt, exercise)
                + "\nMost recent pass: " + lastPassedAt(attempt, exercise));
        String checkedFingerprint = checkedFingerprint(attempt, exercise);
        if (!checkedFingerprint.isEmpty()) {
            background("Checking result freshness", () -> fingerprint(attempt), hash -> {
                if (attempt.equals(selectedAttemptOrNull())
                        && (!hash.equals(checkedFingerprint) || hasUnsavedAttemptFile())) {
                    status.setText("Edited since last check; previous result is stale.");
                    results.append("\nSTALE: check the current files again.");
                }
            });
        }
    }

    private void startOrResume() throws IOException {
        AttemptEntry selected = selectedAttemptOrNull();
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
        background("Creating scratch attempt", () -> scratchAttempts.create(exercise), attempt -> {
            AttemptEntry entry = scratchEntry(attempt);
            scratchProgress.getState().selectedAttempts.put(exercise.id(), attempt.id());
            if (exercise.equals(exercises.getSelectedValue())) {
                attempts.addItem(entry);
                attempts.setSelectedItem(entry);
            }
            openSolution(entry);
        });
    }

    private void copyLegacyAttempt() throws IOException {
        AttemptEntry selected = selectedAttempt();
        if (selected.kind() != AttemptKind.LEGACY) {
            throw new IOException("Select a legacy attempt to copy it to a scratch.");
        }
        Exercise exercise = exercises.getSelectedValue();
        PracticeWorkspace.Attempt legacy = PracticeWorkspace.readAttempt(root(), selected.id());
        background("Copying legacy attempt to scratch", () -> scratchAttempts.copyLegacy(root(), legacy), copied -> {
            AttemptEntry entry = scratchEntry(copied);
            scratchProgress.getState().selectedAttempts.put(exercise.id(), copied.id());
            attempts.insertItemAt(entry, 0);
            attempts.setSelectedItem(entry);
            openSolution(entry);
        });
    }

    private void openSolution(AttemptEntry attempt) {
        background("Opening solution", () -> {
            if (attempt.kind() == AttemptKind.SCRATCH) {
                try {
                    PracticeSupportEnvironment.get(project).module();
                } catch (ExecutionException exception) {
                    throw new IOException("Cannot prepare Java support for the scratch: "
                            + exception.getMessage(), exception);
                }
            }
            var file = LocalFileSystem.getInstance().refreshAndFindFileByNioFile(attempt.solution());
            if (file == null) {
                throw new IOException("Solution file is missing: " + attempt.solution());
            }
            return file;
        }, file -> FileEditorManager.getInstance(project).openFile(file, true));
    }

    private void revealHint() throws IOException {
        AttemptEntry attempt = selectedAttempt();
        Exercise exercise = exercises.getSelectedValue();
        if (attempt.kind() == AttemptKind.SCRATCH) {
            var entry = scratchProgress.entry(attempt.id(), exercise.id());
            entry.hintsRevealed = Math.min(entry.hintsRevealed + 1, exercise.hints().size());
        } else {
            var entry = legacyProgress.entry(attempt.id(), exercise.id());
            entry.hintsRevealed = Math.min(entry.hintsRevealed + 1, exercise.hints().size());
        }
        showAttempt();
    }

    private void runSelected(boolean full) throws IOException, ExecutionException {
        AttemptEntry attempt = selectedAttempt();
        if (attempt.kind() == AttemptKind.SCRATCH) {
            runner.runScratch(attempt.id(), full);
        } else {
            runner.runLegacy(attempt.id(), full);
        }
    }

    private void debugSelected() throws IOException, ExecutionException {
        AttemptEntry attempt = selectedAttempt();
        if (attempt.kind() == AttemptKind.SCRATCH) {
            runner.debugScratch(attempt.id());
        } else {
            runner.debugLegacy(attempt.id());
        }
    }

    private void reloadLegacy() {
        if (!isLegacyWorkspace()) {
            return;
        }
        if (runner.isRunning()) {
            error("Stop the active practice run before reloading Gradle.");
            return;
        }
        status.setText("Importing legacy Gradle project...");
        GradleWorkspaceImport.refresh(project, success -> ApplicationManager.getApplication().invokeLater(() -> {
            if (!disposed) {
                status.setText(success ? "Legacy Gradle project imported."
                        : "Legacy import failed. See Build output and retry.");
            }
        }));
    }

    private void cleanSupportArtifacts() {
        if (PracticeSupportEnvironment.get(project).cleanupAbandonedArtifacts()) {
            status.setText("Removed inactive verification support artifacts.");
        } else {
            error("Stop the active practice run before cleaning support artifacts.");
        }
    }

    private void configureLimits() {
        AttemptEntry selected = selectedAttemptOrNull();
        int testSeconds = selected != null && selected.kind() == AttemptKind.LEGACY
                ? legacyProgress.getState().testSeconds : scratchProgress.getState().testSeconds;
        int suiteSeconds = selected != null && selected.kind() == AttemptKind.LEGACY
                ? legacyProgress.getState().suiteSeconds : scratchProgress.getState().suiteSeconds;
        int heapMb = selected != null && selected.kind() == AttemptKind.LEGACY
                ? legacyProgress.getState().heapMb : scratchProgress.getState().heapMb;
        JSpinner perTest = new JSpinner(new SpinnerNumberModel(testSeconds, 1, 300, 1));
        JSpinner perSuite = new JSpinner(new SpinnerNumberModel(suiteSeconds, 1, 1800, 5));
        JSpinner heap = new JSpinner(new SpinnerNumberModel(heapMb, 64, 2048, 64));
        JPanel fields = new JPanel(new GridLayout(0, 2, 8, 8));
        fields.add(new JLabel("Seconds per test")); fields.add(perTest);
        fields.add(new JLabel("Seconds per suite")); fields.add(perSuite);
        fields.add(new JLabel("Test heap (MiB)")); fields.add(heap);
        var dialog = new com.intellij.openapi.ui.DialogWrapper(project) {
            { setTitle("Practice Execution Limits"); init(); }
            @Override protected javax.swing.JComponent createCenterPanel() { return fields; }
            @Override protected com.intellij.openapi.ui.ValidationInfo doValidate() {
                return (int) perSuite.getValue() < (int) perTest.getValue()
                        ? new com.intellij.openapi.ui.ValidationInfo("Suite limit must be at least the per-test limit.", perSuite)
                        : null;
            }
        };
        if (dialog.showAndGet()) {
            if (selected != null && selected.kind() == AttemptKind.LEGACY) {
                legacyProgress.getState().testSeconds = (int) perTest.getValue();
                legacyProgress.getState().suiteSeconds = (int) perSuite.getValue();
                legacyProgress.getState().heapMb = (int) heap.getValue();
            } else {
                scratchProgress.getState().testSeconds = (int) perTest.getValue();
                scratchProgress.getState().suiteSeconds = (int) perSuite.getValue();
                scratchProgress.getState().heapMb = (int) heap.getValue();
            }
        }
    }

    private void updateButtons() {
        AttemptEntry attempt = selectedAttemptOrNull();
        boolean ready = attempt != null && !runner.isRunning();
        runExamples.setEnabled(ready);
        debugExamples.setEnabled(ready);
        check.setEnabled(ready);
        stop.setEnabled(runner.isRunning());
        copyLegacy.setEnabled(ready && attempt.kind() == AttemptKind.LEGACY);
    }

    private boolean hasUnsavedAttemptFile() {
        AttemptEntry attempt = selectedAttemptOrNull();
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

    private AttemptEntry selectedAttempt() throws IOException {
        AttemptEntry attempt = selectedAttemptOrNull();
        if (attempt == null) {
            throw new IOException("Start or resume an attempt first.");
        }
        return attempt;
    }

    private AttemptEntry selectedAttemptOrNull() {
        return (AttemptEntry) attempts.getSelectedItem();
    }

    private void selectAttempt(AttemptEntry attempt, Exercise exercise) {
        if (attempt.kind() == AttemptKind.SCRATCH) {
            scratchProgress.getState().selectedAttempts.put(exercise.id(), attempt.id());
        } else {
            legacyProgress.getState().selectedAttempts.put(exercise.id(), attempt.id());
        }
    }

    private int hintsRevealed(AttemptEntry attempt, Exercise exercise) {
        return attempt.kind() == AttemptKind.SCRATCH
                ? scratchProgress.entry(attempt.id(), exercise.id()).hintsRevealed
                : legacyProgress.entry(attempt.id(), exercise.id()).hintsRevealed;
    }

    private String status(AttemptEntry attempt, Exercise exercise) {
        return attempt.kind() == AttemptKind.SCRATCH
                ? scratchProgress.entry(attempt.id(), exercise.id()).status
                : legacyProgress.entry(attempt.id(), exercise.id()).status;
    }

    private String details(AttemptEntry attempt, Exercise exercise) {
        return attempt.kind() == AttemptKind.SCRATCH
                ? scratchProgress.entry(attempt.id(), exercise.id()).details
                : legacyProgress.entry(attempt.id(), exercise.id()).details;
    }

    private String checkedAt(AttemptEntry attempt, Exercise exercise) {
        return attempt.kind() == AttemptKind.SCRATCH
                ? scratchProgress.entry(attempt.id(), exercise.id()).checkedAt
                : legacyProgress.entry(attempt.id(), exercise.id()).checkedAt;
    }

    private String lastPassedAt(AttemptEntry attempt, Exercise exercise) {
        return attempt.kind() == AttemptKind.SCRATCH
                ? scratchProgress.entry(attempt.id(), exercise.id()).lastPassedAt
                : legacyProgress.entry(attempt.id(), exercise.id()).lastPassedAt;
    }

    private String checkedFingerprint(AttemptEntry attempt, Exercise exercise) {
        return attempt.kind() == AttemptKind.SCRATCH
                ? scratchProgress.entry(attempt.id(), exercise.id()).checkedFingerprint
                : legacyProgress.entry(attempt.id(), exercise.id()).checkedFingerprint;
    }

    private String fingerprint(AttemptEntry attempt) throws IOException {
        if (attempt.kind() == AttemptKind.SCRATCH) {
            return scratchAttempts.fingerprint(new ScratchAttemptStore.Attempt(
                    attempt.id(), attempt.exerciseId(), ScratchAttemptStore.REVISION, attempt.solution()));
        }
        return PracticeWorkspace.fingerprint(root(), new PracticeWorkspace.Attempt(
                attempt.id(), attempt.exerciseId(), PracticeWorkspace.REVISION, attempt.directory()));
    }

    private AttemptEntry scratchEntry(ScratchAttemptStore.Attempt attempt) {
        return new AttemptEntry(AttemptKind.SCRATCH, attempt.id(), attempt.exerciseId(),
                attempt.solution(), attempt.directory());
    }

    private boolean isLegacyWorkspace() {
        return project.getBasePath() != null && PracticeWorkspace.isWorkspace(root());
    }

    private Path root() {
        return Path.of(Objects.requireNonNull(project.getBasePath()));
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
        } catch (IOException | ExecutionException | IllegalArgumentException e) {
            error(e.getMessage());
        }
    }

    private <T> void background(String title, IoTask<T> operation, Consumer<T> completed) {
        new Task.Backgroundable(project, title, false) {
            @Override public void run(@NotNull ProgressIndicator indicator) {
                try {
                    T result = operation.run();
                    ApplicationManager.getApplication().invokeLater(() -> {
                        if (!disposed && !project.isDisposed()) {
                            completed.accept(result);
                        }
                    });
                } catch (IOException | IllegalArgumentException e) {
                    ApplicationManager.getApplication().invokeLater(() -> {
                        if (!disposed && !project.isDisposed()) {
                            error(e.getMessage());
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
