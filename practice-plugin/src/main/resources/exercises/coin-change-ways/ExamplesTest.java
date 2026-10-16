package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ExamplesTest {
    @Test
    void countsCombinationsNotOrderings() {
        assertThat(Solution.countWays(4, new int[]{1, 2, 3})).as("countWays(4, [1, 2, 3])").isEqualTo(4);
    }

    @Test
    void unreachableAmountHasNoWays() {
        assertThat(Solution.countWays(3, new int[]{2})).as("countWays(3, [2])").isEqualTo(0);
    }

    @Test
    void zeroAmountHasOneWay() {
        assertThat(Solution.countWays(0, new int[]{5, 7})).as("countWays(0, [5, 7])").isEqualTo(1);
    }
}
