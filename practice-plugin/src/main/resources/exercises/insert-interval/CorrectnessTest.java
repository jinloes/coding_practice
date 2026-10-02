package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

class CorrectnessTest {
    @Test
    void insertsBeforeEverything() {
        check(iv(5, 6, 8, 9), new int[]{1, 2}, iv(1, 2, 5, 6, 8, 9));
    }

    @Test
    void insertsAfterEverything() {
        check(iv(1, 2, 3, 4), new int[]{6, 7}, iv(1, 2, 3, 4, 6, 7));
    }

    @Test
    void mergesWhenEndpointsTouchOnTheLeft() {
        check(iv(1, 2, 6, 7), new int[]{2, 4}, iv(1, 4, 6, 7));
    }

    @Test
    void mergesWhenEndpointsTouchOnTheRight() {
        check(iv(1, 2, 6, 7), new int[]{4, 6}, iv(1, 2, 4, 7));
    }

    @Test
    void swallowsEveryInterval() {
        check(iv(2, 3, 4, 5, 6, 7), new int[]{0, 10}, iv(0, 10));
    }

    @Test
    void isAbsorbedByALargerInterval() {
        check(iv(1, 10), new int[]{3, 4}, iv(1, 10));
    }

    @Test
    void bridgesSeveralMiddleIntervals() {
        check(iv(1, 2, 3, 4, 5, 6, 7, 8, 9, 10), new int[]{4, 7}, iv(1, 2, 3, 8, 9, 10));
    }

    @Test
    void handlesPointIntervalsAndDuplicates() {
        check(iv(2, 2, 5, 5), new int[]{2, 2}, iv(2, 2, 5, 5));
        check(iv(1, 3), new int[]{3, 3}, iv(1, 3));
    }

    @Test
    void handlesNegativeAndExtremeValues() {
        check(iv(-9, -5, Integer.MAX_VALUE - 1, Integer.MAX_VALUE), new int[]{Integer.MIN_VALUE, -6}, iv(Integer.MIN_VALUE, -5, Integer.MAX_VALUE - 1, Integer.MAX_VALUE));
    }

    @Test
    void doesNotMutateInputs() {
        int[][] intervals = iv(1, 3, 6, 9);
        int[] added = {2, 5};
        Solution.insert(intervals, added);
        assertThat(intervals).as("intervals must stay unchanged").isDeepEqualTo(iv(1, 3, 6, 9));
        assertThat(added).as("newInterval must stay unchanged").containsExactly(2, 5);
    }

    private static void check(int[][] intervals, int[] newInterval, int[][] expected) {
        assertThat(Solution.insert(intervals, newInterval))
                .as("insert(%s, %s)", Arrays.deepToString(intervals), Arrays.toString(newInterval))
                .isDeepEqualTo(expected);
    }

    private static int[][] iv(int... flat) {
        int[][] result = new int[flat.length / 2][];
        for (int i = 0; i < result.length; i++) {
            result[i] = new int[]{flat[2 * i], flat[2 * i + 1]};
        }
        return result;
    }
}
