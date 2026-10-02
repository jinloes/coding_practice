package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ExamplesTest {
    @Test
    void countsASingleStair() {
        assertThat(Solution.countWays(1)).as("countWays(1)").isEqualTo(1L);
    }

    @Test
    void countsFourStairs() {
        assertThat(Solution.countWays(4)).as("countWays(4)").isEqualTo(5L);
    }

    @Test
    void countsTenStairs() {
        assertThat(Solution.countWays(10)).as("countWays(10)").isEqualTo(89L);
    }
}
