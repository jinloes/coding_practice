package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

class CorrectnessTest {
    @Test
    void needsNoRemovalsForEmptyInput() {
        check(iv(), 0);
    }

    @Test
    void needsNoRemovalsForOneInterval() {
        check(iv(3, 9), 0);
    }

    @Test
    void removesDuplicatesButOne() {
        check(iv(1, 2, 1, 2, 1, 2), 2);
    }

    @Test
    void removesAllButOneWhenAllNest() {
        check(iv(1, 10, 2, 9, 3, 8, 4, 7), 3);
    }

    @Test
    void keepsLongChainOfTouchingIntervals() {
        check(iv(3, 4, 1, 2, 4, 5, 2, 3), 0);
    }

    @Test
    void prefersEarlyEndingIntervals() {
        check(iv(1, 100, 1, 2, 2, 3, 3, 4), 1);
    }

    @Test
    void handlesUnsortedInput() {
        check(iv(5, 8, 1, 3, 2, 6, 6, 9, 3, 5), 2);
    }

    @Test
    void handlesNegativeValues() {
        check(iv(-5, -1, -3, -2, -2, 0, -1, 1), 2);
    }

    @Test
    void comparesExtremeValuesWithoutOverflow() {
        check(iv(Integer.MIN_VALUE, Integer.MAX_VALUE, 0, 1, Integer.MAX_VALUE - 1, Integer.MAX_VALUE), 1);
        check(iv(Integer.MAX_VALUE - 1, Integer.MAX_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE + 1), 0);
    }

    @Test
    void doesNotMutateTheInput() {
        int[][] intervals = iv(5, 8, 1, 3, 2, 6);
        Solution.minRemovals(intervals);
        assertThat(intervals).as("input must stay unchanged and unsorted").isDeepEqualTo(iv(5, 8, 1, 3, 2, 6));
    }

    private static void check(int[][] intervals, int expected) {
        assertThat(Solution.minRemovals(intervals))
                .as("minRemovals(%s)", Arrays.deepToString(intervals))
                .isEqualTo(expected);
    }

    private static int[][] iv(int... flat) {
        int[][] result = new int[flat.length / 2][];
        for (int i = 0; i < result.length; i++) {
            result[i] = new int[]{flat[2 * i], flat[2 * i + 1]};
        }
        return result;
    }
}
