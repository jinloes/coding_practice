package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CorrectnessTest {
    @Test
    void oneCellGridNeedsNoMoves() { assertThat(Solution.shortestPath(new int[][]{{0}}, 0)).isZero(); }

    @Test
    void straightOpenPathUsesManhattanDistance() {
        int[][] grid = {{0,0,0},{0,0,0}};
        assertThat(Solution.shortestPath(grid, 0)).isEqualTo(3);
        assertThat(grid[0]).containsExactly(0,0,0);
    }

    @Test
    void enoughBudgetCanCrossObstacles() { assertThat(Solution.shortestPath(new int[][]{{0,1,1},{1,1,1},{1,1,0}}, 4)).isEqualTo(4); }

    @Test
    void longerRouteCanSaveBudget() {
        int[][] grid = {{0,0,0},{1,1,0},{0,0,0},{0,1,1},{0,0,0}};
        assertThat(Solution.shortestPath(grid, 0)).isEqualTo(10);
    }

    @Test
    void largerCorridorWithPeriodicWalls() {
        int rows = 20;
        int cols = 20;
        int[][] grid = new int[rows][cols];
        for (int r = 1; r < rows - 1; r++) grid[r][1] = 1;
        assertThat(Solution.shortestPath(grid, rows - 2)).isEqualTo(rows + cols - 2);
    }
}
