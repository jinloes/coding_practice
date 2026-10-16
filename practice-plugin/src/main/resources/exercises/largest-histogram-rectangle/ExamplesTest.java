package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

class ExamplesTest {
    @Test
    void findsTheWidestUsableBars() {
        check(new int[]{3, 1, 4, 4, 2}, 8);
    }

    @Test
    void equalBarsFormOneRectangle() {
        check(new int[]{7, 7, 7, 7}, 28);
    }

    @Test
    void noBarsHaveNoArea() {
        check(new int[]{}, 0);
    }

    private static void check(int[] heights, long expected) {
        int[] original = heights.clone();
        String call = "largestRectangle(%s)".formatted(Arrays.toString(original));
        assertThat(Solution.largestRectangle(heights)).as(call).isEqualTo(expected);
        assertThat(heights).as(call + " must not change the input").containsExactly(original);
    }
}
