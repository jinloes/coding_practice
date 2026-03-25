package com.jinloes.simple_functions;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MaxSumTest {

    @Test
    void maxSum1d_simpleArray() {
        assertThat(MaxSum.findMax(new int[]{-1, 2})).isEqualTo(2);
    }

    @Test
    void maxSum1d_complexArray() {
        assertThat(MaxSum.findMax(new int[]{1, -2, 3, 10, -4, 7, 2, -5})).isEqualTo(18);
    }

    @Test
    void maxSum2d_simpleArray() {
        assertThat(MaxSum.findMax(new int[][]{{2, 0}, {3, 1}})).isEqualTo(6);
    }

    @Test
    void maxSum2d_complexArray() {
        assertThat(MaxSum.findMax(new int[][]{
            {5, 8, 12, 1},
            {9, 10, 14, 5},
            {2, 3, 10, 8},
            {11, 10, 2, 6}
        })).isEqualTo(63);
    }
}