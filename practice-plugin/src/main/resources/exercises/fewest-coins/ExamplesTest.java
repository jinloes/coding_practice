package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ExamplesTest {
    @Test
    void prefersFewerCoinsOverGreedyChoice() {
        assertThat(Solution.minCoins(new int[]{1, 3, 4}, 6)).as("minCoins([1, 3, 4], 6)").isEqualTo(2);
    }

    @Test
    void reportsAnUnreachableAmount() {
        assertThat(Solution.minCoins(new int[]{5, 10}, 3)).as("minCoins([5, 10], 3)").isEqualTo(-1);
    }

    @Test
    void needsNoCoinsForZero() {
        assertThat(Solution.minCoins(new int[]{7}, 0)).as("minCoins([7], 0)").isEqualTo(0);
    }
}
