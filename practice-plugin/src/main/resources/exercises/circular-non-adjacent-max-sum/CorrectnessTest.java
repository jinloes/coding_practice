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
    void twoValuesTakeLarger() {
        assertResult(new int[]{3, 8}, 8L);
    }

    @Test
    void allZeros() {
        assertResult(new int[]{0, 0, 0}, 0L);
    }

    @Test
    void firstAndLastAreNeighbours() {
        assertResult(new int[]{5, 1, 1, 5}, 6L);
    }

    @Test
    void fiveValues() {
        assertResult(new int[]{10, 1, 1, 1, 10}, 11L);
    }

    @Test
    void largeMiddleValue() {
        assertResult(new int[]{1, 1, 9, 1, 1}, 10L);
    }

    @Test
    void twoMiddleCandidates() {
        assertResult(new int[]{4, 9, 4, 9}, 18L);
    }

    @Test
    void totalExceedsIntRange() {
        assertResult(new int[]{2147483647, 0, 2147483647, 0, 0}, 4294967294L);
    }

    @Test
    void threeEqualValues() {
        assertResult(new int[]{4, 4, 4}, 4L);
    }

    @Test
    void longRingOfOnes() {
        assertResult(ones(1001), 500L);
    }

    private static void assertResult(int[] values, long expected) {
        int[] copy = values.clone();
        long actual = Solution.maxSum(values);
        assertThat(actual).as("maxSum(%s)", Arrays.toString(copy)).isEqualTo(expected);
        assertThat(values).as("maxSum must not mutate its input").containsExactly(copy);
    }

    private static int[] ones(int length) {
        int[] values = new int[length];
        Arrays.fill(values, 1);
        return values;
    }
}
