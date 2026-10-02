package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CorrectnessTest {
    @Test
    void handlesAnEmptyMatrix() {
        int[][] matrix = new int[0][];
        Solution.zeroLines(matrix);
        assertThat(matrix).isEmpty();
    }

    @Test
    void handlesASingleRow() {
        int[][] matrix = {{4, 0, 6}};
        Solution.zeroLines(matrix);
        assertThat(matrix).isDeepEqualTo(new int[][]{{0, 0, 0}});
    }

    @Test
    void handlesASingleColumn() {
        int[][] matrix = {{4}, {5}, {0}, {7}};
        Solution.zeroLines(matrix);
        assertThat(matrix).isDeepEqualTo(new int[][]{{0}, {0}, {0}, {0}});
    }

    @Test
    void handlesAZeroInTheBottomRightCorner() {
        int[][] matrix = {{1, 2, 3}, {4, 5, 6}, {7, 8, 0}};
        Solution.zeroLines(matrix);
        assertThat(matrix).isDeepEqualTo(new int[][]{{1, 2, 0}, {4, 5, 0}, {0, 0, 0}});
    }

    @Test
    void doesNotCascadeFromCellsThatWereZeroed() {
        int[][] matrix = {{1, 1, 1, 1}, {1, 0, 1, 1}, {1, 1, 1, 1}, {1, 1, 1, 1}};
        Solution.zeroLines(matrix);
        assertThat(matrix).isDeepEqualTo(new int[][]{{1, 0, 1, 1}, {0, 0, 0, 0}, {1, 0, 1, 1}, {1, 0, 1, 1}});
    }

    @Test
    void handlesSeveralZerosInDifferentLines() {
        int[][] matrix = {{1, 2, 3, 0}, {4, 5, 6, 7}, {0, 8, 9, 1}, {2, 3, 4, 5}};
        Solution.zeroLines(matrix);
        assertThat(matrix).isDeepEqualTo(new int[][]{{0, 0, 0, 0}, {0, 5, 6, 0}, {0, 0, 0, 0}, {0, 3, 4, 0}});
    }

    @Test
    void keepsNegativeValuesOutsideZeroedLines() {
        int[][] matrix = {{-1, -2, -3}, {-4, 0, -6}, {-7, -8, -9}};
        Solution.zeroLines(matrix);
        assertThat(matrix).isDeepEqualTo(new int[][]{{-1, 0, -3}, {0, 0, 0}, {-7, 0, -9}});
    }

    @Test
    void handlesAMatrixOfOnlyZeros() {
        int[][] matrix = {{0, 0}, {0, 0}};
        Solution.zeroLines(matrix);
        assertThat(matrix).isDeepEqualTo(new int[][]{{0, 0}, {0, 0}});
    }

    @Test
    void handlesZerosInTheFirstRowAndFirstColumnTogether() {
        int[][] matrix = {{1, 0, 3}, {0, 5, 6}, {7, 8, 9}};
        Solution.zeroLines(matrix);
        assertThat(matrix).isDeepEqualTo(new int[][]{{0, 0, 0}, {0, 0, 0}, {0, 0, 9}});
    }

    @Test
    void handlesAZeroOnlyInTheFirstColumnOfALaterRow() {
        int[][] matrix = {{1, 2, 3}, {4, 5, 6}, {0, 8, 9}};
        Solution.zeroLines(matrix);
        assertThat(matrix).isDeepEqualTo(new int[][]{{0, 2, 3}, {0, 5, 6}, {0, 0, 0}});
    }
}
