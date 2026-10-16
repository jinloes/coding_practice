package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CorrectnessTest {
    @Test
    void singleRow() {
        assertThat(Solution.swimTime(new int[][]{{3, 1, 4, 1, 5}}))
                .as("swimTime of a 1x5 grid")
                .isEqualTo(5);
    }

    @Test
    void singleColumn() {
        assertThat(Solution.swimTime(new int[][]{{2}, {7}, {1}, {3}}))
                .as("swimTime of a 4x1 grid")
                .isEqualTo(7);
    }

    @Test
    void routeMustGoUpAndLeft() {
        assertThat(Solution.swimTime(new int[][]{{0, 9, 0, 0, 0}, {0, 9, 0, 9, 0}, {0, 9, 0, 9, 0}, {0, 0, 0, 9, 0}}))
                .as("swimTime of a 4x5 grid")
                .isEqualTo(0);
    }

    @Test
    void startCellCounts() {
        assertThat(Solution.swimTime(new int[][]{{9, 1}, {1, 1}}))
                .as("swimTime of a 2x2 grid")
                .isEqualTo(9);
    }

    @Test
    void endCellCounts() {
        assertThat(Solution.swimTime(new int[][]{{1, 1}, {1, 9}}))
                .as("swimTime of a 2x2 grid")
                .isEqualTo(9);
    }

    @Test
    void equalElevationsEverywhere() {
        assertThat(Solution.swimTime(new int[][]{{4, 4, 4}, {4, 4, 4}}))
                .as("swimTime of a 2x3 grid")
                .isEqualTo(4);
    }

    @Test
    void randomSixBySeven() {
        assertThat(Solution.swimTime(new int[][]{{28, 35, 29, 28, 32, 37, 12}, {11, 32, 30, 40, 39, 11, 6}, {28, 19, 9, 5, 34, 40, 2}, {38, 25, 28, 39, 10, 39, 0}, {33, 4, 3, 2, 12, 15, 38}, {1, 29, 20, 28, 37, 12, 33}}))
                .as("swimTime of a 6x7 grid")
                .isEqualTo(33);
    }

    @Test
    void tallColumnForcesDetourThroughTheEdge() {
        assertThat(Solution.swimTime(new int[][]{{0, 5, 0, 0}, {0, 5, 0, 5}, {0, 5, 0, 5}, {0, 0, 0, 5}, {7, 7, 7, 0}}))
                .as("swimTime of a 5x4 grid")
                .isEqualTo(5);
    }

    @Test
    void wideRectangle() {
        assertThat(Solution.swimTime(new int[][]{{0, 6, 6, 6, 6, 6, 6, 0}, {0, 0, 0, 0, 0, 0, 0, 0}}))
                .as("swimTime of a 2x8 grid")
                .isEqualTo(0);
    }

    @Test
    void largeGradientNeedsTheCornerElevation() {
        int rows = 150;
        int cols = 90;
        int[][] grid = new int[rows][cols];
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                grid[r][c] = r + c;
            }
        }
        assertThat(Solution.swimTime(grid)).as("swimTime of a %dx%d gradient", rows, cols).isEqualTo(rows + cols - 2);
    }

    @Test
    void doesNotChangeTheGrid() {
        int[][] grid = {{3, 9}, {2, 4}};
        Solution.swimTime(grid);
        assertThat(grid).isDeepEqualTo(new int[][]{{3, 9}, {2, 4}});
    }
}
