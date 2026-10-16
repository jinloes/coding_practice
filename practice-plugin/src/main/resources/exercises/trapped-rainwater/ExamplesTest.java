package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

class ExamplesTest {
    @Test
    void holdsWaterBetweenUnevenBars() {
        check(new int[]{0, 2, 0, 3, 1, 0, 2}, 5);
    }

    @Test
    void holdsWaterInASingleValley() {
        check(new int[]{3, 1, 3}, 2);
    }

    @Test
    void staircaseHoldsNothing() {
        check(new int[]{1, 2, 3}, 0);
    }

    private static void check(int[] heights, long expected) {
        int[] snapshot = heights.clone();
        String call = "trap(%s)".formatted(Arrays.toString(heights));
        assertThat(Solution.trap(heights)).as(call).isEqualTo(expected);
        assertThat(heights).as(call + " must not mutate its input").containsExactly(snapshot);
    }
}
