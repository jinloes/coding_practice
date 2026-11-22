package com.jinloes.system_design_plugin.web;

import com.jinloes.system_design_plugin.catalog.DesignCatalog;
import com.jinloes.system_design_plugin.catalog.DesignCatalog.Problem;
import com.jinloes.system_design_plugin.interview.DiagramSummary;
import com.jinloes.system_design_plugin.interview.InterviewFeedback;
import com.jinloes.system_design_plugin.interview.InterviewPrompt;
import com.jinloes.system_design_plugin.state.DesignProgress;
import com.jinloes.system_design_plugin.workspace.DesignWorkspace;
import com.jinloes.system_design_plugin.workspace.DesignWorkspace.Attempt;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.time.Instant;
import java.time.ZoneId;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * Non-UI state coordinator for the practice tool window: binds workspace operations, progress mutations,
 * prompt preparation, review persistence, and view assembly outside the Swing/JCEF shell.
 */
public final class DesignSession {
    private final DesignWorkspace workspace;
    private final DesignProgress progress;
    private volatile String selectedProblem = "";
    private volatile String error = "";

    public DesignSession(DesignWorkspace workspace, DesignProgress progress) {
        this.workspace = Objects.requireNonNull(workspace, "workspace");
        this.progress = Objects.requireNonNull(progress, "progress");
    }

    public record PreparedReview(Attempt attempt, Problem problem, String targetLevel,
                                 String fingerprint, String followUpAnswers, String prompt) {
        public PreparedReview {
            Objects.requireNonNull(attempt, "attempt");
            Objects.requireNonNull(problem, "problem");
            targetLevel = DesignCatalog.normalizeLevel(targetLevel);
            Objects.requireNonNull(fingerprint, "fingerprint");
            followUpAnswers = Objects.requireNonNullElse(followUpAnswers, "");
            Objects.requireNonNull(prompt, "prompt");
        }
    }

    public String selectedProblem() {
        return selectedProblem;
    }

    public String error() {
        return error;
    }

    public void setError(String message) {
        error = message == null ? "" : message;
    }

    public void clearError() {
        error = "";
    }

    public void selectProblem(String problemId) {
        error = "";
        if (problemId != null && DesignCatalog.exists(problemId)) {
            selectedProblem = problemId;
        }
    }

    public Attempt createAttempt(String problemId) throws IOException {
        error = "";
        Problem problem = DesignCatalog.find(problemId);
        Attempt attempt = workspace.create(problem);
        progress.entry(attempt.id(), problem.id());
        progress.select(problem.id(), attempt.id());
        selectedProblem = problem.id();
        return attempt;
    }

    public Attempt selectAttempt(String attemptId) throws IOException {
        error = "";
        Attempt attempt = workspace.read(attemptId);
        progress.select(attempt.problemId(), attempt.id());
        return attempt;
    }

    public Attempt readAttempt(String attemptId) throws IOException {
        return workspace.read(attemptId);
    }

    public Attempt renameAttempt(String attemptId, String label) throws IOException {
        error = "";
        Attempt attempt = workspace.read(attemptId);
        progress.renameAttempt(attempt.id(), attempt.problemId(), label);
        return attempt;
    }

    public Attempt revealHint(String attemptId) throws IOException {
        error = "";
        Attempt attempt = workspace.read(attemptId);
        progress.revealHint(attempt.id(), attempt.problemId());
        return attempt;
    }

    public void selectLevel(String level) {
        error = "";
        progress.setTargetLevel(level);
    }

    public boolean deleteAttempt(String attemptId, boolean reviewRunning, Consumer<Attempt> beforeDelete)
            throws IOException {
        Objects.requireNonNull(beforeDelete, "beforeDelete");
        if (reviewRunning) {
            error = "Finish or cancel the running review before deleting an attempt.";
            return false;
        }
        error = "";
        Attempt attempt = workspace.read(attemptId);
        beforeDelete.accept(attempt);
        workspace.delete(attempt.id());
        progress.removeAttempt(attempt.id());
        return true;
    }

    public PreparedReview preparePrompt(Attempt attempt, String targetLevel, String followUpAnswers)
            throws IOException {
        Attempt stored = workspace.read(attempt.id());
        Problem problem = DesignCatalog.find(stored.problemId());
        String normalizedLevel = DesignCatalog.normalizeLevel(targetLevel);
        String fingerprint = workspace.fingerprint(stored);
        String notes = workspace.readNotes(stored);
        String summary = DiagramSummary.summarize(workspace.readDiagram(stored));
        String answers = followUpAnswers == null ? "" : followUpAnswers;
        List<String> priorFollowUps = answers.isBlank() ? List.of() : latestParsedFollowUpQuestions(stored);
        String prompt = InterviewPrompt.build(problem, normalizedLevel, notes, summary, priorFollowUps, answers);
        return new PreparedReview(stored, problem, normalizedLevel, fingerprint, answers, prompt);
    }

    public InterviewFeedback.Stored recordCompletedReview(
            PreparedReview prepared, InterviewFeedback feedback, String model) throws IOException {
        String round = prepared.followUpAnswers().isBlank()
                ? InterviewFeedback.ROUND_INITIAL
                : InterviewFeedback.ROUND_FOLLOW_UP;
        InterviewFeedback.Stored stored = new InterviewFeedback.Stored(
                feedback, prepared.fingerprint(), model, Instant.now().toString(),
                prepared.targetLevel(), round, prepared.followUpAnswers());
        workspace.saveFeedback(prepared.attempt(), stored.toJson());
        String verdict = feedback.parsed() ? feedback.verdict() : "";
        progress.recordReview(
                prepared.attempt().id(), prepared.problem().id(), verdict, dimensionScoreMap(feedback));
        return stored;
    }

    public PageState.View view(boolean running, ZoneId zone) {
        return view(selectedProblem, running, error, zone, workspaceStore());
    }

    public PageState.View view(String problemId, boolean running, String currentError, ZoneId zone) {
        return view(problemId, running, currentError, zone, workspaceStore());
    }

    PageState.View view(String problemId, boolean running, String currentError, ZoneId zone,
                        ToolWindowView.AttemptStore store) {
        backfillProblemAttemptScores(problemId, store);
        return ToolWindowView.build(
                problemId,
                running,
                currentError,
                progress.bestVerdicts(),
                progress.problemDimensionScores(),
                progress.targetLevel(),
                progress::selectedAttempt,
                progress::attemptLabel,
                progress::hintsRevealed,
                store,
                zone);
    }

    private ToolWindowView.AttemptStore workspaceStore() {
        return new ToolWindowView.AttemptStore() {
            @Override
            public List<ToolWindowView.AttemptEntry> attempts(String problemId) throws IOException {
                return workspace.attempts(problemId).stream()
                        .map(attempt -> new ToolWindowView.AttemptEntry(attempt.id(), attemptCreatedTime(attempt)))
                        .toList();
            }

            @Override
            public List<InterviewFeedback.Stored> feedbackHistory(String attemptId) throws IOException {
                return workspace.feedbackHistory(attemptId, InterviewFeedback.Stored::fromJson);
            }

            @Override
            public String fingerprint(String attemptId) throws IOException {
                return workspace.fingerprint(attemptId);
            }
        };
    }

    private FileTime attemptCreatedTime(Attempt attempt) {
        long recorded = progress.createdAtMillis(attempt.id());
        return recorded > 0L ? FileTime.fromMillis(recorded) : filesystemCreationTime(attempt.directory());
    }

    static FileTime filesystemCreationTime(Path directory) {
        try {
            return Files.readAttributes(directory, BasicFileAttributes.class, LinkOption.NOFOLLOW_LINKS)
                    .creationTime();
        } catch (IOException exception) {
            return FileTime.fromMillis(0);
        }
    }

    private List<String> latestParsedFollowUpQuestions(Attempt attempt) throws IOException {
        for (InterviewFeedback.Stored stored : workspace.feedbackHistory(attempt, InterviewFeedback.Stored::fromJson)) {
            if (stored.feedback().parsed() && !stored.feedback().followUpQuestions().isEmpty()) {
                return stored.feedback().followUpQuestions();
            }
        }
        return List.of();
    }

    private void backfillProblemAttemptScores(String problemId, ToolWindowView.AttemptStore store) {
        if (problemId == null || !DesignCatalog.exists(problemId)) {
            return;
        }
        try {
            for (ToolWindowView.AttemptEntry attempt : store.attempts(problemId)) {
                if (!progress.needsDimensionBackfill(attempt.id())) {
                    continue;
                }
                InterviewFeedback chosen = selectBackfillFeedback(store.feedbackHistory(attempt.id()));
                if (chosen == null
                        || !progress.backfillDimensionScores(
                                attempt.id(), problemId, chosen.verdict(), dimensionScoreMap(chosen))) {
                    progress.markDimensionBackfillChecked(attempt.id());
                }
            }
        } catch (IOException ignored) {
            // Listing or reading errors are surfaced by ToolWindowView.build below.
        }
    }

    static InterviewFeedback selectBackfillFeedback(List<InterviewFeedback.Stored> history) {
        InterviewFeedback strongest = null;
        for (InterviewFeedback.Stored stored : history) {
            InterviewFeedback candidate = stored.feedback();
            if (!candidate.parsed()
                    || !DesignCatalog.VERDICTS.contains(candidate.verdict())
                    || dimensionScoreMap(candidate).isEmpty()) {
                continue;
            }
            if (strongest == null || isStrongerFeedback(candidate, strongest)) {
                strongest = candidate;
            }
        }
        return strongest;
    }

    private static boolean isStrongerFeedback(InterviewFeedback candidate, InterviewFeedback incumbent) {
        int candidateRank = DesignCatalog.VERDICTS.indexOf(candidate.verdict());
        int incumbentRank = DesignCatalog.VERDICTS.indexOf(incumbent.verdict());
        if (candidateRank != incumbentRank) {
            return candidateRank < incumbentRank;
        }
        return averageFeedbackScore(candidate) > averageFeedbackScore(incumbent);
    }

    private static double averageFeedbackScore(InterviewFeedback feedback) {
        int sum = 0;
        for (InterviewFeedback.Dimension dimension : feedback.dimensions()) {
            sum += dimension.score();
        }
        return (double) sum / feedback.dimensions().size();
    }

    static Map<String, Integer> dimensionScoreMap(InterviewFeedback feedback) {
        if (!feedback.parsed()) {
            return Map.of();
        }
        Map<String, Integer> scores = new LinkedHashMap<>();
        for (InterviewFeedback.Dimension dimension : feedback.dimensions()) {
            scores.put(dimension.name(), dimension.score());
        }
        return scores;
    }
}
