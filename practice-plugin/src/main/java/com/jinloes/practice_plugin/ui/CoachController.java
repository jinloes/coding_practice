package com.jinloes.practice_plugin.ui;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.jinloes.practice_plugin.catalog.ExerciseCatalog.Exercise;
import com.jinloes.practice_plugin.coach.CoachPrompt;
import com.jinloes.practice_plugin.coach.CopilotCoach;
import com.jinloes.practice_plugin.state.ManagedPracticeProgress;
import com.jinloes.practice_plugin.workspace.ManagedPracticeWorkspace;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Coordinates the post-check AI Coach tab: enforces the post-check unlock policy, reads the learner's
 * current solution (including unsaved editor text), runs coaching requests off the EDT, and prevents
 * stale or cancelled replies from leaking across attempt switches.
 */
final class CoachController {
    private static final Set<String> ELIGIBLE_STATUSES = Set.of(
            "PASSED", "ASSERTION_FAILED", "COMPILATION_FAILED", "RUNTIME_ERROR", "TIMED_OUT");

    @FunctionalInterface
    interface CoachExecutor {
        String run(String prompt, CopilotCoach.Settings settings, BooleanSupplier canceled)
                throws CopilotCoach.CoachException;
    }

    @FunctionalInterface
    interface SourceReader {
        String read(ManagedPracticeWorkspace.Attempt attempt) throws IOException;
    }

    private final Project project;
    private final ManagedPracticeProgress progress;
    private final PageView view;
    private final Supplier<Exercise> selectedExercise;
    private final Supplier<ManagedPracticeWorkspace.Attempt> selectedAttempt;
    private final Supplier<Set<String>> passedIds;
    private final BooleanSupplier runnerRunning;
    private final BooleanSupplier alive;
    private final CoachExecutor executor;
    private final SourceReader sourceReader;
    private final Consumer<Runnable> background;
    private final Consumer<Runnable> uiThread;

    private final AtomicInteger requestGeneration = new AtomicInteger();
    private final AtomicInteger freshnessGeneration = new AtomicInteger();
    private final Map<String, String> errorsByAttempt = new HashMap<>();
    private final Set<String> staleAttempts = new HashSet<>();
    private String activeAttemptId;
    private AtomicBoolean activeCancel;

    CoachController(
            Project project,
            ManagedPracticeProgress progress,
            PageView view,
            Supplier<Exercise> selectedExercise,
            Supplier<ManagedPracticeWorkspace.Attempt> selectedAttempt,
            Supplier<Set<String>> passedIds,
            BooleanSupplier runnerRunning,
            BooleanSupplier alive) {
        this(project, progress, view, selectedExercise, selectedAttempt, passedIds, runnerRunning, alive,
                new CopilotCoach()::run,
                AttemptFiles::readCurrentSource,
                task -> ApplicationManager.getApplication().executeOnPooledThread(task),
                action -> {
                    var app = ApplicationManager.getApplication();
                    if (app == null) {
                        action.run();
                    } else {
                        app.invokeLater(action);
                    }
                });
    }

    CoachController(
            Project project,
            ManagedPracticeProgress progress,
            PageView view,
            Supplier<Exercise> selectedExercise,
            Supplier<ManagedPracticeWorkspace.Attempt> selectedAttempt,
            Supplier<Set<String>> passedIds,
            BooleanSupplier runnerRunning,
            BooleanSupplier alive,
            CoachExecutor executor,
            SourceReader sourceReader,
            Consumer<Runnable> background,
            Consumer<Runnable> uiThread) {
        this.project = project;
        this.progress = progress;
        this.view = view;
        this.selectedExercise = selectedExercise;
        this.selectedAttempt = selectedAttempt;
        this.passedIds = passedIds;
        this.runnerRunning = runnerRunning;
        this.alive = alive;
        this.executor = executor;
        this.sourceReader = sourceReader;
        this.background = background;
        this.uiThread = uiThread;
    }

    static boolean isEligibleStatus(String status) {
        return status != null && ELIGIBLE_STATUSES.contains(status);
    }

    boolean isUnlocked() {
        return isUnlocked(selectedAttempt.get(), selectedExercise.get());
    }

    boolean isUnlocked(ManagedPracticeWorkspace.Attempt attempt, Exercise exercise) {
        if (attempt == null || exercise == null || runnerRunning.getAsBoolean()) {
            return false;
        }
        ManagedPracticeProgress.Entry entry = progress.entry(attempt.id(), exercise.id());
        return !entry.checkedFingerprint.isEmpty() && isEligibleStatus(entry.status);
    }

    String lockReason(ManagedPracticeWorkspace.Attempt attempt, Exercise exercise) {
        if (attempt == null || exercise == null) {
            return "Start or resume an attempt and run Check to unlock the AI Coach.";
        }
        if (runnerRunning.getAsBoolean()) {
            return "Wait for the active check to finish before asking the AI Coach.";
        }
        ManagedPracticeProgress.Entry entry = progress.entry(attempt.id(), exercise.id());
        if ("CANCELLED".equals(entry.status) || "RUNNER_ERROR".equals(entry.status)) {
            return "The last check did not complete. Run Check again to unlock the AI Coach.";
        }
        if (entry.checkedFingerprint.isEmpty() || !isEligibleStatus(entry.status)) {
            return "Run Check on this attempt at least once to unlock the AI Coach.";
        }
        return "";
    }

    void ask(CoachPrompt.Mode mode, String followUp) {
        Exercise exercise = selectedExercise.get();
        ManagedPracticeWorkspace.Attempt attempt = selectedAttempt.get();
        if (!isUnlocked(attempt, exercise)) {
            if (attempt != null) {
                errorsByAttempt.put(attempt.id(), lockReason(attempt, exercise));
            }
            refresh();
            return;
        }
        String trimmedFollowUp = followUp == null ? "" : followUp.strip();
        if (mode == CoachPrompt.Mode.FOLLOW_UP && trimmedFollowUp.isEmpty()) {
            errorsByAttempt.put(attempt.id(), "Enter a follow-up question first.");
            refresh();
            return;
        }
        if (activeAttemptId != null) {
            errorsByAttempt.put(attempt.id(),
                    "Another AI Coach request is already running. Wait for it or cancel it.");
            refresh();
            return;
        }

        String sourceCode;
        try {
            sourceCode = sourceReader.read(attempt);
        } catch (IOException exception) {
            errorsByAttempt.put(attempt.id(), "Could not read your solution file: " + exception.getMessage());
            refresh();
            return;
        }

        ManagedPracticeProgress.Entry entry = progress.entry(attempt.id(), exercise.id());
        List<CoachPrompt.Turn> history = progress.coachHistory(attempt.id(), exercise.id()).stream()
                .map(turn -> new CoachPrompt.Turn(turn.role, turn.text))
                .toList();
        String prompt = CoachPrompt.build(new CoachPrompt.Request(
                exercise, mode, sourceCode, entry.status, entry.details, entry.complexity,
                history, trimmedFollowUp));

        ManagedPracticeProgress.Data data = progress.getState();
        int timeout = ManagedPracticeProgress.validCoachTimeout(data.coachTimeoutSeconds)
                ? data.coachTimeoutSeconds : ManagedPracticeProgress.DEFAULT_COACH_TIMEOUT_SECONDS;
        CopilotCoach.Settings settings = new CopilotCoach.Settings(data.coachCliPath, data.coachModel, timeout);

        String attemptId = attempt.id();
        String exerciseId = exercise.id();
        String userPromptLabel = mode == CoachPrompt.Mode.FOLLOW_UP ? trimmedFollowUp : mode.label();
        int generation = requestGeneration.incrementAndGet();
        AtomicBoolean cancelFlag = new AtomicBoolean();
        activeAttemptId = attemptId;
        activeCancel = cancelFlag;
        errorsByAttempt.remove(attemptId);
        refresh();

        background.accept(() -> {
            String reply = null;
            String failure = null;
            try {
                reply = executor.run(prompt, settings, cancelFlag::get);
            } catch (CopilotCoach.CoachException exception) {
                failure = exception.getMessage();
            } catch (RuntimeException exception) {
                failure = exception.getMessage() == null || exception.getMessage().isBlank()
                        ? exception.getClass().getSimpleName() : exception.getMessage();
            }
            String finalReply = reply;
            String finalFailure = failure;
            uiThread.accept(() -> finish(attemptId, exerciseId, userPromptLabel, generation,
                    cancelFlag, finalReply, finalFailure));
        });
    }

    private void finish(
            String attemptId,
            String exerciseId,
            String userPromptLabel,
            int generation,
            AtomicBoolean cancelFlag,
            String reply,
            String failure) {
        if (!alive.getAsBoolean() || (project != null && project.isDisposed())) {
            return;
        }
        if (generation != requestGeneration.get() || cancelFlag.get()) {
            return;
        }
        activeAttemptId = null;
        activeCancel = null;
        if (reply != null) {
            progress.appendCoachTurn(attemptId, exerciseId, "user", userPromptLabel);
            progress.appendCoachTurn(attemptId, exerciseId, "assistant", reply);
            errorsByAttempt.remove(attemptId);
        } else {
            errorsByAttempt.put(attemptId, failure == null ? "AI Coach request failed." : failure);
        }
        refresh();
    }

    void cancel() {
        String runningFor = activeAttemptId;
        if (activeCancel != null) {
            activeCancel.set(true);
        }
        requestGeneration.incrementAndGet();
        activeAttemptId = null;
        activeCancel = null;
        if (runningFor != null) {
            errorsByAttempt.put(runningFor, "AI Coach request cancelled.");
        }
        refresh();
    }

    void clear() {
        Exercise exercise = selectedExercise.get();
        ManagedPracticeWorkspace.Attempt attempt = selectedAttempt.get();
        if (!isUnlocked(attempt, exercise)) {
            refresh();
            return;
        }
        if (attempt.id().equals(activeAttemptId)) {
            if (activeCancel != null) {
                activeCancel.set(true);
            }
            requestGeneration.incrementAndGet();
            activeAttemptId = null;
            activeCancel = null;
        }
        progress.clearCoachHistory(attempt.id(), exercise.id());
        errorsByAttempt.remove(attempt.id());
        refresh();
    }

    void dispose() {
        if (activeCancel != null) {
            activeCancel.set(true);
        }
        requestGeneration.incrementAndGet();
        activeAttemptId = null;
        activeCancel = null;
    }

    void refresh() {
        view.show(currentPage());
    }

    void onAttemptEdited(ManagedPracticeWorkspace.Attempt attempt, Exercise exercise) {
        if (attempt == null || exercise == null) {
            return;
        }
        ManagedPracticeProgress.Entry entry = progress.entry(attempt.id(), exercise.id());
        if (!entry.checkedFingerprint.isEmpty()) {
            freshnessGeneration.incrementAndGet();
            staleAttempts.add(attempt.id());
            refresh();
        }
    }

    void onCheckFinished(
            ManagedPracticeWorkspace.Attempt attempt,
            Exercise exercise,
            ManagedPracticeWorkspace workspace,
            BackgroundTasks tasks) {
        freshnessGeneration.incrementAndGet();
        if (attempt == null || exercise == null) {
            return;
        }
        staleAttempts.remove(attempt.id());
        ManagedPracticeProgress.Entry entry = progress.entry(attempt.id(), exercise.id());
        refresh();
        refreshFreshness(attempt, entry, workspace, tasks);
    }

    void refreshFreshness(
            ManagedPracticeWorkspace.Attempt attempt,
            ManagedPracticeProgress.Entry entry,
            ManagedPracticeWorkspace workspace,
            BackgroundTasks tasks) {
        if (attempt == null || entry == null || entry.checkedFingerprint.isEmpty()) {
            return;
        }
        String checkedFingerprint = entry.checkedFingerprint;
        int generation = freshnessGeneration.incrementAndGet();
        tasks.run("Checking AI Coach freshness", () -> workspace.fingerprint(attempt), hash -> {
            if (!alive.getAsBoolean() || (project != null && project.isDisposed())
                    || generation != freshnessGeneration.get()) {
                return;
            }
            boolean isStale = !hash.equals(checkedFingerprint) || AttemptFiles.hasUnsavedChanges(attempt);
            if (isStale) {
                staleAttempts.add(attempt.id());
            } else {
                staleAttempts.remove(attempt.id());
            }
            if (attempt.equals(selectedAttempt.get())) {
                refresh();
            }
        });
    }

    PracticePage.Coach currentPage() {
        Exercise exercise = selectedExercise.get();
        ManagedPracticeWorkspace.Attempt attempt = selectedAttempt.get();
        boolean unlocked = isUnlocked(attempt, exercise);
        String reason = unlocked ? "" : lockReason(attempt, exercise);
        boolean running = attempt != null && attempt.id().equals(activeAttemptId);
        boolean stale = unlocked && (staleAttempts.contains(attempt.id()) || AttemptFiles.hasUnsavedChanges(attempt));
        String summary = "";
        List<PracticePage.CoachTurn> turns = List.of();
        String error = "";
        if (attempt != null && exercise != null) {
            ManagedPracticeProgress.Entry entry = progress.entry(attempt.id(), exercise.id());
            summary = checkSummary(entry);
            turns = progress.coachHistory(attempt.id(), exercise.id()).stream()
                    .map(turn -> new PracticePage.CoachTurn(turn.role, turn.text, turn.createdAt))
                    .toList();
            error = errorsByAttempt.getOrDefault(attempt.id(), "");
        }
        String model = progress.getState().coachModel;
        String modelLabel = model == null || model.isBlank() ? "CLI default" : model.strip();
        String attemptId = attempt == null ? null : attempt.id();
        return new PracticePage.Coach(
                exercise, attemptId, passedIds.get(), unlocked, reason, running, summary, stale, error, modelLabel, turns);
    }

    private static String checkSummary(ManagedPracticeProgress.Entry entry) {
        if (entry.checkedFingerprint.isEmpty()) {
            return "Not checked yet";
        }
        String label = ResultsView.label(entry.status);
        if (entry.tests > 0) {
            int passed = Math.max(0, entry.tests - entry.failures);
            label += " (" + passed + "/" + entry.tests + " tests)";
        }
        if ("PASSED".equals(entry.status) && entry.complexity != null && !entry.complexity.isBlank()) {
            label += " · " + entry.complexity.strip();
        }
        return label;
    }
}
