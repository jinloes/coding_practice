package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ExamplesTest {
    @Test
    void clearsTheRowAndColumnOfAMiddleZero() {
        int[][] matrix = {{1, 1, 1}, {1, 0, 1}, {1, 1, 1}};
        Solution.zeroLines(matrix);
        assertThat(matrix).isDeepEqualTo(new int[][]{{1, 0, 1}, {0, 0, 0}, {1, 0, 1}});
    }

    @Test
    void clearsFromAZeroInTheFirstRowAndColumn() {
        int[][] matrix = {{0, 2, 3, 4}, {5, 6, 7, 8}, {9, 10, 11, 12}};
        Solution.zeroLines(matrix);
        assertThat(matrix).isDeepEqualTo(new int[][]{{0, 0, 0, 0}, {0, 6, 7, 8}, {0, 10, 11, 12}});
    }

    @Test
    void leavesAMatrixWithoutZerosUnchanged() {
        int[][] matrix = {{1, 2}, {3, 4}};
        Solution.zeroLines(matrix);
        assertThat(matrix).isDeepEqualTo(new int[][]{{1, 2}, {3, 4}});
    }
}
