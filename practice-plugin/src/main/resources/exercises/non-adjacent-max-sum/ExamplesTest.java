package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

class ExamplesTest {
    @Test
    void picksFirstAndThird() {
        assertResult(new int[]{4, 1, 7, 3}, 11L);
    }

    @Test
    void singleValue() {
        assertResult(new int[]{5}, 5L);
    }

    @Test
    void prefersLargeMiddle() {
        assertResult(new int[]{2, 9, 2}, 9L);
    }

    private static void assertResult(int[] values, long expected) {
        int[] copy = values.clone();
        long actual = Solution.maxSum(values);
        assertThat(actual).as("maxSum(%s)", Arrays.toString(copy)).isEqualTo(expected);
        assertThat(values).as("maxSum must not mutate its input").containsExactly(copy);
    }
}
