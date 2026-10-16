package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

class ExamplesTest {
    @Test
    void popsThreeBalloons() {
        check(new int[]{3, 1, 5}, 35);
    }

    @Test
    void singleBalloonUsesImplicitOnes() {
        check(new int[]{7}, 7);
    }

    @Test
    void emptyRowEarnsNothing() {
        check(new int[]{}, 0);
    }

    private static void check(int[] values, int expected) {
        int[] copy = values.clone();
        String call = "maxCoins(%s)".formatted(Arrays.toString(values));
        assertThat(Solution.maxCoins(values)).as(call).isEqualTo(expected);
        assertThat(values).as(call + " must not modify its input").containsExactly(copy);
    }
}
