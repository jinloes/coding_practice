package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CorrectnessTest {
    private final Solution solution = new Solution();

    @Test
    void returnsZeroForEmptyInput() {
        assertThat(solution.maxProfit(new int[]{}, new int[]{}, new int[]{})).isEqualTo(0);
    }

    @Test
    void handlesSingleJob() {
        assertThat(solution.maxProfit(new int[]{5}, new int[]{9}, new int[]{42})).isEqualTo(42);
    }

    @Test
    void handlesUnsortedInputOrder() {
        assertThat(solution.maxProfit(
                new int[]{6, 1, 4, 3},
                new int[]{9, 3, 6, 10},
                new int[]{60, 20, 70, 100}))
                .isEqualTo(150);
    }

    @Test
    void picksSingleHighProfitJobOverMultipleLowProfitJobs() {
        assertThat(solution.maxProfit(
                new int[]{1, 3, 5, 1},
                new int[]{3, 5, 7, 7},
                new int[]{10, 10, 10, 100}))
                .isEqualTo(100);
    }

    @Test
    void handlesDuplicateIntervalsWithDifferentProfits() {
        assertThat(solution.maxProfit(
                new int[]{1, 1, 4, 4},
                new int[]{4, 4, 8, 8},
                new int[]{15, 25, 30, 10}))
                .isEqualTo(55);
    }
}
