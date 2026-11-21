package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CorrectnessTest {
    @Test
    void emptyTrackerReturnsNoQualifiedAgents() {
        assertThat(new Solution().qualifiedAgents(1)).isEmpty();
    }

    @Test
    void distinguishesCloseFractionalAveragesWithoutTruncation() {
        Solution tracker = new Solution();
        // 19 / 4 = 4.75 vs 14 / 3 = 4.666... vs 9 / 2 = 4.5
        tracker.recordScore("rio", 5);
        tracker.recordScore("rio", 5);
        tracker.recordScore("rio", 5);
        tracker.recordScore("rio", 4);

        tracker.recordScore("nia", 5);
        tracker.recordScore("nia", 5);
        tracker.recordScore("nia", 4);

        tracker.recordScore("leo", 5);
        tracker.recordScore("leo", 4);

        assertThat(tracker.qualifiedAgents(2)).containsExactly("rio", "nia", "leo");
    }

    @Test
    void updatesRankingsAcrossRepeatedQueriesAsNewScoresArrive() {
        Solution tracker = new Solution();
        tracker.recordScore("dev", 3);
        tracker.recordScore("sam", 5);
        assertThat(tracker.qualifiedAgents(1)).containsExactly("sam", "dev");

        tracker.recordScore("sam", 1);
        tracker.recordScore("dev", 5);
        // dev: 8/2 = 4.0, sam: 6/2 = 3.0
        assertThat(tracker.qualifiedAgents(2)).containsExactly("dev", "sam");
    }

    @Test
    void rejectsOutOfRangeScoresBlankAgentsAndInvalidThresholds() {
        Solution tracker = new Solution();
        assertThatThrownBy(() -> tracker.recordScore("", 5))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> tracker.recordScore("   ", 5))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> tracker.recordScore(null, 5))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> tracker.recordScore("mia", 0))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> tracker.recordScore("mia", 6))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> tracker.qualifiedAgents(0))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void handlesLargeScoreCountsWithoutIntegerOverflow() {
        Solution tracker = new Solution();
        for (int i = 0; i < 1_000; i++) {
            tracker.recordScore("alpha", 5);
            tracker.recordScore("beta", i == 999 ? 4 : 5);
        }
        assertThat(tracker.qualifiedAgents(1_000)).containsExactly("alpha", "beta");
    }
}
