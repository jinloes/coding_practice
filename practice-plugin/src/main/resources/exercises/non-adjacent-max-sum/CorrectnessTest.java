package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

class CorrectnessTest {
    @Test
    void emptyArrayGivesZero() {
        assertResult(new int[0], 0L);
    }

    @Test
    void singleValue() {
        assertResult(new int[]{7}, 7L);
    }

    @Test
    void twoValuesTakeLarger() {
        assertResult(new int[]{3, 9}, 9L);
    }

    @Test
    void allZeros() {
        assertResult(new int[]{0, 0, 0, 0}, 0L);
    }

    @Test
    void mayNeedAGapOfTwo() {
        assertResult(new int[]{5, 1, 1, 5}, 10L);
    }

    @Test
    void doesNotJustAlternate() {
        assertResult(new int[]{1, 100, 1, 1, 100, 1}, 200L);
    }

    @Test
    void ascendingValues() {
        assertResult(new int[]{1, 2, 3, 4, 5, 6}, 12L);
    }

    @Test
    void middleBeatsEnds() {
        assertResult(new int[]{4, 5, 4}, 8L);
    }

    @Test
    void totalExceedsIntRange() {
        assertResult(new int[]{2147483647, 0, 2147483647}, 4294967294L);
    }

    @Test
    void adjacentLargeValuesCannotBothBeTaken() {
        assertResult(new int[]{2147483647, 2147483647}, 2147483647L);
    }

    @Test
    void equalValues() {
        assertResult(new int[]{3, 3, 3, 3, 3}, 9L);
    }

    private static void assertResult(int[] values, long expected) {
        int[] copy = values.clone();
        long actual = Solution.maxSum(values);
        assertThat(actual).as("maxSum(%s)", Arrays.toString(copy)).isEqualTo(expected);
        assertThat(values).as("maxSum must not mutate its input").containsExactly(copy);
    }
}
