package com.jinloes.system_design_plugin.web;

import com.jinloes.system_design_plugin.catalog.DesignCatalog;
import com.jinloes.system_design_plugin.catalog.DesignCatalog.Problem;
import com.jinloes.system_design_plugin.interview.InterviewFeedback;
import com.jinloes.system_design_plugin.web.PageState.AttemptInfo;
import com.jinloes.system_design_plugin.web.PageState.View;
import com.jinloes.system_design_plugin.web.ToolWindowView.AttemptEntry;
import com.jinloes.system_design_plugin.web.ToolWindowView.AttemptStore;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.attribute.FileTime;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.ToIntFunction;

import static org.assertj.core.api.Assertions.assertThat;

class ToolWindowViewTest {
    private static final Problem PROBLEM = DesignCatalog.all().get(0);
    private static final Set<String> REVIEWED = Set.of(DesignCatalog.all().get(1).id());
    private static final Function<String, String> NO_SELECTION = problemId -> null;
    private static final ToIntFunction<String> NO_HINTS = attemptId -> 0;

    private static final class FakeStore implements AttemptStore {
        private final List<AttemptEntry> attempts;
        private final Map<String, List<InterviewFeedback.Stored>> feedback = new HashMap<>();
        private IOException listFailure;
        private IOException feedbackFailure;
        private IOException fingerprintFailure;
        private String currentFingerprint = "fp";

        FakeStore(AttemptEntry... attempts) {
            this.attempts = List.of(attempts);
        }

        @Override
        public List<AttemptEntry> attempts(String problemId) throws IOException {
            assertThat(problemId).isEqualTo(PROBLEM.id());
            if (listFailure != null) {
                throw listFailure;
            }
            return attempts;
        }

        @Override
        public List<InterviewFeedback.Stored> feedbackHistory(String attemptId) throws IOException {
            if (feedbackFailure != null) {
                throw feedbackFailure;
            }
            return feedback.getOrDefault(attemptId, List.of());
        }

        @Override
        public String fingerprint(String attemptId) throws IOException {
            if (fingerprintFailure != null) {
                throw fingerprintFailure;
            }
            return currentFingerprint;
        }
    }

    private static AttemptEntry entry(String id, String created) {
        return new AttemptEntry(id, FileTime.from(Instant.parse(created)));
    }

    private static InterviewFeedback.Stored stored() {
        return new InterviewFeedback.Stored(InterviewFeedback.unparsed("reply"), "fp", "model", "now");
    }

    private static View build(String problemId, String error, Function<String, String> selection,
                              ToIntFunction<String> hints, AttemptStore store) {
        return ToolWindowView.build(problemId, true, error, REVIEWED, selection, hints, store, ZoneOffset.UTC);
    }

    @Test
    void missingOrUnknownProblemsShowOnlyTheProblemList() {
        for (String problemId : new String[]{null, "missing"}) {
            View view = build(problemId, "boom", NO_SELECTION, NO_HINTS, new FakeStore());

            assertThat(view.selected()).isNull();
            assertThat(view.attempts()).isEmpty();
            assertThat(view.selectedAttempt()).isEmpty();
            assertThat(view.reviewed()).isEqualTo(REVIEWED);
            assertThat(view.running()).isTrue();
            assertThat(view.feedback()).isNull();
            assertThat(view.error()).isEqualTo("boom");
        }
    }

    @Test
    void ordersAttemptsByCreationThenIdAndLabelsThemInOrder() {
        FakeStore store = new FakeStore(entry("c", "2026-03-02T09:05:00Z"), entry("b", "2026-01-15T14:30:00Z"),
                entry("a", "2026-03-02T09:05:00Z"));

        View view = build(PROBLEM.id(), "", NO_SELECTION, NO_HINTS, store);

        assertThat(view.selected()).isSameAs(PROBLEM);
        assertThat(view.attempts()).containsExactly(
                new AttemptInfo("b", "Attempt 1 (" + label("2026-01-15T14:30:00Z") + ")"),
                new AttemptInfo("a", "Attempt 2 (" + label("2026-03-02T09:05:00Z") + ")"),
                new AttemptInfo("c", "Attempt 3 (" + label("2026-03-02T09:05:00Z") + ")"));
    }

    // Month names follow the default locale, as in the IDE.
    private static String label(String instant) {
        return DateTimeFormatter.ofPattern("MMM d, HH:mm").withZone(ZoneOffset.UTC).format(Instant.parse(instant));
    }

    @Test
    void honorsTheStoredSelectionAndOtherwiseFallsBackToTheNewestAttempt() {
        FakeStore store = new FakeStore(entry("old", "2026-01-01T00:00:00Z"), entry("new", "2026-02-01T00:00:00Z"));

        assertThat(build(PROBLEM.id(), "", problemId -> "old", NO_HINTS, store).selectedAttempt()).isEqualTo("old");
        assertThat(build(PROBLEM.id(), "", problemId -> "gone", NO_HINTS, store).selectedAttempt()).isEqualTo("new");
        assertThat(build(PROBLEM.id(), "", NO_SELECTION, NO_HINTS, store).selectedAttempt()).isEqualTo("new");
    }

    @Test
    void withoutAttemptsNothingIsSelected() {
        View view = build(PROBLEM.id(), "", problemId -> "gone", attemptId -> 3, new FakeStore());

        assertThat(view.selected()).isSameAs(PROBLEM);
        assertThat(view.attempts()).isEmpty();
        assertThat(view.selectedAttempt()).isEmpty();
        assertThat(view.hintsRevealed()).isZero();
        assertThat(view.feedback()).isNull();
        assertThat(view.stale()).isFalse();
    }

    @Test
    void reportsHintsRevealedForTheSelectedAttempt() {
        FakeStore store = new FakeStore(entry("a", "2026-01-01T00:00:00Z"));

        View view = build(PROBLEM.id(), "", NO_SELECTION, attemptId -> attemptId.equals("a") ? 2 : 0, store);

        assertThat(view.hintsRevealed()).isEqualTo(2);
    }

    @Test
    void showsReviewHistoryAndComputesPerReviewStaleness() {
        FakeStore store = new FakeStore(entry("a", "2026-01-01T00:00:00Z"));
        InterviewFeedback.Stored latest = new InterviewFeedback.Stored(
                InterviewFeedback.unparsed("latest"), "fp-2", "model", "t2");
        InterviewFeedback.Stored earlier = new InterviewFeedback.Stored(
                InterviewFeedback.unparsed("earlier"), "fp-1", "model", "t1");
        store.feedback.put("a", List.of(latest, earlier));
        store.currentFingerprint = "fp-2";

        View fresh = build(PROBLEM.id(), "", NO_SELECTION, NO_HINTS, store);
        assertThat(fresh.feedback()).isSameAs(latest);
        assertThat(fresh.stale()).isFalse();
        assertThat(fresh.reviews()).containsExactly(
                new PageState.Review(latest, false),
                new PageState.Review(earlier, true));

        store.currentFingerprint = "fp-3";
        View stale = build(PROBLEM.id(), "", NO_SELECTION, NO_HINTS, store);
        assertThat(stale.feedback()).isSameAs(latest);
        assertThat(stale.stale()).isTrue();
        assertThat(stale.reviews()).extracting(PageState.Review::stale).containsExactly(true, true);
    }

    @Test
    void noFeedbackIsNeverStale() {
        FakeStore store = new FakeStore(entry("a", "2026-01-01T00:00:00Z"));
        store.currentFingerprint = "changed";

        View view = build(PROBLEM.id(), "", NO_SELECTION, NO_HINTS, store);

        assertThat(view.feedback()).isNull();
        assertThat(view.reviews()).isEmpty();
        assertThat(view.stale()).isFalse();
    }

    @Test
    void aListingFailureIsReportedUnlessAnErrorIsAlreadyShown() {
        FakeStore store = new FakeStore();
        store.listFailure = new IOException("disk gone");

        View reported = build(PROBLEM.id(), "", NO_SELECTION, NO_HINTS, store);
        View kept = build(PROBLEM.id(), "earlier", NO_SELECTION, NO_HINTS, store);

        assertThat(reported.error()).isEqualTo("Could not list attempts: disk gone");
        assertThat(reported.attempts()).isEmpty();
        assertThat(reported.selectedAttempt()).isEmpty();
        assertThat(kept.error()).isEqualTo("earlier");
    }

    @Test
    void unreadableFeedbackShowsNoFeedbackWithoutAnError() {
        FakeStore store = new FakeStore(entry("a", "2026-01-01T00:00:00Z"));
        store.feedback.put("a", List.of(stored()));
        store.feedbackFailure = new IOException("corrupt");

        View view = build(PROBLEM.id(), "", NO_SELECTION, NO_HINTS, store);

        assertThat(view.selectedAttempt()).isEqualTo("a");
        assertThat(view.feedback()).isNull();
        assertThat(view.reviews()).isEmpty();
        assertThat(view.stale()).isFalse();
        assertThat(view.error()).isEmpty();
    }

    @Test
    void aStalenessFailureKeepsTheFeedbackAndTreatsItAsCurrent() {
        FakeStore store = new FakeStore(entry("a", "2026-01-01T00:00:00Z"));
        InterviewFeedback.Stored feedback = stored();
        store.feedback.put("a", List.of(feedback));
        store.currentFingerprint = "different";
        store.fingerprintFailure = new IOException("unreadable notes");

        View view = build(PROBLEM.id(), "", NO_SELECTION, NO_HINTS, store);

        assertThat(view.feedback()).isSameAs(feedback);
        assertThat(view.stale()).isFalse();
        assertThat(view.error()).isEmpty();
    }

    @Test
    void formatsCustomAttemptLabelsAndPreservesTargetLevel() {
        FakeStore store = new FakeStore(entry("a", "2026-01-15T14:30:00Z"), entry("b", "2026-02-01T00:00:00Z"));

        View view = ToolWindowView.build(PROBLEM.id(), false, "", REVIEWED, "Staff+", NO_SELECTION,
                id -> "a".equals(id) ? "  Sharded KV  " : null, NO_HINTS, store, ZoneOffset.UTC);

        assertThat(view.targetLevel()).isEqualTo("Staff+");
        assertThat(view.attempts()).containsExactly(
                new AttemptInfo("a", "Sharded KV (" + label("2026-01-15T14:30:00Z") + ")", "Sharded KV"),
                new AttemptInfo("b", "Attempt 2 (" + label("2026-02-01T00:00:00Z") + ")", ""));
    }
}
