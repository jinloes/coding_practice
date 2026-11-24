package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ExamplesTest {
    private final Solution solution = new Solution();

    @Test
    void combinesBackToBackJobs() {
        assertThat(solution.maxProfit(
                new int[]{1, 2, 3, 3},
                new int[]{3, 4, 5, 6},
                new int[]{50, 10, 40, 70}))
                .isEqualTo(120);
    }

    @Test
    void picksThreeSmallerJobsOverOneLongJob() {
        assertThat(solution.maxProfit(
                new int[]{1, 2, 3, 4, 6},
                new int[]{3, 5, 10, 6, 9},
                new int[]{20, 20, 100, 70, 60}))
                .isEqualTo(150);
    }

    @Test
    void picksBestSingleJobWhenAllOverlap() {
        assertThat(solution.maxProfit(
                new int[]{1, 1, 1},
                new int[]{2, 3, 4},
                new int[]{5, 6, 4}))
                .isEqualTo(6);
    }
}
