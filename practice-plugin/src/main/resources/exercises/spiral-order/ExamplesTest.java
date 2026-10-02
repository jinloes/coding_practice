package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ExamplesTest {
    @Test
    void walksASquareMatrix() {
        assertThat(Solution.spiralOrder(new int[][]{{1, 2, 3}, {4, 5, 6}, {7, 8, 9}})).containsExactly(1, 2, 3, 6, 9, 8, 7, 4, 5);
    }

    @Test
    void walksAWideMatrix() {
        assertThat(Solution.spiralOrder(new int[][]{{1, 2, 3, 4}, {5, 6, 7, 8}, {9, 10, 11, 12}})).containsExactly(1, 2, 3, 4, 8, 12, 11, 10, 9, 5, 6, 7);
    }

    @Test
    void walksASingleColumn() {
        assertThat(Solution.spiralOrder(new int[][]{{1}, {2}, {3}})).containsExactly(1, 2, 3);
    }
}
