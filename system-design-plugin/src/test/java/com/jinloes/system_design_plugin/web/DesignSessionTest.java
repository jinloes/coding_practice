package com.jinloes.system_design_plugin.web;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.jinloes.system_design_plugin.catalog.DesignCatalog;
import com.jinloes.system_design_plugin.interview.InterviewFeedback;
import com.jinloes.system_design_plugin.state.DesignProgress;
import com.jinloes.system_design_plugin.workspace.DesignWorkspace;
import com.jinloes.system_design_plugin.workspace.DesignWorkspace.Attempt;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.LongSupplier;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;

class DesignSessionTest {
    @TempDir
    Path tempDir;

    private AtomicLong clock;
    private DesignWorkspace workspace;
    private DesignProgress progress;
    private DesignSession session;

    @BeforeEach
    void setUp() throws ReflectiveOperationException {
        clock = new AtomicLong(1_700_000_000_000L);
        AtomicLong ids = new AtomicLong(1L);
        Constructor<DesignWorkspace> ctor = DesignWorkspace.class.getDeclaredConstructor(
                Path.class, Supplier.class, LongSupplier.class);
        ctor.setAccessible(true);
        workspace = ctor.newInstance(
                tempDir,
                (Supplier<UUID>) () -> new UUID(0L, ids.getAndIncrement()),
                (LongSupplier) clock::get);
        progress = new DesignProgress(clock::get);
        session = new DesignSession(workspace, progress);
    }

    private static InterviewFeedback parsedFeedback(String verdict, int score, List<String> followUps) {
        JsonObject json = new JsonObject();
        json.addProperty("verdict", verdict);
        json.addProperty("summary", "Structured review");
        JsonArray dimensions = new JsonArray();
        for (String name : DesignCatalog.DIMENSIONS) {
            JsonObject dim = new JsonObject();
            dim.addProperty("name", name);
            dim.addProperty("score", score);
            dim.add("strengths", new JsonArray());
            dim.add("gaps", new JsonArray());
            dimensions.add(dim);
        }
        json.add("dimensions", dimensions);
        JsonArray questions = new JsonArray();
        followUps.forEach(questions::add);
        json.add("followUpQuestions", questions);
        json.add("missedExpectations", new JsonArray());
        return InterviewFeedback.parse(json.toString());
    }

    @Test
    void coordinatesAttemptLifecycleSelectionAndErrorState() throws IOException {
        session.setError("stale error");
        assertThat(session.error()).isEqualTo("stale error");
        session.setError(null);
        assertThat(session.error()).isEmpty();
        session.setError("next error");
        session.clearError();
        assertThat(session.error()).isEmpty();

        session.selectProblem(null);
        session.selectProblem("unknown-problem");
        assertThat(session.selectedProblem()).isEmpty();

        session.selectProblem("url-shortener");
        assertThat(session.selectedProblem()).isEqualTo("url-shortener");

        clock.set(1_700_000_010_000L);
        Attempt first = session.createAttempt("url-shortener");
        clock.set(1_700_000_020_000L);
        Attempt second = session.createAttempt("url-shortener");

        assertThat(progress.createdAtMillis(first.id())).isEqualTo(1_700_000_010_000L);
        assertThat(progress.createdAtMillis(second.id())).isEqualTo(1_700_000_020_000L);
        assertThat(session.readAttempt(first.id()).id()).isEqualTo(first.id());

        session.selectAttempt(first.id());
        session.renameAttempt(first.id(), "First draft");
        assertThat(progress.attemptLabel(first.id())).isEqualTo("First draft");
        session.revealHint(first.id());
        session.selectLevel("Staff+");

        PageState.View view = session.view(false, ZoneOffset.UTC);
        assertThat(view.selectedAttempt()).isEqualTo(first.id());
        assertThat(view.hintsRevealed()).isEqualTo(1);
        assertThat(view.targetLevel()).isEqualTo("Staff+");
        assertThat(view.attempts()).extracting(PageState.AttemptInfo::id)
                .containsExactly(first.id(), second.id());
        assertThat(view.attempts().get(0).customLabel()).isEqualTo("First draft");

        AtomicBoolean closedEditors = new AtomicBoolean();
        assertThat(session.deleteAttempt(first.id(), true, ignored -> closedEditors.set(true))).isFalse();
        assertThat(session.error()).contains("Finish or cancel");
        assertThat(closedEditors).isFalse();

        assertThat(session.deleteAttempt(first.id(), false, ignored -> closedEditors.set(true))).isTrue();
        assertThat(closedEditors).isTrue();
        assertThat(session.error()).isEmpty();
        assertThat(progress.findEntry(first.id())).isEmpty();
        assertThat(workspace.attempts("url-shortener")).extracting(Attempt::id).containsExactly(second.id());
        assertThat(session.view(false, ZoneOffset.UTC).attempts())
                .extracting(PageState.AttemptInfo::id)
                .containsExactly(second.id());
    }

    @Test
    void preparedPromptAndRecordedReviewKeepTheirCapturedInputAfterLaterSaves() throws IOException {
        Attempt attempt = session.createAttempt("url-shortener");
        java.nio.file.Files.writeString(attempt.notes(), "Captured notes");
        java.nio.file.Files.writeString(attempt.diagram(),
                "{\"elements\":[{\"id\":\"t\",\"type\":\"text\",\"text\":\"Captured diagram\"}]}");
        String fingerprint = workspace.fingerprint(attempt);
        DesignSession.PreparedReview prepared = session.preparePrompt(attempt, "Senior", null);
        assertThat(prepared.prompt()).contains("Captured notes", "Captured diagram");
        assertThat(prepared.fingerprint()).isEqualTo(fingerprint);

        java.nio.file.Files.writeString(attempt.notes(), "Later notes");
        java.nio.file.Files.writeString(attempt.diagram(), DesignWorkspace.EMPTY_SCENE);
        InterviewFeedback.Stored recorded = session.recordCompletedReview(
                prepared, parsedFeedback("Hire", 3, List.of("How does recovery work?")), "model");
        assertThat(prepared.prompt()).contains("Captured notes", "Captured diagram").doesNotContain("Later notes");
        assertThat(recorded.fingerprint()).isEqualTo(fingerprint);
        assertThat(workspace.latestFeedback(attempt, InterviewFeedback.Stored::fromJson).orElseThrow().fingerprint())
                .isEqualTo(fingerprint);
        assertThat(workspace.isStale(attempt, recorded.fingerprint())).isTrue();

        DesignSession.PreparedReview followUp = session.preparePrompt(attempt, "Senior", "Retry within a deadline.");
        String followUpFingerprint = followUp.fingerprint();
        assertThat(followUp.prompt()).contains("Later notes", "How does recovery work?");
        java.nio.file.Files.writeString(attempt.notes(), "Another edit");
        clock.incrementAndGet();
        InterviewFeedback.Stored recordedFollowUp = session.recordCompletedReview(
                followUp, parsedFeedback("Hire", 3, List.of()), "model");
        assertThat(recordedFollowUp.fingerprint()).isEqualTo(followUpFingerprint);
        assertThat(recordedFollowUp.round()).isEqualTo(InterviewFeedback.ROUND_FOLLOW_UP);
        assertThat(workspace.isStale(attempt, recordedFollowUp.fingerprint())).isTrue();
    }

    @Test
    void preparesPromptsUsingNewestParsedFollowUpsAndRecordsCompletedReviews() throws IOException {
        Attempt attempt = session.createAttempt("url-shortener");

        DesignSession.PreparedReview initial = session.preparePrompt(attempt, "Senior", null);
        assertThat(initial.followUpAnswers()).isEmpty();
        assertThat(initial.prompt()).doesNotContain("<prior_follow_up_questions>");

        // Initial review has follow-up questions; a later unparsed retry and empty-question review must not hide them.
        clock.set(1_700_000_100_000L);
        session.recordCompletedReview(
                initial,
                parsedFeedback("Hire", 3, List.of("How do you shard hot URLs?")),
                "model-a");
        clock.set(1_700_000_200_000L);
        session.recordCompletedReview(
                initial,
                parsedFeedback("Hire", 3, List.of()),
                "model-a");
        clock.set(1_700_000_300_000L);
        DesignSession.PreparedReview followUp =
                session.preparePrompt(attempt, "Senior", "Consistent hashing with virtual nodes.");
        assertThat(followUp.prompt()).contains("How do you shard hot URLs?");
        assertThat(followUp.prompt()).contains("Consistent hashing with virtual nodes.");

        InterviewFeedback.Stored unparsedFollowUp = session.recordCompletedReview(
                followUp, InterviewFeedback.parse("non-json model text"), "model-b");
        assertThat(unparsedFollowUp.round()).isEqualTo(InterviewFeedback.ROUND_FOLLOW_UP);
        assertThat(progress.bestVerdicts()).containsEntry("url-shortener", "Hire");
        assertThat(progress.problemDimensionScores().get("url-shortener").values()).containsOnly(3);

        // Newer unparsed file on top still resolves the earlier parsed follow-up question on the next round.
        DesignSession.PreparedReview secondFollowUp =
                session.preparePrompt(attempt, "Senior", "Also cache top keys at the edge.");
        assertThat(secondFollowUp.prompt()).contains("How do you shard hot URLs?");

        // Attempt without any parsed follow-up questions omits prior questions cleanly.
        Attempt cleanAttempt = session.createAttempt("rate-limiter");
        clock.set(1_700_000_400_000L);
        workspace.saveFeedback(cleanAttempt, new InterviewFeedback.Stored(
                InterviewFeedback.unparsed("raw"), "fp", "m", "now").toJson());
        DesignSession.PreparedReview withoutQuestions =
                session.preparePrompt(cleanAttempt, "Senior", "Token bucket per tenant.");
        assertThat(withoutQuestions.prompt()).doesNotContain("How do you shard");
    }

    @Test
    void backfillsLegacyReviewedAttemptScoresOnViewAndHandlesFallbacks() throws IOException {
        assertThat(session.view(null, false, "", ZoneOffset.UTC).selected()).isNull();
        assertThat(session.view("missing", false, "", ZoneOffset.UTC).selected()).isNull();
        session.selectProblem("rate-limiter");
        assertThat(session.view(false, ZoneOffset.UTC).attempts()).isEmpty();

        // Simulate a legacy attempt created on disk before createdAtMillis or dimensionScores existed.
        Attempt legacy = workspace.create(DesignCatalog.find("url-shortener"));
        Attempt unreviewed = workspace.create(DesignCatalog.find("news-feed"));
        assertThat(DesignSession.filesystemCreationTime(tempDir.resolve("does-not-exist")))
                .isEqualTo(FileTime.fromMillis(0));

        DesignProgress.Data legacyState = new DesignProgress.Data();
        DesignProgress.Entry legacyEntry = new DesignProgress.Entry();
        legacyEntry.problemId = "url-shortener";
        legacyEntry.lastVerdict = "Hire";
        legacyEntry.lastReviewedAt = "2026-01-01T00:00:00Z";
        legacyState.attempts.put(legacy.id(), legacyEntry);
        DesignProgress.Entry unreviewedEntry = new DesignProgress.Entry();
        unreviewedEntry.problemId = "news-feed";
        legacyState.attempts.put(unreviewed.id(), unreviewedEntry);
        progress.loadState(legacyState);

        clock.set(1_700_000_500_000L);
        workspace.saveFeedback(legacy, new InterviewFeedback.Stored(
                parsedFeedback("Hire", 2, List.of()), workspace.fingerprint(legacy), "m", "t1").toJson());
        clock.set(1_700_000_600_000L);
        workspace.saveFeedback(legacy, new InterviewFeedback.Stored(
                InterviewFeedback.unparsed("raw retry"), workspace.fingerprint(legacy), "m", "t2").toJson());

        session.selectProblem("news-feed");
        assertThat(session.view(false, ZoneOffset.UTC).selectedAttempt()).isEqualTo(unreviewed.id());
        // Also cover an attempt on disk with no DesignProgress entry yet, and a reviewed attempt with unparsed feedback.
        Attempt untracked = workspace.create(DesignCatalog.find("chat-messaging"));
        session.selectProblem("chat-messaging");
        assertThat(session.view(false, ZoneOffset.UTC).selectedAttempt()).isEqualTo(untracked.id());

        Attempt unparsedOnly = workspace.create(DesignCatalog.find("metrics-monitoring"));
        Attempt mismatchedProblem = workspace.create(DesignCatalog.find("key-value-store"));
        DesignProgress.Data extraLegacy = progress.getState();
        DesignProgress.Entry unparsedEntry = new DesignProgress.Entry();
        unparsedEntry.problemId = "metrics-monitoring";
        unparsedEntry.lastReviewedAt = "2026-01-01T00:00:00Z";
        DesignProgress.Entry wrongProblemEntry = new DesignProgress.Entry();
        wrongProblemEntry.problemId = "rate-limiter";
        wrongProblemEntry.lastVerdict = "Hire";
        wrongProblemEntry.lastReviewedAt = "2026-01-01T00:00:00Z";
        extraLegacy.attempts.put(unparsedOnly.id(), unparsedEntry);
        extraLegacy.attempts.put(mismatchedProblem.id(), wrongProblemEntry);
        progress.loadState(extraLegacy);
        clock.set(1_700_000_650_000L);
        workspace.saveFeedback(unparsedOnly, new InterviewFeedback.Stored(
                InterviewFeedback.unparsed("raw"), workspace.fingerprint(unparsedOnly), "m", "t0").toJson());
        clock.set(1_700_000_660_000L);
        workspace.saveFeedback(mismatchedProblem, new InterviewFeedback.Stored(
                parsedFeedback("Hire", 3, List.of()), workspace.fingerprint(mismatchedProblem), "m", "t0").toJson());
        assertThat(progress.needsDimensionBackfill(unparsedOnly.id())).isTrue();
        session.selectProblem("metrics-monitoring");
        assertThat(session.view(false, ZoneOffset.UTC).selectedAttempt()).isEqualTo(unparsedOnly.id());
        assertThat(progress.needsDimensionBackfill(unparsedOnly.id())).isFalse();
        assertThat(progress.problemDimensionScores()).doesNotContainKey("metrics-monitoring");

        assertThat(progress.needsDimensionBackfill(mismatchedProblem.id())).isTrue();
        session.selectProblem("key-value-store");
        assertThat(session.view(false, ZoneOffset.UTC).selectedAttempt()).isEqualTo(mismatchedProblem.id());
        assertThat(progress.needsDimensionBackfill(mismatchedProblem.id())).isFalse();

        // First view of url-shortener backfills scores from feedback history; second view reuses persisted scores.
        session.selectProblem("url-shortener");
        PageState.View backfilled = session.view(false, ZoneOffset.UTC);
        assertThat(backfilled.dimensionSummary())
                .allSatisfy(summary -> {
                    assertThat(summary.reviewedCount()).isEqualTo(1);
                    assertThat(summary.averageScore()).isEqualTo(2.0);
                });
        assertThat(session.view(false, ZoneOffset.UTC).selectedAttempt()).isEqualTo(legacy.id());

        // Multi-attempt backfill scans every attempt of the opened problem, even when selectedAttempt is newer/weaker.
        clock.set(1_700_000_670_000L);
        Attempt olderStrong = workspace.create(DesignCatalog.find("distributed-cache"));
        clock.set(1_700_000_680_000L);
        Attempt newerWeakSelected = workspace.create(DesignCatalog.find("distributed-cache"));
        DesignProgress.Data multiAttemptLegacy = progress.getState();
        DesignProgress.Entry olderStrongEntry = new DesignProgress.Entry();
        olderStrongEntry.problemId = "distributed-cache";
        olderStrongEntry.lastVerdict = "Hire";
        olderStrongEntry.lastReviewedAt = "2026-01-01T00:00:00Z";
        DesignProgress.Entry newerWeakEntry = new DesignProgress.Entry();
        newerWeakEntry.problemId = "distributed-cache";
        newerWeakEntry.lastVerdict = "No hire";
        newerWeakEntry.lastReviewedAt = "2026-01-02T00:00:00Z";
        multiAttemptLegacy.attempts.put(olderStrong.id(), olderStrongEntry);
        multiAttemptLegacy.attempts.put(newerWeakSelected.id(), newerWeakEntry);
        multiAttemptLegacy.selectedAttempts.put("distributed-cache", newerWeakSelected.id());
        progress.loadState(multiAttemptLegacy);
        clock.set(1_700_000_700_000L);
        workspace.saveFeedback(olderStrong, new InterviewFeedback.Stored(
                parsedFeedback("Hire", 4, List.of()), workspace.fingerprint(olderStrong), "m", "t3").toJson());
        clock.set(1_700_000_710_000L);
        workspace.saveFeedback(newerWeakSelected, new InterviewFeedback.Stored(
                parsedFeedback("No hire", 1, List.of()), workspace.fingerprint(newerWeakSelected), "m", "t4").toJson());
        session.selectProblem("distributed-cache");
        assertThat(session.view(false, ZoneOffset.UTC).selectedAttempt()).isEqualTo(newerWeakSelected.id());
        assertThat(progress.problemDimensionScores().get("distributed-cache").values()).containsOnly(4);

        // Feedback selection chooses strongest parsed verdict and breaks equal-verdict ties by higher average score.
        InterviewFeedback invalidVerdict = new InterviewFeedback(
                true, "Unknown", "s", List.of(), List.of(), List.of(), "");
        InterviewFeedback emptyDimensions = new InterviewFeedback(
                true, "Strong hire", "s", List.of(), List.of(), List.of(), "");
        List<InterviewFeedback.Stored> history = List.of(
                new InterviewFeedback.Stored(InterviewFeedback.unparsed("raw"), "fp", "m", "t6"),
                new InterviewFeedback.Stored(invalidVerdict, "fp", "m", "t5"),
                new InterviewFeedback.Stored(emptyDimensions, "fp", "m", "t4"),
                new InterviewFeedback.Stored(parsedFeedback("Lean no hire", 2, List.of()), "fp", "m", "t3"),
                new InterviewFeedback.Stored(parsedFeedback("Hire", 2, List.of()), "fp", "m", "t2"),
                new InterviewFeedback.Stored(parsedFeedback("Hire", 4, List.of()), "fp", "m", "t1"),
                new InterviewFeedback.Stored(parsedFeedback("Hire", 3, List.of()), "fp", "m", "t0"),
                new InterviewFeedback.Stored(parsedFeedback("Lean hire", 4, List.of()), "fp", "m", "t-1"));
        InterviewFeedback chosen = DesignSession.selectBackfillFeedback(history);
        assertThat(chosen).isNotNull();
        assertThat(chosen.verdict()).isEqualTo("Hire");
        assertThat(chosen.dimensions()).allSatisfy(dim -> assertThat(dim.score()).isEqualTo(4));

        // Store IOException during backfill is swallowed so ToolWindowView.build reports the listing error cleanly.
        ToolWindowView.AttemptStore failingStore = new ToolWindowView.AttemptStore() {
            @Override
            public List<ToolWindowView.AttemptEntry> attempts(String problemId) throws IOException {
                throw new IOException("io failure");
            }

            @Override
            public List<InterviewFeedback.Stored> feedbackHistory(String attemptId) {
                return List.of();
            }

            @Override
            public String fingerprint(String attemptId) {
                return "";
            }
        };
        PageState.View failedView = session.view("url-shortener", false, "", ZoneOffset.UTC, failingStore);
        assertThat(failedView.error()).isEqualTo("Could not list attempts: io failure");
    }
}
