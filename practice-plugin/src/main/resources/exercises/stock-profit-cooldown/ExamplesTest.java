package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ExamplesTest {
    @Test
    void tradesAroundTheCooldown() {
        assertThat(Solution.maxProfit(new int[]{1, 2, 3, 0, 2})).as("maxProfit([1, 2, 3, 0, 2])").isEqualTo(3);
    }

    @Test
    void fallingPricesGiveNoProfit() {
        assertThat(Solution.maxProfit(new int[]{5, 4, 3})).as("maxProfit([5, 4, 3])").isEqualTo(0);
    }

    @Test
    void holdingBeatsEarlySale() {
        assertThat(Solution.maxProfit(new int[]{1, 4, 2, 7})).as("maxProfit([1, 4, 2, 7])").isEqualTo(6);
    }
}
