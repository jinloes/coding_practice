package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

class ExamplesTest {
    @Test
    void recolorsTheConnectedRegion() {
        check(new int[][]{{2, 2, 2}, {2, 2, 0}, {2, 0, 1}}, new int[][]{{1, 1, 1}, {1, 1, 0}, {1, 0, 1}}, 1, 1, 2);
    }

    @Test
    void sameColorLeavesTheImageUnchanged() {
        check(new int[][]{{0, 0}, {0, 0}}, new int[][]{{0, 0}, {0, 0}}, 0, 0, 0);
    }

    @Test
    void diagonalPixelsAreNotConnected() {
        check(new int[][]{{1, 2, 1}, {2, 7, 2}, {1, 2, 1}}, new int[][]{{1, 2, 1}, {2, 1, 2}, {1, 2, 1}}, 1, 1, 7);
    }

    private static void check(int[][] expected, int[][] image, int row, int col, int color) {
        String call = "recolor(%s, %d, %d, %d)".formatted(Arrays.deepToString(image), row, col, color);
        assertThat(Solution.recolor(image, row, col, color)).as(call).isDeepEqualTo(expected);
    }
}
