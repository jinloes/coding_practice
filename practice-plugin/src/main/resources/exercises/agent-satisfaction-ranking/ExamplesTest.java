package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ExamplesTest {
    @Test
    void ordersAgentsByExactAverageScore() {
        Solution tracker = new Solution();
        tracker.recordScore("mia", 5);
        tracker.recordScore("ben", 4);
        tracker.recordScore("mia", 4);
        tracker.recordScore("ben", 5);
        tracker.recordScore("ben", 5);

        assertThat(tracker.qualifiedAgents(2)).containsExactly("ben", "mia");
    }

    @Test
    void excludesAgentsBelowMinimumRatingThreshold() {
        Solution tracker = new Solution();
        tracker.recordScore("zoe", 4);
        tracker.recordScore("zoe", 4);
        tracker.recordScore("ava", 4);
        tracker.recordScore("kai", 5);

        assertThat(tracker.qualifiedAgents(2)).containsExactly("zoe");
    }

    @Test
    void breaksAverageTiesByCountThenAlphabetically() {
        Solution tracker = new Solution();
        tracker.recordScore("bob", 5);
        tracker.recordScore("bob", 3);
        tracker.recordScore("ada", 4);
        tracker.recordScore("ada", 4);
        tracker.recordScore("cam", 4);

        assertThat(tracker.qualifiedAgents(1)).containsExactly("ada", "bob", "cam");
    }
}
