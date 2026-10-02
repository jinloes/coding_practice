package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

class CorrectnessTest {
    @Test
    void returnsEmptyForEmptyInput() {
        check(iv(), iv());
    }

    @Test
    void keepsDisjointIntervalsSorted() {
        check(iv(7, 8, 1, 2, 4, 5), iv(1, 2, 4, 5, 7, 8));
    }

    @Test
    void mergesIdenticalIntervals() {
        check(iv(2, 3, 2, 3, 2, 3), iv(2, 3));
    }

    @Test
    void absorbsNestedIntervals() {
        check(iv(1, 10, 2, 3, 4, 5, 6, 9), iv(1, 10));
    }

    @Test
    void mergesChainOfOverlaps() {
        check(iv(5, 7, 1, 3, 3, 5, 7, 9), iv(1, 9));
    }

    @Test
    void doesNotMergeIntervalsWithAGapOfOne() {
        check(iv(1, 2, 3, 4), iv(1, 2, 3, 4));
    }

    @Test
    void handlesPointIntervals() {
        check(iv(4, 4, 1, 1, 4, 4), iv(1, 1, 4, 4));
    }

    @Test
    void handlesNegativeValues() {
        check(iv(-3, -1, -10, -4, -2, 2), iv(-10, -4, -3, 2));
    }

    @Test
    void sortsExtremeStartsWithoutOverflow() {
        check(iv(Integer.MAX_VALUE - 1, Integer.MAX_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE + 1), iv(Integer.MIN_VALUE, Integer.MIN_VALUE + 1, Integer.MAX_VALUE - 1, Integer.MAX_VALUE));
        check(iv(Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MIN_VALUE, 0, -1, 5), iv(Integer.MIN_VALUE, 5, Integer.MAX_VALUE, Integer.MAX_VALUE));
    }

    @Test
    void doesNotMutateTheInput() {
        int[][] intervals = iv(8, 10, 1, 3, 2, 6);
        Solution.merge(intervals);
        assertThat(intervals).as("input must stay unchanged and unsorted").isDeepEqualTo(iv(8, 10, 1, 3, 2, 6));
    }

    private static void check(int[][] intervals, int[][] expected) {
        int[][] snapshot = Arrays.stream(intervals).map(int[]::clone).toArray(int[][]::new);
        assertThat(Solution.merge(intervals))
                .as("merge(%s)", Arrays.deepToString(snapshot))
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
