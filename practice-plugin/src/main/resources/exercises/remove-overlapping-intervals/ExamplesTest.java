package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

class ExamplesTest {
    @Test
    void removesTwoFromOverlappingChain() {
        check(iv(1, 4, 2, 3, 3, 5, 4, 6), 2);
    }

    @Test
    void keepsTouchingIntervals() {
        check(iv(1, 2, 2, 3), 0);
    }

    @Test
    void removesTheLongCoveringInterval() {
        check(iv(0, 5, 1, 2, 3, 4), 1);
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
