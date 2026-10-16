package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CorrectnessTest {
    @Test
    void zeroAmountHasOneWay() {
        assertThat(Solution.countWays(0, new int[]{})).as("countWays(0, [])").isEqualTo(1);
    }

    @Test
    void noCoinsForPositiveAmount() {
        assertThat(Solution.countWays(5, new int[]{})).as("countWays(5, [])").isEqualTo(0);
    }

    @Test
    void singleCoinDivides() {
        assertThat(Solution.countWays(12, new int[]{3})).as("countWays(12, [3])").isEqualTo(1);
    }

    @Test
    void singleCoinDoesNotDivide() {
        assertThat(Solution.countWays(10, new int[]{4})).as("countWays(10, [4])").isEqualTo(0);
    }

    @Test
    void orderDoesNotMatter() {
        assertThat(Solution.countWays(7, new int[]{1, 2, 5})).as("countWays(7, [1, 2, 5])").isEqualTo(6);
    }

    @Test
    void largeCoinsUnused() {
        assertThat(Solution.countWays(6, new int[]{1, 50, 100})).as("countWays(6, [1, 50, 100])").isEqualTo(1);
    }

    @Test
    void manyCoinValues() {
        assertThat(Solution.countWays(50, new int[]{1, 2, 5, 10, 20})).as("countWays(50, [1, 2, 5, 10, 20])").isEqualTo(450);
    }

    @Test
    void biggerAmountCounts() {
        assertThat(Solution.countWays(200, new int[]{1, 2, 5, 10, 20, 50, 100})).as("countWays(200, [1, 2, 5, 10, 20, 50, 100])").isEqualTo(73681);
    }

    @Test
    void coinEqualsAmount() {
        assertThat(Solution.countWays(9, new int[]{9})).as("countWays(9, [9])").isEqualTo(1);
    }
}
