package com.jinloes.system_design_plugin.state;

import com.jinloes.system_design_plugin.catalog.DesignCatalog;
import com.jinloes.system_design_plugin.interview.CopilotInterviewer;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DesignProgressTest {
    private static final String ATTEMPT = "url-shortener-0123456789abcdef0123456789abcdef";

    private static Map<String, Integer> scores(int score) {
        Map<String, Integer> result = new HashMap<>();
        for (String dimension : DesignCatalog.DIMENSIONS) {
            result.put(dimension, score);
        }
        return result;
    }

    private static DesignProgress.Entry entryOf(String problemId, String lastVerdict, String bestVerdict,
                                                String scoresVerdict, String reviewedAt,
                                                Map<String, Integer> dimensionScores) {
        DesignProgress.Entry entry = new DesignProgress.Entry();
        entry.problemId = problemId;
        entry.lastVerdict = lastVerdict;
        entry.bestVerdict = bestVerdict;
        entry.scoresVerdict = scoresVerdict;
        entry.lastReviewedAt = reviewedAt;
        entry.dimensionScores = dimensionScores;
        return entry;
    }

    @Test
    void stateRoundTripsSelectionHintsReviewsAndSettings() {
        AtomicLong clock = new AtomicLong(1_700_000_000_000L);
        DesignProgress original = new DesignProgress(clock::get);
        original.select("url-shortener", ATTEMPT);
        original.revealHint(ATTEMPT, "url-shortener");
        original.recordReview(ATTEMPT, "url-shortener", "Hire", scores(3));
        original.updateSettings("C:\\tools\\copilot.exe", "some-model", 120);

        DesignProgress restored = new DesignProgress();
        restored.loadState(original.getState());

        DesignProgress.Entry entry = restored.entry(ATTEMPT, "url-shortener");
        assertThat(restored.selectedAttempt("url-shortener")).isEqualTo(ATTEMPT);
        assertThat(restored.createdAtMillis(ATTEMPT)).isEqualTo(1_700_000_000_000L);
        assertThat(restored.createdAtMillis("missing-attempt")).isZero();
        assertThat(restored.findEntry(ATTEMPT)).isPresent();
        assertThat(restored.findEntry("missing-attempt")).isEmpty();
        assertThat(restored.hintsRevealed(ATTEMPT)).isEqualTo(1);
        assertThat(restored.hintsRevealed("missing-attempt")).isZero();
        assertThat(entry.hintsRevealed).isEqualTo(1);
        assertThat(entry.lastVerdict).isEqualTo("Hire");
        assertThat(entry.bestVerdict).isEqualTo("Hire");
        assertThat(entry.dimensionScores).containsAllEntriesOf(scores(3));
        assertThat(entry.lastReviewedAt).isNotBlank();
        assertThat(restored.settings())
                .isEqualTo(new CopilotInterviewer.Settings("C:\\tools\\copilot.exe", "some-model", 120));
        original.updateSettings(null, null, 60);
        assertThat(original.settings()).isEqualTo(new CopilotInterviewer.Settings("", "", 60));
    }

    @Test
    void getStateReturnsAnIsolatedSnapshot() {
        DesignProgress progress = new DesignProgress();
        progress.revealHint(ATTEMPT, "url-shortener");
        DesignProgress.Data snapshot = progress.getState();
        snapshot.attempts.clear();
        snapshot.selectedAttempts.put("url-shortener", "other");
        snapshot.cliPath = "mutated";

        assertThat(progress.hintsRevealed(ATTEMPT)).isEqualTo(1);
        assertThat(progress.selectedAttempt("url-shortener")).isNull();
        assertThat(progress.settings().cliPath()).isEmpty();
    }

    @Test
    void supportsConcurrentMutationsAndPooledReads() throws Exception {
        DesignProgress progress = new DesignProgress();
        try (var pool = Executors.newFixedThreadPool(4)) {
            List<Callable<Void>> tasks = new ArrayList<>();
            tasks.add(() -> {
                for (int i = 0; i < 200; i++) {
                    String id = "url-shortener-" + i;
                    progress.revealHint(id, "url-shortener");
                    progress.select("url-shortener", id);
                    progress.recordReview(id, "url-shortener", "Hire", scores(3));
                }
                return null;
            });
            for (int reader = 0; reader < 3; reader++) {
                tasks.add(() -> {
                    for (int i = 0; i < 200; i++) {
                        assertThat(progress.reviewedProblemIds()).isNotNull();
                        progress.selectedAttempt("url-shortener");
                        progress.hintsRevealed("url-shortener-" + i);
                        assertThat(progress.getState().attempts).isNotNull();
                    }
                    return null;
                });
            }
            for (Future<Void> future : pool.invokeAll(tasks)) {
                future.get();
            }
        }
        assertThat(progress.reviewedProblemIds()).containsExactly("url-shortener");
    }

    @Test
    void hintsStopAtTheCatalogHintCount() {
        DesignProgress progress = new DesignProgress();
        for (int index = 1; index <= DesignCatalog.HINT_COUNT; index++) {
            assertThat(progress.revealHint(ATTEMPT, "url-shortener")).isEqualTo(index);
        }
        assertThat(progress.revealHint(ATTEMPT, "url-shortener")).isEqualTo(DesignCatalog.HINT_COUNT);
    }

    @Test
    void reviewedProblemsAreDerivedFromAttemptsWithFeedback() {
        DesignProgress progress = new DesignProgress();
        progress.entry("rate-limiter-a", "rate-limiter");
        progress.revealHint("news-feed-a", "news-feed");
        progress.recordReview("url-shortener-a", "url-shortener", null, null);
        progress.recordReview("url-shortener-b", "url-shortener", "No hire", scores(1));

        assertThat(progress.reviewedProblemIds()).containsExactly("url-shortener");
        assertThat(progress.entry("url-shortener-a", "url-shortener").lastVerdict).isEmpty();
    }

    @Test
    void attemptsStayBoundToTheirProblem() {
        DesignProgress progress = new DesignProgress();
        progress.entry(ATTEMPT, "url-shortener");
        assertThatThrownBy(() -> progress.entry(ATTEMPT, "rate-limiter"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void timeoutAcceptsTheDocumentedRangeOnly() {
        DesignProgress.validateTimeout(30);
        DesignProgress.validateTimeout(1800);
        assertThatThrownBy(() -> DesignProgress.validateTimeout(29)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> DesignProgress.validateTimeout(1801))
                .isInstanceOf(IllegalArgumentException.class);

        DesignProgress progress = new DesignProgress();
        assertThat(progress.timeoutSeconds()).isEqualTo(300);
        DesignProgress.Data tooLow = new DesignProgress.Data();
        tooLow.timeoutSeconds = 5;
        progress.loadState(tooLow);
        assertThat(progress.timeoutSeconds()).isEqualTo(300);
        DesignProgress.Data tooHigh = new DesignProgress.Data();
        tooHigh.timeoutSeconds = 5000;
        progress.loadState(tooHigh);
        assertThat(progress.timeoutSeconds()).isEqualTo(300);
    }

    @Test
    void loadStateBackfillsMissingFieldsFromOlderState() {
        DesignProgress.Data state = new DesignProgress.Data();
        state.attempts = null;
        state.selectedAttempts = null;
        state.cliPath = null;
        state.model = null;
        DesignProgress progress = new DesignProgress();

        progress.loadState(state);

        assertThat(progress.getState().attempts).isEmpty();
        assertThat(progress.selectedAttempt("url-shortener")).isNull();
        assertThat(progress.getState().cliPath).isEmpty();
        assertThat(progress.getState().model).isEmpty();

        DesignProgress.Data partialEntry = new DesignProgress.Data();
        DesignProgress.Entry sparse = entryOf(null, null, null, null, null, null);
        sparse.createdAtMillis = -50L;
        partialEntry.attempts.put("null-entry", null);
        partialEntry.attempts.put(ATTEMPT, sparse);
        partialEntry.attempts.put("rate-limiter-1",
                entryOf("rate-limiter", "Hire", "", "", "2026-01-01T00:00:00Z", Map.of()));
        partialEntry.attempts.put("news-feed-1",
                entryOf("news-feed", "Lean hire", "", "", "2026-01-02T00:00:00Z", scores(2)));
        partialEntry.attempts.put("chat-messaging-1",
                entryOf("chat-messaging", "", "", "", "2026-01-03T00:00:00Z", scores(4)));
        progress.loadState(partialEntry);
        assertThat(progress.getState().attempts)
                .containsOnlyKeys(ATTEMPT, "rate-limiter-1", "news-feed-1", "chat-messaging-1");
        assertThat(progress.getState().attempts.get(ATTEMPT).problemId).isEmpty();
        assertThat(progress.createdAtMillis(ATTEMPT)).isZero();
        assertThat(progress.bestVerdicts()).containsEntry("rate-limiter", "Hire");
        assertThat(progress.getState().attempts.get("news-feed-1").scoresVerdict).isEqualTo("Lean hire");
        assertThat(progress.getState().attempts.get("news-feed-1").scoresBackfilled).isTrue();
        assertThat(progress.getState().attempts.get("chat-messaging-1").dimensionScores).isEmpty();
        assertThat(progress.getState().attempts.get("chat-messaging-1").scoresVerdict).isEmpty();
    }

    @Test
    void renamesRemovesAttemptsAndPersistsTargetLevel() {
        DesignProgress progress = new DesignProgress();
        assertThat(progress.targetLevel()).isEqualTo("Senior");
        progress.setTargetLevel("Staff+");
        assertThat(progress.targetLevel()).isEqualTo("Staff+");
        progress.setTargetLevel("Staff+");
        progress.setTargetLevel("Unknown");
        assertThat(progress.targetLevel()).isEqualTo("Senior");

        assertThat(progress.attemptLabel("missing")).isEmpty();
        assertThat(progress.renameAttempt(ATTEMPT, "url-shortener", "  First\npass\t" + "x".repeat(90)))
                .hasSize(DesignProgress.MAX_LABEL_CHARS)
                .startsWith("First pass ");
        assertThat(progress.attemptLabel(ATTEMPT)).startsWith("First pass ");
        assertThat(progress.renameAttempt(ATTEMPT, "url-shortener", null)).isEmpty();
        assertThat(progress.renameAttempt(ATTEMPT, "url-shortener", "")).isEmpty();

        progress.select("url-shortener", ATTEMPT);
        progress.select("url-shortener", ATTEMPT);
        progress.select("rate-limiter", "rate-limiter-keep");
        progress.recordReview(ATTEMPT, "url-shortener", "Hire", scores(3));
        assertThat(progress.reviewedProblemIds()).containsExactly("url-shortener");

        progress.removeAttempt(ATTEMPT);
        progress.removeAttempt("rate-limiter-keep");
        progress.removeAttempt("never-existed");
        assertThat(progress.selectedAttempt("url-shortener")).isNull();
        assertThat(progress.selectedAttempt("rate-limiter")).isNull();
        assertThat(progress.reviewedProblemIds()).isEmpty();

        progress.recordReview("url-1", "url-shortener", null, Map.of());
        progress.recordReview("url-2", "url-shortener", "Lean hire", scores(2));
        progress.recordReview("url-3", "url-shortener", "Strong hire", scores(4));
        progress.recordReview("url-4", "url-shortener", "No hire", scores(1));
        progress.recordReview("url-5", "url-shortener", "Bogus verdict", scores(1));
        assertThat(progress.bestVerdicts()).containsEntry("url-shortener", "Strong hire");

        assertThatThrownBy(() -> progress.loadState(null)).isInstanceOf(NullPointerException.class);
    }

    @Test
    void preservesBestVerdictAndDimensionProfileAcrossRetriesAndBackfillsLegacyAttempts() {
        DesignProgress progress = new DesignProgress(() -> -10L);
        AtomicInteger notifications = new AtomicInteger();
        DesignProgress.Subscription subscription = progress.subscribe(notifications::incrementAndGet);

        Map<String, Integer> messy = new HashMap<>(scores(3));
        messy.put("Unknown dimension", 4);
        messy.put(DesignCatalog.DIMENSIONS.get(0), 0);
        messy.put(DesignCatalog.DIMENSIONS.get(1), 5);
        messy.put(DesignCatalog.DIMENSIONS.get(2), null);
        progress.recordReview(ATTEMPT, "url-shortener", "Hire", messy);
        assertThat(progress.createdAtMillis(ATTEMPT)).isZero();
        assertThat(progress.problemDimensionScores().get("url-shortener")).hasSize(4);

        // Weaker or unparsed retries on the same attempt keep the stronger verdict and dimension scores.
        progress.recordReview(ATTEMPT, "url-shortener", "No hire", scores(1));
        progress.recordReview(ATTEMPT, "url-shortener", "", scores(4));
        assertThat(progress.bestVerdicts()).containsEntry("url-shortener", "Hire");
        assertThat(progress.findEntry(ATTEMPT).orElseThrow().lastVerdict).isEmpty();
        assertThat(progress.problemDimensionScores().get("url-shortener")).hasSize(4);

        // Equal verdict updates scores only when the average score does not regress; stronger verdict upgrades.
        progress.recordReview(ATTEMPT, "url-shortener", "Hire", scores(2));
        assertThat(progress.problemDimensionScores().get("url-shortener").values()).containsOnly(3);
        progress.recordReview(ATTEMPT, "url-shortener", "Hire", scores(4));
        assertThat(progress.problemDimensionScores().get("url-shortener").values()).containsOnly(4);
        progress.recordReview(ATTEMPT, "url-shortener", "Strong hire", scores(3));
        assertThat(progress.bestVerdicts()).containsEntry("url-shortener", "Strong hire");
        assertThat(progress.problemDimensionScores().get("url-shortener").values()).containsOnly(3);

        // Problem-level scores choose strongest scoresVerdict across attempts, breaking ties by average then timestamp.
        progress.recordReview("rate-1", "rate-limiter", "Hire", scores(2));
        progress.recordReview("rate-2", "rate-limiter", "Hire", scores(4));
        progress.recordReview("rate-3", "rate-limiter", "Hire", scores(3));
        progress.recordReview("rate-4", "rate-limiter", "Lean hire", scores(4));
        assertThat(progress.problemDimensionScores().get("rate-limiter").values()).containsOnly(4);
        progress.recordReview("rate-5", "rate-limiter", "Strong hire", scores(2));
        assertThat(progress.problemDimensionScores().get("rate-limiter").values()).containsOnly(2);

        // Pre-upgrade bestVerdict ("Strong hire") with empty dimensionScores must not block retries or misrank scores.
        DesignProgress legacyUpgrade = new DesignProgress();
        DesignProgress.Data upgradeData = new DesignProgress.Data();
        String d0 = DesignCatalog.DIMENSIONS.get(0);
        String d1 = DesignCatalog.DIMENSIONS.get(1);
        upgradeData.attempts.put("kv-legacy",
                entryOf("key-value-store", "Strong hire", "Strong hire", "", "2026-01-01T00:00:00Z", Map.of()));
        upgradeData.attempts.put("chat-mid",
                entryOf("chat-messaging", "Hire", "Hire", "Hire", "2026-02-01T00:00:00Z", Map.of(d0, 3, d1, 3)));
        upgradeData.attempts.put("chat-old",
                entryOf("chat-messaging", "Hire", "Hire", "Hire", "2026-01-01T00:00:00Z", Map.of(d0, 2, d1, 4)));
        upgradeData.attempts.put("chat-new",
                entryOf("chat-messaging", "Hire", "Hire", "Hire", "2026-03-01T00:00:00Z", Map.of(d0, 4, d1, 2)));
        legacyUpgrade.loadState(upgradeData);
        assertThat(legacyUpgrade.problemDimensionScores().get("chat-messaging")).containsEntry(d0, 4);

        legacyUpgrade.recordReview("kv-legacy", "key-value-store", "No hire", scores(1));
        assertThat(legacyUpgrade.bestVerdicts()).containsEntry("key-value-store", "Strong hire");
        assertThat(legacyUpgrade.problemDimensionScores().get("key-value-store").values()).containsOnly(1);
        legacyUpgrade.recordReview("kv-hire", "key-value-store", "Hire", scores(4));
        assertThat(legacyUpgrade.problemDimensionScores().get("key-value-store").values()).containsOnly(4);
        legacyUpgrade.recordReview("kv-legacy", "key-value-store", "Hire", scores(3));
        assertThat(legacyUpgrade.findEntry("kv-legacy").orElseThrow().dimensionScores.values()).containsOnly(3);
        legacyUpgrade.recordReview("kv-empty-scores", "key-value-store", "Hire", Map.of());
        assertThat(legacyUpgrade.findEntry("kv-empty-scores").orElseThrow().dimensionScores).isEmpty();

        progress.entry("unreviewed", "news-feed");
        DesignProgress.Data newsFeedState = progress.getState();
        newsFeedState.attempts.put("legacy-reviewed",
                entryOf("news-feed", "Lean hire", "", "", "2026-01-05T00:00:00Z", Map.of()));
        newsFeedState.attempts.put("unparsed-reviewed",
                entryOf("news-feed", "", "", "", "2026-01-06T00:00:00Z", Map.of()));
        progress.loadState(newsFeedState);

        assertThat(progress.needsDimensionBackfill("missing")).isFalse();
        assertThat(progress.needsDimensionBackfill("unreviewed")).isFalse();
        assertThat(progress.needsDimensionBackfill(ATTEMPT)).isFalse();
        assertThat(progress.needsDimensionBackfill("legacy-reviewed")).isTrue();
        assertThat(progress.needsDimensionBackfill("unparsed-reviewed")).isTrue();
        progress.markDimensionBackfillChecked("missing");
        progress.markDimensionBackfillChecked("unparsed-reviewed");
        assertThat(progress.needsDimensionBackfill("unparsed-reviewed")).isFalse();

        assertThat(progress.backfillDimensionScores("legacy-reviewed", "news-feed", "", scores(3))).isFalse();
        assertThat(progress.backfillDimensionScores("legacy-reviewed", "news-feed", "Hire", Map.of())).isFalse();
        assertThat(progress.backfillDimensionScores("missing", "news-feed", "Hire", scores(3))).isFalse();
        assertThat(progress.backfillDimensionScores("legacy-reviewed", "wrong-problem", "Hire", scores(3))).isFalse();
        assertThat(progress.backfillDimensionScores("unreviewed", "news-feed", "Hire", scores(3))).isFalse();
        assertThat(progress.backfillDimensionScores("legacy-reviewed", "news-feed", "Hire", scores(3))).isTrue();
        assertThat(progress.needsDimensionBackfill("legacy-reviewed")).isFalse();
        assertThat(progress.bestVerdicts()).containsEntry("news-feed", "Hire");
        assertThat(progress.backfillDimensionScores("legacy-reviewed", "news-feed", "Strong hire", scores(4)))
                .isFalse();
        assertThat(progress.problemDimensionScores().get("news-feed").values()).containsOnly(3);

        int countBeforeClose = notifications.get();
        assertThat(countBeforeClose).isPositive();
        subscription.close();
        progress.recordReview("after-close", "news-feed", "Strong hire", scores(4));
        assertThat(notifications.get()).isEqualTo(countBeforeClose);
    }
}
