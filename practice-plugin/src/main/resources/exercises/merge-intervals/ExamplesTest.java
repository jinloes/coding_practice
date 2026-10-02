package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

class ExamplesTest {
    @Test
    void mergesUnsortedOverlaps() {
        check(iv(8, 10, 1, 3, 2, 6), iv(1, 6, 8, 10));
    }

    @Test
    void mergesTouchingEndpoints() {
        check(iv(1, 4, 4, 5), iv(1, 5));
    }

    @Test
    void keepsSingleInterval() {
        check(iv(5, 6), iv(5, 6));
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
