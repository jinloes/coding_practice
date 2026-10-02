package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

class ExamplesTest {
    @Test
    void mergesOverlappingInterval() {
        check(iv(1, 3, 6, 9), new int[]{2, 5}, iv(1, 5, 6, 9));
    }

    @Test
    void insertsIntoEmptyList() {
        check(iv(), new int[]{4, 7}, iv(4, 7));
    }

    @Test
    void insertsIntoGapWithoutMerging() {
        check(iv(1, 2, 5, 6), new int[]{3, 4}, iv(1, 2, 3, 4, 5, 6));
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
