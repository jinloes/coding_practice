package com.jinloes.practice_plugin.ui;

import com.intellij.execution.ExecutionException;
import com.intellij.icons.AllIcons;
import com.intellij.notification.NotificationGroupManager;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.editor.event.DocumentEvent;
import com.intellij.openapi.editor.event.DocumentListener;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.ui.OnePixelSplitter;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBList;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.components.JBTabbedPane;
import com.intellij.ui.components.JBTextArea;
import com.intellij.util.ui.JBFont;
import com.intellij.util.ui.JBUI;
import com.intellij.util.ui.UIUtil;
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
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;

final class PracticePanel extends JPanel implements Disposable, PracticeActions.RunControls {
    private static final int PROBLEM_TAB = 0;
    private static final int GUIDE_TAB = 1;

    private final Project project;
    private final ManagedPracticeProgress progress;
    private final ManagedPracticeWorkspace workspace;
    private final LegacyImportService legacyImport;
    private final PracticeRunner runner;
    private final BackgroundTasks tasks;
    private final DefaultListModel<Exercise> model = new DefaultListModel<>();
    private final JBList<Exercise> exercises = new JBList<>(model);
    private final ExerciseFilters filters = new ExerciseFilters();
    private final JBLabel count = new JBLabel();
    private final JComboBox<ManagedPracticeWorkspace.Attempt> attempts = new JComboBox<>();
    private final PageView problem;
    private final PageView guide;
    private final JBTabbedPane tabs = new JBTabbedPane();
    private final JBTextArea hints = PracticeViews.textArea();
    private final ResultsView results = new ResultsView();
    private final HintButton hintButton = new HintButton(() -> guarded(this::revealHint));
    private final JBLabel status = new JBLabel("Choose an exercise.");
    private final ActionToolbar toolbar;
    private final List<AnAction> titleActions = new ArrayList<>();
    private final CustomInputPrompt input = new CustomInputPrompt();
    private Set<String> passed = Set.of();
    private boolean updatingAttempts;
    private boolean disposed;
    private boolean checking;
    private int selectionGeneration;

    /**
     * Held as a field so {@link #dispose()} can remove exactly this registration. The runner
     * outlives the panel, so a lambda created inline could never be removed.
     */
    private final Consumer<String> runOutput = this::showRunOutput;

    PracticePanel(Project project) {
        super(new BorderLayout(0, 6));
        this.project = project;
        progress = ManagedPracticeProgress.get();
        workspace = ManagedPracticeWorkspace.get();
        legacyImport = new LegacyImportService(workspace);
        runner = project.getService(PracticeRunner.class);
        tasks = new BackgroundTasks(project, () -> !disposed, this::error);
        problem = PageView.create(this, this::onPageEvent);
        guide = PageView.create(this, this::onPageEvent);
        setBorder(JBUI.Borders.empty(8));

        titleActions.add(PracticeActions.action("Limits", "Edit run and check limits",
                AllIcons.General.Settings, () -> true, () -> guarded(() -> LimitsDialog.edit(project, progress))));
        titleActions.add(PracticeActions.action("Clean Generated Artifacts",
                "Remove inactive generated practice artifacts", AllIcons.Actions.GC,
                () -> !runner.isRunning(), this::cleanGeneratedArtifacts));
        if (isLegacyWorkspace()) {
            titleActions.add(PracticeActions.action("Import Legacy Attempts",
                    "Import attempts from this legacy practice project", AllIcons.ToolbarDecorator.Import,
                    () -> true, () -> guarded(this::importLegacyAttempts)));
        }

        JPanel header = new JPanel(new GridLayout(0, 1, 4, 4));
        filters.addTo(header);
        count.setFont(JBFont.small());
        count.setForeground(UIUtil.getContextHelpForeground());
        header.add(count);
        add(header, BorderLayout.NORTH);

        exercises.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        exercises.setCellRenderer(new ExerciseCellRenderer(() -> passed));
        exercises.getEmptyText().setText("No exercises match these filters");
        exercises.getAccessibleContext().setAccessibleName("Exercises");
        tabs.addTab("Problem", problem.component());
        tabs.addTab("Study Guide", guide.component());
        JPanel hintPanel = new JPanel(new BorderLayout());
        hintPanel.add(new JBScrollPane(hints), BorderLayout.CENTER);
        JPanel hintActions = new JPanel(new FlowLayout(FlowLayout.LEFT));
        hintActions.add(hintButton.component());
        hintPanel.add(hintActions, BorderLayout.SOUTH);
        tabs.addTab("Hints", hintPanel);
        tabs.addTab("Results", results.component());
        OnePixelSplitter split = new OnePixelSplitter(true, 0.35f);
        split.setFirstComponent(new JBScrollPane(exercises));
        split.setSecondComponent(tabs);
        add(split, BorderLayout.CENTER);

        attempts.getAccessibleContext().setAccessibleName("Saved attempts");
        attempts.setRenderer(PracticeViews.labelled(
                ManagedPracticeWorkspace.Attempt.class, ManagedPracticeWorkspace.Attempt::id));
        JPanel editing = new JPanel(new BorderLayout(4, 0));
        editing.add(new JBLabel("Attempt:"), BorderLayout.WEST);
        editing.add(attempts, BorderLayout.CENTER);
        JPanel attemptButtons = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        button(attemptButtons, "Start / Resume", this::startOrResume);
        button(attemptButtons, "New Attempt", this::newAttempt);
        editing.add(attemptButtons, BorderLayout.EAST);
        toolbar = PracticeActions.runToolbar(this, this);
        JPanel footer = new JPanel(new GridLayout(0, 1, 4, 4));
        footer.add(editing);
        footer.add(toolbar.getComponent());
        footer.add(status);
        add(footer, BorderLayout.SOUTH);

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
        filters.onGuideChange(() -> {
            filter();
            refreshGuide();
            if (filters.shownGuide() != null) {
                tabs.setSelectedIndex(GUIDE_TAB);
            }
        });
        EditorFactory.getInstance().getEventMulticaster().addDocumentListener(new DocumentListener() {
            @Override
            public void documentChanged(@NotNull DocumentEvent event) {
                var file = FileDocumentManager.getInstance().getFile(event.getDocument());
                if (AttemptFiles.isSolution(file, selectedAttemptOrNull())) {
                    status.setText("Edited since displayed result; check again.");
                }
            }
        }, this);
        filter();
    }

    /** The tool window title bar actions: limits, cleanup, and legacy import for legacy projects. */
    List<AnAction> titleActions() {
        return List.copyOf(titleActions);
    }

    private void filter() {
        passed = progress.passedExerciseIds();
        Exercise previous = exercises.getSelectedValue();
        model.clear();
        filters.visible(passed).forEach(model::addElement);
        updateCount();
        if (previous != null && model.contains(previous)) {
            exercises.setSelectedValue(previous, true);
        } else if (!model.isEmpty()) {
            exercises.setSelectedIndex(0);
        }
    }

    private void updateCount() {
        long shownPassed = java.util.stream.IntStream.range(0, model.size())
                .filter(index -> passed.contains(model.get(index).id())).count();
        count.setText(model.size() + " exercises · " + shownPassed + " passed");
    }

    private void onPageEvent(PageEvent event) {
        guarded(() -> {
            switch (event.type()) {
                case SELECT_EXERCISE -> selectExercise(ExerciseCatalog.find(event.id()));
                case SELECT_GUIDE -> filters.selectGuide(event.id());
                case SELECT_TRACK -> filters.selectTrack(event.id());
            }
        });
    }

    private void selectExercise(Exercise exercise) {
        if (!model.contains(exercise)) {
            filters.reveal(exercise);
        }
        exercises.setSelectedValue(exercise, true);
        tabs.setSelectedIndex(PROBLEM_TAB);
    }

    private void showExercise() {
        int generation = ++selectionGeneration;
        Exercise exercise = exercises.getSelectedValue();
        updatingAttempts = true;
        attempts.removeAllItems();
        updatingAttempts = false;
        refreshPages();
        hints.setText("Start or resume an attempt to reveal hints.");
        results.clear();
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
        hints.setText(HintButton.revealedText(exercise.hints(), entry.hintsRevealed));
        status.setText("Attempt " + attempt.id() + ": " + ResultsView.label(entry.status));
        results.showEntry(entry);
        if (!entry.checkedFingerprint.isEmpty()) {
            background("Checking result freshness", () -> workspace.fingerprint(attempt), hash -> {
                if (attempt.equals(selectedAttemptOrNull())
                        && (!hash.equals(entry.checkedFingerprint) || hasUnsavedAttemptFile())) {
                    status.setText("Edited since last check; previous result is stale.");
                    results.markStale("check the current solution again.");
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
            showExercise();
            results.showMessage("Legacy import finished", imported.summary());
            tabs.setSelectedComponent(results.component());
        });
    }

    private void openSolution(ManagedPracticeWorkspace.Attempt attempt) {
        background("Opening solution", () -> AttemptFiles.prepare(project, attempt),
                file -> FileEditorManager.getInstance(project).openFile(file, true));
    }

    @Override
    public boolean canRun() {
        return selectedAttemptOrNull() != null && !runner.isRunning();
    }

    @Override
    public boolean isRunning() {
        return runner.isRunning();
    }

    @Override
    public void runExamples() {
        launch(false, () -> runner.runExamples(selectedAttempt().id()));
    }

    @Override
    public void check() {
        launch(true, () -> runner.check(selectedAttempt().id()));
    }

    @Override
    public void stop() {
        runner.stop();
    }

    @Override
    public void runWithInput(boolean debug) {
        ManagedPracticeWorkspace.Attempt attempt = selectedAttemptOrNull();
        if (attempt == null) {
            error("Start or resume an attempt first.");
            return;
        }
        String trimmed = input.ask(project, ExerciseCatalog.find(attempt.exerciseId()), debug);
        if (trimmed == null) {
            return;
        }
        launch(false, () -> {
            if (debug) {
                runner.debugExamples(attempt.id(), trimmed);
            } else {
                runner.runExamples(attempt.id(), trimmed);
            }
        });
    }

    private void launch(boolean fullCheck, Action action) {
        checking = fullCheck;
        tabs.setSelectedComponent(results.component());
        guarded(action);
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
        hintButton.update(selectedAttemptOrNull() != null);
        toolbar.updateActionsAsync();
    }

    private boolean hasUnsavedAttemptFile() {
        return AttemptFiles.hasUnsavedChanges(selectedAttemptOrNull());
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

    HintButton hintButton() {
        return hintButton;
    }

    private boolean isLegacyWorkspace() {
        return project.getBasePath() != null && PracticeWorkspace.isWorkspace(root());
    }

    private Path root() {
        return Path.of(Objects.requireNonNull(project.getBasePath()));
    }

    private void button(JPanel panel, String title, Action action) {
        JButton button = new JButton(title);
        button.addActionListener(event -> guarded(action));
        panel.add(button);
    }

    private void guarded(Action action) {
        try {
            action.run();
        } catch (IOException | ExecutionException | IllegalArgumentException exception) {
            error(exception.getMessage());
        }
    }

    private <T> void background(String title, BackgroundTasks.IoTask<T> operation, Consumer<T> completed) {
        tasks.run(title, operation, completed);
    }

    private void error(String text) {
        status.setText("Action failed; see Results.");
        results.showError(text);
        NotificationGroupManager.getInstance().getNotificationGroup("Algorithm Practice")
                .createNotification(text, NotificationType.ERROR).notify(project);
    }

    private void showRunOutput(String text) {
        if (disposed) {
            return;
        }
        boolean running = runner.isRunning();
        var attempt = selectedAttemptOrNull();
        Exercise exercise = exercises.getSelectedValue();
        if (running) {
            results.showRunning();
        } else if (checking && attempt != null && exercise != null) {
            results.showStatus(progress.entry(attempt.id(), exercise.id()));
        } else {
            results.showRunFinished(text);
        }
        results.showOutput(text);
        if (hasUnsavedAttemptFile()) {
            results.markStale("there are unsaved changes. Check again after saving.");
        }
        status.setText(running ? "Running..." : "Run finished; see Results.");
        if (!running) {
            passed = progress.passedExerciseIds();
            updateCount();
            exercises.repaint();
            refreshPages();
        }
        updateButtons();
    }

    private void refreshPages() {
        Exercise selected = exercises.getSelectedValue();
        problem.show(new PracticePage.Problem(selected, passed));
        refreshGuide();
    }

    private void refreshGuide() {
        guide.show(new PracticePage.Study(filters.shownGuide(), exercises.getSelectedValue(), passed));
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
}
