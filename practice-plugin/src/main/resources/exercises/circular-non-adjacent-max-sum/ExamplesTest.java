package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

class ExamplesTest {
    @Test
    void endsConflict() {
        assertResult(new int[]{2, 3, 2}, 3L);
    }

    @Test
    void typicalRing() {
        assertResult(new int[]{1, 2, 3, 1}, 4L);
    }

    @Test
    void singleValue() {
        assertResult(new int[]{6}, 6L);
    }

    private static void assertResult(int[] values, long expected) {
        int[] copy = values.clone();
        long actual = Solution.maxSum(values);
        assertThat(actual).as("maxSum(%s)", Arrays.toString(copy)).isEqualTo(expected);
        assertThat(values).as("maxSum must not mutate its input").containsExactly(copy);
    }
}
