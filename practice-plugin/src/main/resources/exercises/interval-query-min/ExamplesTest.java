package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;

class ExamplesTest {
    @Test
    void answersEachQueryWithItsSmallestInterval() {
        check(new int[][]{{2, 5}, {3, 4}, {8, 9}}, new int[]{4, 6, 9, 1}, 2, -1, 2, -1);
    }

    @Test
    void endpointsAreInclusive() {
        check(new int[][]{{1, 10}}, new int[]{1, 10, 11}, 10, 10, -1);
    }

    @Test
    void noIntervalsMeansMinusOne() {
        check(new int[][]{}, new int[]{5}, -1);
    }

    private static void check(int[][] intervals, int[] queries, int... expected) {
        String call = "smallestCovering(%s, %s)".formatted(Arrays.deepToString(intervals), Arrays.toString(queries));
        assertThat(Solution.smallestCovering(intervals, queries)).as(call).containsExactly(expected);
    }

    private static int[] bruteForce(int[][] intervals, int[] queries) {
        int[] answers = new int[queries.length];
        for (int i = 0; i < queries.length; i++) {
            int best = -1;
            for (int[] interval : intervals) {
                int size = interval[1] - interval[0] + 1;
                if (interval[0] <= queries[i] && queries[i] <= interval[1] && (best == -1 || size < best)) {
                    best = size;
                }
            }
            answers[i] = best;
        }
        return answers;
    }
}
