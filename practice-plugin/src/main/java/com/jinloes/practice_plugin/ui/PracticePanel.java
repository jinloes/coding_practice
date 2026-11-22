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
import com.intellij.openapi.project.Project;
import com.intellij.ui.components.ActionLink;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBList;
import com.intellij.ui.components.JBTabbedPane;
import com.intellij.util.ui.JBUI;
import com.jinloes.practice_plugin.app.LegacyImportService;
import com.jinloes.practice_plugin.catalog.ExerciseCatalog;
import com.jinloes.practice_plugin.catalog.ExerciseCatalog.Exercise;
import com.jinloes.practice_plugin.catalog.TrackCatalog;
import com.jinloes.practice_plugin.run.PracticeRunner;
import com.jinloes.practice_plugin.run.RunEvent;
import com.jinloes.practice_plugin.state.ManagedPracticeProgress;
import com.jinloes.practice_plugin.state.PracticeProgress;
import com.jinloes.practice_plugin.workspace.ManagedPracticeWorkspace;
import com.jinloes.practice_plugin.workspace.PracticeModuleWorkspace;
import com.jinloes.practice_plugin.workspace.PracticeWorkspace;
import org.jetbrains.annotations.NotNull;

import javax.swing.DefaultListModel;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.IntStream;

final class PracticePanel extends JPanel implements Disposable, PracticeActions.RunControls {
    private static final int PROBLEM_TAB = 0;
    private static final int GUIDE_TAB = 1;
    private static final int HINTS_TAB = 2;
    private static final String LOCKED_HINTS_HTML = "<p>Start or resume an attempt to reveal hints.</p>";

    private final Project project;
    private final ManagedPracticeProgress progress = ManagedPracticeProgress.get();
    private final ManagedPracticeWorkspace workspace = ManagedPracticeWorkspace.get();
    private final LegacyImportService legacyImport = new LegacyImportService(workspace);
    private final PracticeRunner runner;
    private final BackgroundTasks tasks;
    private final DefaultListModel<Exercise> model = new DefaultListModel<>();
    private final JBList<Exercise> exercises;
    private final ExerciseFilters filters = new ExerciseFilters();
    private final JBLabel count = new JBLabel();
    private final ActionLink resetFilters = new ActionLink(
            "Reset filters", (ActionListener) _ -> filters.resetAll());
    private final AttemptBar attemptBar;
    private final PageView problem;
    private final PageView guide;
    private final JBTabbedPane tabs = new JBTabbedPane();
    private String hintsHtml = LOCKED_HINTS_HTML;
    private final JEditorPane hints = PracticeViews.htmlPane(() -> hintsHtml);
    private final ResultsView results = new ResultsView();
    private final HintButton hintButton = new HintButton(() -> guarded(this::revealHint));
    private final JBLabel status = new JBLabel("Choose an exercise.");
    private final ActionToolbar toolbar;
    private final List<AnAction> titleActions = new ArrayList<>();
    private final CustomInputPrompt input = new CustomInputPrompt();
    private Set<String> passed = Set.of();
    private boolean disposed;
    private int selectionGeneration;

    /** Removed on {@link #dispose()} from the project-scoped runner. */
    private final Consumer<RunEvent> runEvents = this::onRunEvent;

    PracticePanel(Project project) {
        super(new BorderLayout(0, 6));
        this.project = project;
        runner = project.getService(PracticeRunner.class);
        tasks = new BackgroundTasks(project, () -> !disposed, this::error, this::onTaskCancelled);
        problem = PageView.lazy(this, this::onPageEvent, true);
        guide = PageView.lazy(this, this::onPageEvent, false);
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

        add(PracticeViews.header(filters, count, resetFilters), BorderLayout.NORTH);
        exercises = PracticeViews.exerciseList(
                model, () -> passed, () -> guarded(this::startOrResume), filters::resetAll);
        tabs.addTab("Problem", problem.component());
        tabs.addTab("Study Guide", guide.component());
        tabs.addTab("Hints", PracticeViews.hintTab(hints, hintButton));
        tabs.addTab("Results", results.component());
        add(PracticeViews.splitter(exercises, tabs), BorderLayout.CENTER);

        attemptBar = new AttemptBar(attempt -> AttemptBar.statusOf(progress, attempt),
                this::showAttempt, () -> guarded(this::startOrResume), () -> guarded(this::newAttempt));
        toolbar = PracticeActions.runToolbar(this, this);
        add(PracticeViews.column(attemptBar.component(), toolbar.getComponent(), status), BorderLayout.SOUTH);

        PracticeIdeActions.register(project, this);
        runner.addEventListener(runEvents);
        exercises.addListSelectionListener(event -> {
            if (!event.getValueIsAdjusting()) {
                showExercise();
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
        tabs.addChangeListener(_ -> syncActivePageTab());
        EditorFactory.getInstance().getEventMulticaster().addDocumentListener(new DocumentListener() {
            @Override
            public void documentChanged(@NotNull DocumentEvent event) {
                if (AttemptFiles.isSolution(FileDocumentManager.getInstance().getFile(event.getDocument()),
                        attemptBar.selected())) {
                    onAttemptEdited();
                }
            }
        }, this);
        filter();
    }

    List<AnAction> titleActions() {
        return List.copyOf(titleActions);
    }

    JComponent preferredFocusedComponent() {
        return exercises;
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
        long shownPassed = IntStream.range(0, model.size())
                .filter(index -> passed.contains(model.get(index).id())).count();
        count.setText(model.size() + " exercises · " + shownPassed + " passed");
        boolean active = filters.hasActiveFilters();
        resetFilters.setEnabled(active);
        resetFilters.setVisible(active);
    }

    private void onPageEvent(PageEvent event) {
        guarded(() -> {
            switch (event.type()) {
                case SELECT_EXERCISE -> selectExercise(ExerciseCatalog.find(event.id()));
                case SELECT_GUIDE -> filters.openGuide(event.id());
                case SELECT_TRACK -> filters.openTrack(event.id());
                case CLEAR_FILTERS -> filters.resetAll();
                case START_OR_OPEN -> startOrResume();
                case RUN_EXAMPLES -> runExamples();
                case RUN_SAMPLE -> runSample();
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
        setHintsHtml(LOCKED_HINTS_HTML);
        results.clear();
        attemptBar.loadFor(exercise, generation, () -> selectionGeneration,
                exercises::getSelectedValue, workspace, progress, tasks);
        refreshGuide();
        updateButtons();
    }

    private void showAttempt() {
        var attempt = attemptBar.selected();
        Exercise exercise = exercises.getSelectedValue();
        updateButtons();
        if (attempt == null || exercise == null) {
            setHintsHtml(LOCKED_HINTS_HTML);
            status.setText("Choose Start Attempt to create your first managed attempt.");
            return;
        }
        progress.getState().selectedAttempts.put(exercise.id(), attempt.id());
        var entry = progress.entry(attempt.id(), exercise.id());
        setHintsHtml(HintButton.revealedHtml(exercise.hints(), entry.hintsRevealed));
        status.setText("Attempt " + attempt.id() + ": " + ResultsView.label(entry.status));
        results.showEntry(entry);
        if (!entry.checkedFingerprint.isEmpty()) {
            tasks.run("Checking result freshness", () -> workspace.fingerprint(attempt), hash -> {
                if (attempt.equals(attemptBar.selected())
                        && (!hash.equals(entry.checkedFingerprint) || AttemptFiles.hasUnsavedChanges(attempt))) {
                    status.setText("Edited since last check; previous result is stale.");
                    results.markStale("check the current solution again.");
                }
            });
        }
    }

    private void setHintsHtml(String body) {
        hintsHtml = body;
        PracticeViews.setHtmlBody(hints, body);
    }

    boolean hasSelectedExercise() {
        return exercises.getSelectedValue() != null;
    }

    void startOrOpenAttempt() {
        guarded(this::startOrResume);
    }

    private void startOrResume() throws IOException {
        attemptBar.startOrResume(project, workspace, progress, tasks, runner::isRunning, exercises::getSelectedValue);
    }

    private void newAttempt() throws IOException {
        attemptBar.newAttempt(project, workspace, progress, tasks, runner::isRunning, exercises::getSelectedValue);
    }

    private void runSample() {
        launch(() -> {
            var attempt = attemptBar.selectedOrThrow();
            runner.runExamples(attempt.id(), ExerciseCatalog.find(attempt.exerciseId()).sampleInput());
        });
    }

    private void importLegacyAttempts() throws IOException {
        if (!isLegacyWorkspace()) {
            throw new IOException("The current project is not a marked legacy practice project.");
        }
        Path legacyRoot = Path.of(Objects.requireNonNull(project.getBasePath())).toRealPath();
        tasks.run("Importing legacy attempts", () -> legacyImport.importLegacy(
                legacyRoot, PracticeProgress.get(project), progress), imported -> {
            showExercise();
            results.showMessage("Legacy import finished", imported.summary());
            tabs.setSelectedComponent(results.component());
        });
    }

    @Override
    public boolean canRun() {
        return attemptBar.selected() != null && !runner.isRunning();
    }

    @Override
    public boolean isRunning() {
        return runner.isRunning();
    }

    @Override
    public void runExamples() {
        launch(() -> runner.runExamples(attemptBar.selectedOrThrow().id()));
    }

    @Override
    public void debugExamples() {
        launch(() -> runner.debugExamples(attemptBar.selectedOrThrow().id()));
    }

    @Override
    public void check() {
        launch(() -> runner.check(attemptBar.selectedOrThrow().id()));
    }

    @Override
    public void stop() {
        runner.stop();
    }

    @Override
    public void runWithInput(boolean debug) {
        ManagedPracticeWorkspace.Attempt attempt = attemptBar.selected();
        if (attempt == null) {
            error("Start or resume an attempt first.");
            return;
        }
        String trimmed = input.ask(project, ExerciseCatalog.find(attempt.exerciseId()), debug);
        if (trimmed == null) {
            return;
        }
        launch(() -> {
            if (debug) {
                runner.debugExamples(attempt.id(), trimmed);
            } else {
                runner.runExamples(attempt.id(), trimmed);
            }
        });
    }

    private void launch(Action action) {
        tabs.setSelectedComponent(results.component());
        guarded(action);
        updateButtons();
    }

    private void revealHint() throws IOException {
        var attempt = attemptBar.selectedOrThrow();
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
        var attempt = attemptBar.selected();
        Exercise exercise = exercises.getSelectedValue();
        int totalHints = exercise == null ? 3 : exercise.hints().size();
        int revealed = attempt == null || exercise == null
                ? 0 : progress.entry(attempt.id(), exercise.id()).hintsRevealed;
        boolean hasAttempt = attempt != null && exercise != null;
        hintButton.update(hasAttempt, revealed, totalHints);
        tabs.setTitleAt(HINTS_TAB, HintButton.tabTitle(hasAttempt, revealed, totalHints));
        problem.show(new PracticePage.Problem(
                exercise, passed, attempt != null, canRun()));
        toolbar.updateActionsAsync();
    }

    HintButton hintButton() {
        return hintButton;
    }

    ResultsView resultsView() {
        return results;
    }

    String statusText() {
        return status.getText();
    }

    void onAttemptEdited() {
        var attempt = attemptBar.selected();
        Exercise exercise = exercises.getSelectedValue();
        if (attempt == null || exercise == null) {
            return;
        }
        var entry = progress.entry(attempt.id(), exercise.id());
        if (!entry.checkedFingerprint.isEmpty() || results.hasDisplayedResult()) {
            status.setText("Edited since displayed result; check again.");
            results.markStale("solution was edited; run or check again.");
        }
    }

    private void syncActivePageTab() {
        int selected = tabs.getSelectedIndex();
        problem.setActive(selected == PROBLEM_TAB);
        guide.setActive(selected == GUIDE_TAB);
    }

    private boolean isLegacyWorkspace() {
        String base = project.getBasePath();
        return base != null && PracticeWorkspace.isWorkspace(Path.of(base));
    }

    private void guarded(Action action) {
        try {
            action.run();
        } catch (IOException | ExecutionException | IllegalArgumentException exception) {
            error(exception.getMessage());
        }
    }

    private void onTaskCancelled() {
        if (attemptBar != null) {
            attemptBar.resetBusy();
        }
    }

    private void error(String text) {
        if (attemptBar != null) {
            attemptBar.resetBusy();
        }
        status.setText("Action failed; see Results.");
        results.showError(text);
        NotificationGroupManager.getInstance().getNotificationGroup("Algorithm Practice")
                .createNotification(text, NotificationType.ERROR).notify(project);
    }

    private void onRunEvent(RunEvent event) {
        if (disposed) {
            return;
        }
        switch (event) {
            case RunEvent.Started started -> {
                if (started.attemptId().isEmpty() || attemptBar.matches(started.attemptId())) {
                    results.showRunning();
                    results.showOutput(started.text());
                    if (AttemptFiles.hasUnsavedChanges(attemptBar.selected())) {
                        results.markStale("there are unsaved changes. Check again after saving.");
                    }
                }
                status.setText("Running...");
                updateButtons();
            }
            case RunEvent.FailedToStart failed -> {
                error(failed.text());
                updateButtons();
            }
            case RunEvent.CheckFinished finished -> {
                passed = progress.passedExerciseIds();
                updateCount();
                exercises.repaint();
                refreshGuide();
                attemptBar.component().repaint();
                updateButtons();
                var entry = progress.entry(finished.attemptId(), finished.exerciseId());
                if (attemptBar.matches(finished.attemptId())) {
                    results.showStatus(entry);
                    showSelectedRunFinished(finished.text());
                } else {
                    status.setText("Check finished for " + finished.attemptId() + ": "
                            + ResultsView.label(entry.status));
                }
            }
            case RunEvent.ExampleFinished finished -> {
                updateButtons();
                if (finished.attemptId().isEmpty() || attemptBar.matches(finished.attemptId())) {
                    results.showRunFinished(finished.text());
                    showSelectedRunFinished(finished.text());
                } else {
                    status.setText("Example run finished for " + finished.attemptId() + ".");
                }
            }
        }
    }

    private void showSelectedRunFinished(String outputText) {
        results.showOutput(outputText);
        if (AttemptFiles.hasUnsavedChanges(attemptBar.selected())) {
            results.markStale("there are unsaved changes. Check again after saving.");
        }
        status.setText("Run finished; see Results.");
    }

    private void refreshGuide() {
        var shown = filters.shownGuide();
        tabs.setTitleAt(GUIDE_TAB,
                shown != null && TrackCatalog.isTrack(shown.id()) ? "Practice Track" : "Study Guide");
        guide.show(new PracticePage.Study(shown, exercises.getSelectedValue(), passed));
    }

    @Override
    public void dispose() {
        disposed = true;
        PracticeIdeActions.unregister(project, this);
        runner.removeEventListener(runEvents);
    }

    @FunctionalInterface
    private interface Action {
        void run() throws IOException, ExecutionException;
    }
}
