package com.jinloes.system_design_plugin.state;

import com.jinloes.system_design_plugin.catalog.DesignCatalog;
import org.junit.jupiter.api.Test;

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
        original.getState().cliPath = "C:\\tools\\copilot.exe";
        original.getState().model = "some-model";
        original.getState().timeoutSeconds = 120;

        DesignProgress restored = new DesignProgress();
        restored.loadState(original.getState());

        DesignProgress.Entry entry = restored.entry(ATTEMPT, "url-shortener");
        assertThat(restored.selectedAttempt("url-shortener")).isEqualTo(ATTEMPT);
        assertThat(entry.hintsRevealed).isEqualTo(1);
        assertThat(entry.lastVerdict).isEqualTo("Hire");
        assertThat(entry.lastReviewedAt).isNotBlank();
        assertThat(restored.getState().cliPath).isEqualTo("C:\\tools\\copilot.exe");
        assertThat(restored.getState().model).isEqualTo("some-model");
        assertThat(restored.timeoutSeconds()).isEqualTo(120);
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
        progress.getState().timeoutSeconds = 5;
        assertThat(progress.timeoutSeconds()).isEqualTo(300);
        progress.getState().timeoutSeconds = 5000;
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
    }
}
