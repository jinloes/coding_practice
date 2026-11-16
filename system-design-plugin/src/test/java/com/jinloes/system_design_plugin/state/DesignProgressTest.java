package com.jinloes.system_design_plugin.state;

import com.jinloes.system_design_plugin.catalog.DesignCatalog;
import com.jinloes.system_design_plugin.interview.CopilotInterviewer;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DesignProgressTest {
    private static final String ATTEMPT = "url-shortener-0123456789abcdef0123456789abcdef";

    @Test
    void stateRoundTripsSelectionHintsReviewsAndSettings() {
        DesignProgress original = new DesignProgress();
        original.select("url-shortener", ATTEMPT);
        original.revealHint(ATTEMPT, "url-shortener");
        original.recordReview(ATTEMPT, "url-shortener", "Hire");
        original.updateSettings("C:\\tools\\copilot.exe", "some-model", 120);

        DesignProgress restored = new DesignProgress();
        restored.loadState(original.getState());

        DesignProgress.Entry entry = restored.entry(ATTEMPT, "url-shortener");
        assertThat(restored.selectedAttempt("url-shortener")).isEqualTo(ATTEMPT);
        assertThat(restored.hintsRevealed(ATTEMPT)).isEqualTo(1);
        assertThat(restored.hintsRevealed("missing-attempt")).isZero();
        assertThat(entry.hintsRevealed).isEqualTo(1);
        assertThat(entry.lastVerdict).isEqualTo("Hire");
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
                    progress.recordReview(id, "url-shortener", "Hire");
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
        progress.recordReview("url-shortener-a", "url-shortener", null);
        progress.recordReview("url-shortener-b", "url-shortener", "No hire");

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
        DesignProgress.Entry sparse = new DesignProgress.Entry();
        sparse.problemId = null;
        sparse.lastVerdict = null;
        sparse.lastReviewedAt = null;
        partialEntry.attempts.put("null-entry", null);
        partialEntry.attempts.put(ATTEMPT, sparse);
        progress.loadState(partialEntry);
        assertThat(progress.getState().attempts).containsOnlyKeys(ATTEMPT);
        assertThat(progress.getState().attempts.get(ATTEMPT).problemId).isEmpty();
    }
}
