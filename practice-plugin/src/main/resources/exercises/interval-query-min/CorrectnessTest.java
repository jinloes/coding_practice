package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;

class CorrectnessTest {
    @Test
    void keepsQueryOrder() {
        check(new int[][]{{1, 3}, {5, 9}}, new int[]{7, 2, 100, 5, 1}, 5, 3, -1, 5, 3);
    }

    @Test
    void touchingIntervalsPickSmaller() {
        check(new int[][]{{1, 3}, {3, 6}}, new int[]{0, 3, 6}, -1, 3, 4);
    }

    @Test
    void singlePointInterval() {
        check(new int[][]{{5, 5}}, new int[]{4, 5, 6}, -1, 1, -1);
    }

    @Test
    void duplicateIntervalsAndQueries() {
        check(new int[][]{{2, 4}, {2, 4}, {1, 9}}, new int[]{3, 3, 8}, 3, 3, 9);
    }

    @Test
    void expiredShortIntervalDoesNotWin() {
        check(new int[][]{{1, 2}, {1, 100}}, new int[]{50}, 100);
    }

    @Test
    void noQueries() {
        check(new int[][]{{1, 2}}, new int[]{});
    }

    @Test
    void doesNotMutateInputs() {
        int[][] intervals = {{8, 9}, {2, 5}};
        int[] queries = {9, 2};
        Solution.smallestCovering(intervals, queries);
        assertThat(Arrays.deepToString(intervals)).as("intervals").isEqualTo("[[8, 9], [2, 5]]");
        assertThat(queries).as("queries").containsExactly(9, 2);
    }

    @Test
    void matchesBruteForceOnRandomData() {
        Random random = new Random(7);
        int[][] intervals = new int[300][];
        for (int i = 0; i < intervals.length; i++) {
            int start = 1 + random.nextInt(500);
            intervals[i] = new int[]{start, start + random.nextInt(60)};
        }
        int[] queries = new int[400];
        for (int i = 0; i < queries.length; i++) {
            queries[i] = 1 + random.nextInt(600);
        }
        assertThat(Solution.smallestCovering(intervals, queries)).isEqualTo(bruteForce(intervals, queries));
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
