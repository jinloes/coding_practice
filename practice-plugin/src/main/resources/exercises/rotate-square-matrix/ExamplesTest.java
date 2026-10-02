package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ExamplesTest {
    @Test
    void rotatesATwoByTwoMatrix() {
        int[][] matrix = {{1, 2}, {3, 4}};
        Solution.rotateClockwise(matrix);
        assertThat(matrix).isDeepEqualTo(new int[][]{{3, 1}, {4, 2}});
    }

    @Test
    void rotatesAThreeByThreeMatrix() {
        int[][] matrix = {{1, 2, 3}, {4, 5, 6}, {7, 8, 9}};
        Solution.rotateClockwise(matrix);
        assertThat(matrix).isDeepEqualTo(new int[][]{{7, 4, 1}, {8, 5, 2}, {9, 6, 3}});
    }

    @Test
    void leavesASingleCellAlone() {
        int[][] matrix = {{5}};
        Solution.rotateClockwise(matrix);
        assertThat(matrix).isDeepEqualTo(new int[][]{{5}});
    }
}
