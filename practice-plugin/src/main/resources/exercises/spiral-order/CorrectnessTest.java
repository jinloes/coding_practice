package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CorrectnessTest {
    @Test
    void returnsNothingForAnEmptyMatrix() {
        assertThat(Solution.spiralOrder(new int[0][])).isEmpty();
    }

    @Test
    void handlesASingleCell() {
        assertThat(Solution.spiralOrder(new int[][]{{7}})).containsExactly(7);
    }

    @Test
    void handlesASingleRow() {
        assertThat(Solution.spiralOrder(new int[][]{{1, 2, 3, 4}})).containsExactly(1, 2, 3, 4);
    }

    @Test
    void handlesATallMatrix() {
        assertThat(Solution.spiralOrder(new int[][]{{1, 2}, {3, 4}, {5, 6}, {7, 8}})).containsExactly(1, 2, 4, 6, 8, 7, 5, 3);
    }

    @Test
    void handlesATwoByTwoMatrix() {
        assertThat(Solution.spiralOrder(new int[][]{{1, 2}, {3, 4}})).containsExactly(1, 2, 4, 3);
    }

    @Test
    void handlesAFourByFourMatrix() {
        assertThat(Solution.spiralOrder(new int[][]{{1, 2, 3, 4}, {5, 6, 7, 8}, {9, 10, 11, 12}, {13, 14, 15, 16}})).containsExactly(1, 2, 3, 4, 8, 12, 16, 15, 14, 13, 9, 5, 6, 7, 11, 10);
    }

    @Test
    void handlesAnOddWideMatrixEndingInTheMiddle() {
        assertThat(Solution.spiralOrder(new int[][]{{1, 2, 3, 4, 5}, {6, 7, 8, 9, 10}, {11, 12, 13, 14, 15}})).containsExactly(1, 2, 3, 4, 5, 10, 15, 14, 13, 12, 11, 6, 7, 8, 9);
    }

    @Test
    void keepsDuplicateAndNegativeValues() {
        assertThat(Solution.spiralOrder(new int[][]{{-1, -1}, {0, -1}})).containsExactly(-1, -1, -1, 0);
    }

    @Test
    void doesNotMutateTheMatrix() {
        int[][] matrix = {{1, 2, 3}, {4, 5, 6}};
        Solution.spiralOrder(matrix);
        assertThat(matrix).isDeepEqualTo(new int[][]{{1, 2, 3}, {4, 5, 6}});
    }
}
