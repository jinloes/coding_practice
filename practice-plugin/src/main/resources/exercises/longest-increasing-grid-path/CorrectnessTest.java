package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CorrectnessTest {
    @Test
    void singleCellGivesOne() {
        assertThat(Solution.longestRise(new int[][]{{7}})).as("longestRise([[7]])").isEqualTo(1);
    }

    @Test
    void singleRowRising() {
        assertThat(Solution.longestRise(new int[][]{{1, 2, 3, 4, 5}})).as("longestRise([[1, 2, 3, 4, 5]])").isEqualTo(5);
    }

    @Test
    void singleColumnFalling() {
        assertThat(Solution.longestRise(new int[][]{{5}, {4}, {3}, {2}, {1}})).as("longestRise([[5], [4], [3], [2], [1]])").isEqualTo(5);
    }

    @Test
    void pathMustGoUpAndLeft() {
        assertThat(Solution.longestRise(new int[][]{{9, 8, 7}, {6, 5, 4}, {3, 2, 1}})).as("longestRise([[9, 8, 7], [6, 5, 4], [3, 2, 1]])").isEqualTo(5);
    }

    @Test
    void equalValuesBlockPath() {
        assertThat(Solution.longestRise(new int[][]{{2, 2, 2}, {2, 3, 2}, {2, 2, 2}})).as("longestRise([[2, 2, 2], [2, 3, 2], [2, 2, 2]])").isEqualTo(2);
    }

    @Test
    void bendingPath() {
        assertThat(Solution.longestRise(new int[][]{{9, 9, 4}, {6, 6, 8}, {2, 1, 1}})).as("longestRise([[9, 9, 4], [6, 6, 8], [2, 1, 1]])").isEqualTo(4);
    }

    @Test
    void negativeValues() {
        assertThat(Solution.longestRise(new int[][]{{-5, -3}, {-4, -9}})).as("longestRise([[-5, -3], [-4, -9]])").isEqualTo(2);
    }

    @Test
    void extremeValues() {
        assertThat(Solution.longestRise(new int[][]{{2147483647, -2147483648}, {0, 2147483646}})).as("longestRise([[2147483647, -2147483648], [0, 2147483646]])").isEqualTo(2);
    }

    @Test
    void randomGrid() {
        assertThat(Solution.longestRise(new int[][]{{7, 8, 3, 9, 2}, {9, 1, 8, 0, 7}, {3, 2, 9, 7, 9}, {7, 4, 8, 5, 6}})).as("longestRise([[7, 8, 3, 9, 2], [9, 1, 8, 0, 7], [3, 2, 9, 7, 9], [7, 4, 8, 5, 6]])").isEqualTo(5);
    }

    @Test
    void spiralShape() {
        assertThat(Solution.longestRise(new int[][]{{1, 2, 3, 4}, {12, 13, 14, 5}, {11, 16, 15, 6}, {10, 9, 8, 7}})).as("longestRise([[1, 2, 3, 4], [12, 13, 14, 5], [11, 16, 15, 6], [10, 9, 8, 7]])").isEqualTo(16);
    }
}
