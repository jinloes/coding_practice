package com.jinloes.practice_plugin.ui;

import com.intellij.execution.ExecutionException;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.ToggleAction;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.editor.event.DocumentEvent;
import com.intellij.openapi.editor.event.DocumentListener;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.ui.OnePixelSplitter;
import com.intellij.ui.components.ActionLink;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBList;
import com.intellij.ui.components.JBTabbedPane;
import com.intellij.util.ui.JBUI;
import com.jinloes.practice_plugin.app.LegacyImportService;
import com.jinloes.practice_plugin.catalog.ExerciseCatalog;
import com.jinloes.practice_plugin.catalog.ExerciseCatalog.Exercise;
import com.jinloes.practice_plugin.catalog.TrackCatalog;
import com.jinloes.practice_plugin.coach.CoachPrompt;
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
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.IntStream;

final class PracticePanel extends JPanel implements Disposable, PracticeActions.RunControls {
    static final int WIDE_LAYOUT_THRESHOLD_PX = 720;
    private static final int PROBLEM_TAB = 0;
    private static final int GUIDE_TAB = 1;
    private static final int HINTS_TAB = 2;
    private static final int RESULTS_TAB = 3;
    private static final int COACH_TAB = 4;

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
    private final JPanel catalogPane;
    private final OnePixelSplitter splitter;
    private final AttemptBar attemptBar;
    private final RunStatusPresenter runStatus;
    private final PageView problem;
    private final PageView guide;
    private final PageView hints;
    private final PageView coach;
    private final CoachController coachController;
    private final JBTabbedPane tabs = new JBTabbedPane();
    private final ResultsView results;
    private final JBLabel status = new JBLabel("Choose an exercise.");
    private final ActionToolbar toolbar;
    private JPanel footerPanel;
    private final List<AnAction> titleActions = new ArrayList<>();
    private final CustomInputPrompt input = new CustomInputPrompt();
    private Set<String> passed = Set.of();
    private Exercise selectedExercise;
    private boolean catalogVisible = true;
    private boolean wideLayout;
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
        hints = PageView.lazy(this, this::onPageEvent, false);
        results = new ResultsView(PageView.lazy(this, this::onPageEvent, false));
        coach = PageView.lazy(this, this::onPageEvent, false);
        setBorder(JBUI.Borders.empty(8));

        titleActions.add(new ToggleAction(
                "Show Catalog",
                "Show or hide the exercise catalog and filters to maximize reading space",
                AllIcons.Actions.SplitVertically) {
            @Override
            public @NotNull ActionUpdateThread getActionUpdateThread() {
                return ActionUpdateThread.EDT;
            }

            @Override
            public boolean isSelected(@NotNull AnActionEvent event) {
                return catalogVisible;
            }

            @Override
            public void setSelected(@NotNull AnActionEvent event, boolean state) {
                setCatalogVisible(state);
            }
        });
        titleActions.add(PracticeActions.action("Limits", "Edit run and check limits",
                AllIcons.General.Settings, () -> true, () -> guarded(() -> LimitsDialog.edit(project, progress))));
        titleActions.add(PracticeActions.action("AI Coach Settings",
                "Configure Copilot CLI path, model, and timeout for the AI Coach",
                AllIcons.General.GearPlain, () -> true, () -> guarded(this::editCoachSettings)));
        titleActions.add(PracticeActions.action("Clean Generated Artifacts",
                "Remove inactive generated practice artifacts", AllIcons.Actions.GC,
                () -> !runner.isRunning(), this::cleanGeneratedArtifacts));
        if (isLegacyWorkspace()) {
            titleActions.add(PracticeActions.action("Import Legacy Attempts",
                    "Import attempts from this legacy practice project", AllIcons.ToolbarDecorator.Import,
                    () -> true, () -> guarded(this::importLegacyAttempts)));
        }

        exercises = PracticeViews.exerciseList(
                model, () -> passed, () -> guarded(this::startOrResume), filters::resetAll);
        catalogPane = PracticeViews.catalogPane(filters, count, resetFilters, exercises);
        tabs.addTab("Problem", problem.component());
        tabs.addTab("Study Guide", guide.component());
        tabs.addTab("Hints", hints.component());
        tabs.addTab("Results", results.component());
        tabs.addTab("Coach", coach.component());
        splitter = PracticeViews.splitter(catalogPane, tabs);
        add(splitter, BorderLayout.CENTER);

        attemptBar = new AttemptBar(attempt -> AttemptBar.statusOf(progress, attempt),
                this::showAttempt, () -> guarded(this::startOrResume), () -> guarded(this::newAttempt));
        coachController = new CoachController(
                project, progress, coach, exercises::getSelectedValue, attemptBar::selected,
                () -> passed, runner::isRunning, () -> !disposed);
        runStatus = new RunStatusPresenter(project, progress, workspace, attemptBar, results, status, tasks);
        toolbar = PracticeActions.runToolbar(this, this);
        footerPanel = PracticeViews.footer(attemptBar.component(), toolbar.getComponent(), status, false);
        add(footerPanel, BorderLayout.SOUTH);

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent event) {
                applyResponsiveLayout(getWidth());
            }
        });

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
                case REVEAL_HINT -> revealHint();
                case OPEN_COACH -> openCoachTab();
                case COACH_DIAGNOSE -> coachController.ask(CoachPrompt.Mode.DIAGNOSE, "");
                case COACH_TRANSLATE -> coachController.ask(CoachPrompt.Mode.TRANSLATE, "");
                case COACH_COMPARE -> coachController.ask(CoachPrompt.Mode.COMPARE_OPTIMAL, "");
                case COACH_FOLLOW_UP -> coachController.ask(CoachPrompt.Mode.FOLLOW_UP, event.id());
                case COACH_CANCEL -> coachController.cancel();
                case COACH_CLEAR -> coachController.clear();
                case COACH_SETTINGS -> editCoachSettings();
            }
        });
    }

    private void openCoachTab() {
        tabs.setSelectedIndex(COACH_TAB);
        coachController.refresh();
    }

    private void editCoachSettings() {
        if (CoachSettingsDialog.edit(project, progress)) {
            coachController.refresh();
        }
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
        boolean changed = exercise != null && !exercise.equals(selectedExercise);
        if (changed) {
            selectedExercise = exercise;
        }
        results.clear();
        attemptBar.loadFor(exercise, generation, () -> selectionGeneration,
                exercises::getSelectedValue, workspace, progress, tasks);
        refreshGuide();
        updateButtons();
        if (changed) {
            tabs.setSelectedIndex(PROBLEM_TAB);
        }
    }

    private void showAttempt() {
        var attempt = attemptBar.selected();
        Exercise exercise = exercises.getSelectedValue();
        updateButtons();
        if (attempt == null || exercise == null) {
            runStatus.showNoAttempt();
            return;
        }
        progress.getState().selectedAttempts.put(exercise.id(), attempt.id());
        var entry = progress.entry(attempt.id(), exercise.id());
        runStatus.showAttempt(attempt, entry);
        coachController.refreshFreshness(attempt, entry, workspace, tasks);
    }

    boolean hasSelectedExercise() {
        return exercises.getSelectedValue() != null;
    }

    void startOrOpenAttempt() {
        guarded(this::startOrResume);
    }

    private void startOrResume() throws IOException {
        tabs.setSelectedIndex(PROBLEM_TAB);
        attemptBar.startOrResume(project, workspace, progress, tasks, runner::isRunning, exercises::getSelectedValue);
    }

    private void newAttempt() throws IOException {
        tabs.setSelectedIndex(PROBLEM_TAB);
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
        tabs.setTitleAt(HINTS_TAB, HintButton.tabTitle(hasAttempt, revealed, totalHints));
        hints.show(new PracticePage.Hints(exercise, passed, hasAttempt, revealed));
        problem.show(new PracticePage.Problem(
                exercise, passed, attempt != null, canRun()));
        results.setAskCoachEnabled(coachController.isUnlocked());
        coachController.refresh();
        toolbar.updateActionsAsync();
    }

    boolean isCatalogVisible() {
        return catalogVisible;
    }

    void setCatalogVisible(boolean visible) {
        if (catalogVisible == visible) {
            return;
        }
        catalogVisible = visible;
        splitter.setFirstComponent(visible ? catalogPane : null);
        revalidate();
        repaint();
    }

    boolean isHorizontalSplit() {
        return !splitter.isVertical();
    }

    void applyResponsiveLayout(int width) {
        boolean wide = width >= WIDE_LAYOUT_THRESHOLD_PX;
        if (wide == wideLayout) {
            return;
        }
        wideLayout = wide;
        splitter.setOrientation(!wide);
        remove(footerPanel);
        footerPanel = PracticeViews.footer(attemptBar.component(), toolbar.getComponent(), status, wide);
        add(footerPanel, BorderLayout.SOUTH);
        revalidate();
        repaint();
    }

    String hintsTabTitle() {
        return tabs.getTitleAt(HINTS_TAB);
    }

    ResultsView resultsView() {
        return results;
    }

    CoachController coachController() {
        return coachController;
    }

    String statusText() {
        return status.getText();
    }

    void onAttemptEdited() {
        var attempt = attemptBar.selected();
        Exercise exercise = exercises.getSelectedValue();
        runStatus.onAttemptEdited(attempt, exercise);
        coachController.onAttemptEdited(attempt, exercise);
    }

    private void syncActivePageTab() {
        int selected = tabs.getSelectedIndex();
        problem.setActive(selected == PROBLEM_TAB);
        guide.setActive(selected == GUIDE_TAB);
        hints.setActive(selected == HINTS_TAB);
        results.setActive(selected == RESULTS_TAB);
        coach.setActive(selected == COACH_TAB);
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
        runStatus.error(text);
    }

    private void onRunEvent(RunEvent event) {
        if (!disposed) {
            runStatus.onRunEvent(event, this::refreshAfterCheck, this::updateButtons);
        }
    }

    private void refreshAfterCheck() {
        passed = progress.passedExerciseIds();
        updateCount();
        exercises.repaint();
        refreshGuide();
        attemptBar.component().repaint();
        coachController.onCheckFinished(attemptBar.selected(), exercises.getSelectedValue(), workspace, tasks);
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
        coachController.dispose();
        PracticeIdeActions.unregister(project, this);
        runner.removeEventListener(runEvents);
    }

    @FunctionalInterface
    private interface Action {
        void run() throws IOException, ExecutionException;
    }
}
