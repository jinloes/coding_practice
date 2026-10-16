package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CorrectnessTest {
    @Test
    void emptyPricesGiveZero() {
        assertThat(Solution.maxProfit(new int[]{})).as("maxProfit([])").isEqualTo(0);
    }

    @Test
    void singleDayGivesZero() {
        assertThat(Solution.maxProfit(new int[]{9})).as("maxProfit([9])").isEqualTo(0);
    }

    @Test
    void risingPrices() {
        assertThat(Solution.maxProfit(new int[]{1, 2, 3, 4, 5})).as("maxProfit([1, 2, 3, 4, 5])").isEqualTo(4);
    }

    @Test
    void cooldownBlocksImmediateRebuy() {
        assertThat(Solution.maxProfit(new int[]{1, 5, 2, 6})).as("maxProfit([1, 5, 2, 6])").isEqualTo(5);
    }

    @Test
    void zigzagPrices() {
        assertThat(Solution.maxProfit(new int[]{3, 8, 2, 9, 1, 10})).as("maxProfit([3, 8, 2, 9, 1, 10])").isEqualTo(14);
    }

    @Test
    void flatPrices() {
        assertThat(Solution.maxProfit(new int[]{4, 4, 4, 4})).as("maxProfit([4, 4, 4, 4])").isEqualTo(0);
    }

    @Test
    void longFallingRun() {
        assertThat(Solution.maxProfit(new int[]{30, 29, 28, 27, 26, 25, 24, 23, 22, 21, 20, 19, 18, 17, 16, 15, 14, 13, 12, 11, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1})).as("maxProfit([30, 29, 28, 27, 26, 25, 24, 23, 22, 21, 20, 19, 18, 17, 16, 15, 14, 13, 12, 11, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1])").isEqualTo(0);
    }

    @Test
    void randomMovement() {
        assertThat(Solution.maxProfit(new int[]{15, 14, 12, 15, 18, 6, 12, 2, 15, 7, 0, 8, 16, 13, 15, 12})).as("maxProfit([15, 14, 12, 15, 18, 6, 12, 2, 15, 7, 0, 8, 16, 13, 15, 12])").isEqualTo(35);
    }

    @Test
    void skippingASaleDayHelps() {
        assertThat(Solution.maxProfit(new int[]{6, 1, 3, 2, 7})).as("maxProfit([6, 1, 3, 2, 7])").isEqualTo(6);
    }
}
