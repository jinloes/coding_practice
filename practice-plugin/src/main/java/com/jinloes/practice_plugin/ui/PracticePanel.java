package com.jinloes.practice_plugin.ui;

import com.intellij.execution.ExecutionException;
import com.intellij.ide.impl.OpenProjectTask;
import com.intellij.ide.impl.ProjectUtil;
import com.intellij.notification.NotificationGroupManager;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.editor.event.DocumentEvent;
import com.intellij.openapi.editor.event.DocumentListener;
import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
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
import com.jinloes.practice_plugin.workspace.GradleWorkspaceImport;
import com.jinloes.practice_plugin.workspace.PracticeWorkspace;
import com.jinloes.practice_plugin.workspace.PracticeWorkspace.Attempt;
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
import java.util.Locale;
import java.util.Objects;
import java.util.function.Consumer;

final class PracticePanel extends JPanel implements Disposable {
    private final Project project;
    private final PracticeProgress progress;
    private final PracticeRunner runner;
    private final DefaultListModel<Exercise> model = new DefaultListModel<>();
    private final JBList<Exercise> exercises = new JBList<>(model);
    private final JComboBox<String> topic = new JComboBox<>();
    private final JComboBox<String> difficulty = new JComboBox<>();
    private final JComboBox<String> progressFilter = new JComboBox<>(new String[]{"All progress", "Passed before", "Not passed"});
    private final JBTextField search = new JBTextField();
    private final JComboBox<Attempt> attempts = new JComboBox<>();
    private final JBTextArea statement = textArea();
    private final JBTextArea hints = textArea();
    private final JBTextArea results = textArea();
    private final JLabel status = new JLabel("Choose an exercise.");
    private final JButton runExamples = new JButton("Run Examples");
    private final JButton debugExamples = new JButton("Debug Examples");
    private final JButton check = new JButton("Check Solution");
    private final JButton stop = new JButton("Stop");
    private boolean updatingAttempts;
    private boolean disposed;
    private int selectionGeneration;

    PracticePanel(Project project) {
        super(new BorderLayout(6, 6));
        this.project = project;
        this.progress = PracticeProgress.get(project);
        this.runner = project.getService(PracticeRunner.class);
        setBorder(JBUI.Borders.empty(8));

        JPanel header = new JPanel(new GridLayout(0, 1, 4, 4));
        JPanel projects = new JPanel(new FlowLayout(FlowLayout.LEFT));
        button(projects, "Create Practice Project", this::createProject);
        button(projects, "Open Practice Project", this::openProject);
        button(projects, "Reload Gradle", this::reload);
        button(projects, "Limits", this::configureLimits);
        header.add(projects);
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
                if (value instanceof Attempt attempt) {
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
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT));
        actions.add(runExamples);
        actions.add(debugExamples);
        actions.add(check);
        actions.add(stop);
        footer.add(actions);
        footer.add(status);
        add(footer, BorderLayout.SOUTH);

        runExamples.addActionListener(event -> guarded(() -> runner.run(selectedAttempt().id(), false)));
        debugExamples.addActionListener(event -> guarded(() -> runner.debug(selectedAttempt().id())));
        check.addActionListener(event -> guarded(() -> runner.run(selectedAttempt().id(), true)));
        stop.addActionListener(event -> runner.stop());
        runner.setListener(text -> {
            if (!disposed) {
                results.setText(text);
                if (hasUnsavedAttemptFiles()) {
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
            @Override protected void textChanged(@NotNull javax.swing.event.DocumentEvent event) {
                filter();
            }
        });
        EditorFactory.getInstance().getEventMulticaster().addDocumentListener(new DocumentListener() {
            @Override public void documentChanged(@NotNull DocumentEvent event) {
                Attempt attempt = (Attempt) attempts.getSelectedItem();
                var file = FileDocumentManager.getInstance().getFile(event.getDocument());
                if (attempt != null && file != null && relevantFile(Path.of(file.getPath()), attempt)) {
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
        statement.setText(exercise == null ? "No exercises match these filters." : exercise.statement());
        statement.setCaretPosition(0);
        hints.setText("Start or resume an attempt to reveal hints.");
        results.setText("");
        updateButtons();
        if (exercise != null && isWorkspace()) {
            background("Loading practice attempts", () -> PracticeWorkspace.attempts(root(), exercise.id()), loaded -> {
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
        } else {
            status.setText("Create or open a dedicated practice project to begin.");
        }
    }

    private void showAttempt() {
        Attempt attempt = (Attempt) attempts.getSelectedItem();
        Exercise exercise = exercises.getSelectedValue();
        updateButtons();
        if (attempt == null || exercise == null) {
            status.setText("Choose Start / Resume to create your first attempt.");
            return;
        }
        progress.getState().selectedAttempts.put(exercise.id(), attempt.id());
        var entry = progress.entry(attempt.id(), exercise.id());
        hints.setText(String.join("\n\n", exercise.hints().subList(0,
                Math.min(entry.hintsRevealed, exercise.hints().size()))));
        if (hints.getText().isEmpty()) {
            hints.setText("Hints are optional. Reveal one only when you want help.");
        }
        status.setText("Attempt ready. " + entry.status);
        results.setText(entry.status + "\n" + entry.details + "\nLast full check: " + entry.checkedAt
                + "\nMost recent pass: " + entry.lastPassedAt);
        if (!entry.checkedFingerprint.isEmpty()) {
            background("Checking result freshness", () -> PracticeWorkspace.fingerprint(root(), attempt), hash -> {
                if (attempt.equals(attempts.getSelectedItem())
                        && (!hash.equals(entry.checkedFingerprint) || hasUnsavedAttemptFiles())) {
                    status.setText("Edited since last check; previous result is stale.");
                    results.append("\nSTALE: check the current files again.");
                }
            });
        }
    }

    private void startOrResume() throws IOException {
        requireWorkspace();
        Attempt selected = (Attempt) attempts.getSelectedItem();
        if (selected == null) {
            newAttempt();
        } else {
            openSolution(selected);
        }
    }

    private void newAttempt() throws IOException {
        requireWorkspace();
        if (runner.isRunning()) {
            throw new IOException("Stop the active run before creating another attempt.");
        }
        Exercise exercise = exercises.getSelectedValue();
        if (exercise == null) {
            throw new IOException("Select an exercise first.");
        }
        background("Creating practice attempt", () -> PracticeWorkspace.createAttempt(root(), exercise), attempt -> {
            progress.getState().selectedAttempts.put(exercise.id(), attempt.id());
            if (exercise.equals(exercises.getSelectedValue())) {
                attempts.addItem(attempt);
                attempts.setSelectedItem(attempt);
            }
            openSolution(attempt);
            reload();
        });
    }

    private void openSolution(Attempt attempt) {
        background("Opening solution", () -> {
            var file = LocalFileSystem.getInstance().refreshAndFindFileByNioFile(attempt.solution());
            if (file == null) {
                throw new IOException("Solution file is missing: " + attempt.solution());
            }
            return file;
        }, file -> FileEditorManager.getInstance(project).openFile(file, true));
    }

    private void revealHint() throws IOException {
        Attempt attempt = selectedAttempt();
        Exercise exercise = exercises.getSelectedValue();
        var entry = progress.entry(attempt.id(), exercise.id());
        entry.hintsRevealed = Math.min(entry.hintsRevealed + 1, exercise.hints().size());
        showAttempt();
    }

    private void createProject() {
        var parent = FileChooser.chooseFile(FileChooserDescriptorFactory.createSingleFolderDescriptor()
                .withTitle("Choose Parent Directory for New Practice Project"), project, null);
        if (parent == null) {
            return;
        }
        String name = Messages.showInputDialog(project, "New directory name:", "Create Practice Project",
                null, "algorithm-practice", null);
        if (name == null) {
            return;
        }
        if (!name.matches("[a-zA-Z0-9][a-zA-Z0-9_-]*")) {
            error("Use a directory name containing letters, digits, hyphens, or underscores.");
            return;
        }
        Path destination = Path.of(parent.getPath()).resolve(name);
        background("Creating practice project", () -> {
            PracticeWorkspace.create(destination);
            return destination;
        }, this::openPracticeProject);
    }

    private void openProject() {
        var folder = FileChooser.chooseFile(FileChooserDescriptorFactory.createSingleFolderDescriptor()
                .withTitle("Open Plugin-Created Practice Project"), project, null);
        if (folder != null) {
            Path destination = Path.of(folder.getPath());
            if (!PracticeWorkspace.isWorkspace(destination)) {
                error("This is not a plugin-created practice project.");
            } else {
                openPracticeProject(destination);
            }
        }
    }

    private void openPracticeProject(Path path) {
        background("Opening practice project", () -> {
            Project opened = ProjectUtil.openOrImport(path.toRealPath(), OpenProjectTask.build().withForceOpenInNewFrame(true));
            if (opened == null) {
                throw new IOException("Project opening was cancelled or failed. Open " + path + " from File > Open.");
            }
            return opened;
        }, opened -> GradleWorkspaceImport.refresh(opened, success -> {
            if (!success) {
                NotificationGroupManager.getInstance().getNotificationGroup("Algorithm Practice")
                        .createNotification("Gradle import failed. Configure a JDK 17+ and retry Reload Gradle.",
                                NotificationType.ERROR).notify(opened);
            }
        }));
    }

    private void reload() {
        if (!isWorkspace()) {
            error("Open a plugin-created practice project first.");
            return;
        }
        if (runner.isRunning()) {
            error("Stop the active practice run before reloading Gradle.");
            return;
        }
        status.setText("Importing Gradle project...");
        GradleWorkspaceImport.refresh(project, success -> ApplicationManager.getApplication().invokeLater(() -> {
            if (!disposed) {
                status.setText(success ? "Gradle imported. Ready to code and debug."
                        : "Import failed. See Build output; configure a JDK 17+ and retry.");
            }
        }));
    }

    private void configureLimits() {
        var data = progress.getState();
        JSpinner perTest = new JSpinner(new SpinnerNumberModel(data.testSeconds, 1, 300, 1));
        JSpinner perSuite = new JSpinner(new SpinnerNumberModel(data.suiteSeconds, 1, 1800, 5));
        JSpinner heap = new JSpinner(new SpinnerNumberModel(data.heapMb, 64, 2048, 64));
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
            data.testSeconds = (int) perTest.getValue();
            data.suiteSeconds = (int) perSuite.getValue();
            data.heapMb = (int) heap.getValue();
        }
    }

    private void updateButtons() {
        boolean ready = attempts.getSelectedItem() != null && !runner.isRunning();
        runExamples.setEnabled(ready);
        debugExamples.setEnabled(ready);
        check.setEnabled(ready);
        stop.setEnabled(runner.isRunning());
    }

    private boolean hasUnsavedAttemptFiles() {
        Attempt attempt = (Attempt) attempts.getSelectedItem();
        if (attempt == null) {
            return false;
        }
        for (var document : FileDocumentManager.getInstance().getUnsavedDocuments()) {
            var file = FileDocumentManager.getInstance().getFile(document);
            if (file != null && relevantFile(Path.of(file.getPath()), attempt)) {
                return true;
            }
        }
        return false;
    }

    private boolean relevantFile(Path file, Attempt attempt) {
        return file.startsWith(attempt.directory()) || file.equals(root().resolve("build.gradle"))
                || file.equals(root().resolve("settings.gradle"));
    }

    private Attempt selectedAttempt() throws IOException {
        Attempt attempt = (Attempt) attempts.getSelectedItem();
        if (attempt == null) {
            throw new IOException("Start or resume an attempt first.");
        }
        return attempt;
    }

    private boolean isWorkspace() {
        return project.getBasePath() != null && PracticeWorkspace.isWorkspace(root());
    }

    private void requireWorkspace() throws IOException {
        if (!isWorkspace()) {
            throw new IOException("Create or open a dedicated practice project first.");
        }
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

    @Override public void dispose() {
        disposed = true;
        runner.setListener(ignored -> {});
    }

    @FunctionalInterface private interface Action {
        void run() throws IOException, ExecutionException;
    }

    @FunctionalInterface private interface IoTask<T> {
        T run() throws IOException;
    }
}
