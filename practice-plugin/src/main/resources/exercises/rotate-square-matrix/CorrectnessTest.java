package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CorrectnessTest {
    @Test
    void handlesAnEmptyMatrix() {
        int[][] matrix = new int[0][];
        Solution.rotateClockwise(matrix);
        assertThat(matrix).isEmpty();
    }

    @Test
    void rotatesAFourByFourMatrix() {
        int[][] matrix = {{1, 2, 3, 4}, {5, 6, 7, 8}, {9, 10, 11, 12}, {13, 14, 15, 16}};
        Solution.rotateClockwise(matrix);
        assertThat(matrix).isDeepEqualTo(new int[][]{{13, 9, 5, 1}, {14, 10, 6, 2}, {15, 11, 7, 3}, {16, 12, 8, 4}});
    }

    @Test
    void rotatesAFiveByFiveMatrixWithAFixedCentre() {
        int[][] matrix = {{1, 2, 3, 4, 5}, {6, 7, 8, 9, 10}, {11, 12, 13, 14, 15}, {16, 17, 18, 19, 20}, {21, 22, 23, 24, 25}};
        Solution.rotateClockwise(matrix);
        assertThat(matrix).isDeepEqualTo(new int[][]{{21, 16, 11, 6, 1}, {22, 17, 12, 7, 2}, {23, 18, 13, 8, 3}, {24, 19, 14, 9, 4}, {25, 20, 15, 10, 5}});
    }

    @Test
    void handlesNegativeAndDuplicateValues() {
        int[][] matrix = {{-1, -1}, {0, 7}};
        Solution.rotateClockwise(matrix);
        assertThat(matrix).isDeepEqualTo(new int[][]{{0, -1}, {7, -1}});
    }

    @Test
    void handlesExtremeValues() {
        int[][] matrix = {{Integer.MIN_VALUE, Integer.MAX_VALUE}, {0, -1}};
        Solution.rotateClockwise(matrix);
        assertThat(matrix).isDeepEqualTo(new int[][]{{0, Integer.MIN_VALUE}, {-1, Integer.MAX_VALUE}});
    }

    @Test
    void fourRotationsRestoreTheOriginal() {
        int[][] matrix = {{1, 2, 3}, {4, 5, 6}, {7, 8, 9}};
        for (int turn = 0; turn < 4; turn++) {
            Solution.rotateClockwise(matrix);
        }
        assertThat(matrix).isDeepEqualTo(new int[][]{{1, 2, 3}, {4, 5, 6}, {7, 8, 9}});
    }

    @Test
    void rotatesTheSameArrayObjectsInPlace() {
        int[][] matrix = {{1, 2}, {3, 4}};
        int[][] sameOuter = matrix;
        Solution.rotateClockwise(matrix);
        assertThat(matrix).isSameAs(sameOuter);
        assertThat(matrix).isDeepEqualTo(new int[][]{{3, 1}, {4, 2}});
    }

    @Test
    void rotatesAnotherSizeAgainstTheDefinition() {
        int n = 6;
        int[][] matrix = new int[n][n];
        for (int r = 0; r < n; r++) {
            for (int c = 0; c < n; c++) {
                matrix[r][c] = r * 10 + c;
            }
        }
        Solution.rotateClockwise(matrix);
        for (int r = 0; r < n; r++) {
            for (int c = 0; c < n; c++) {
                assertThat(matrix[r][c]).as("cell (%d, %d)", r, c).isEqualTo((n - 1 - c) * 10 + r);
            }
        }
    }
}
