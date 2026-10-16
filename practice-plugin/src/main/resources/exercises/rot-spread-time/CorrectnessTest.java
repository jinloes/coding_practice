package com.jinloes.practice;

import java.util.Arrays;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class CorrectnessTest {
    @Test
    void emptyGridNeedsNoTime() {
        check(0, new int[0][]);
        check(0, new int[][]{{}});
    }

    @Test
    void allEmptyCellsNeedNoTime() {
        check(0, new int[][]{{0, 0}, {0, 0}});
    }

    @Test
    void freshWithoutAnyRottenIsImpossible() {
        check(-1, new int[][]{{1, 1}, {1, 1}});
    }

    @Test
    void twoSourcesSpreadAtTheSameTime() {
        check(2, new int[][]{{2, 1, 1, 1, 2}});
    }

    @Test
    void singleSourceOnALine() {
        check(4, new int[][]{{2, 1, 1, 1, 1}});
    }

    @Test
    void wallsCanCutOffFreshCells() {
        check(-1, new int[][]{{2, 0, 1}, {1, 0, 1}});
    }

    @Test
    void cornerSourceInOpenGrid() {
        int[][] grid = fresh(3, 3);
        grid[0][0] = 2;
        check(4, grid);
    }

    @Test
    void largeGridFromACorner() {
        int[][] grid = fresh(40, 40);
        grid[0][0] = 2;
        check(78, grid);
    }

    @Test
    void rotOnlyFollowsEdges() {
        check(-1, new int[][]{{2, 0}, {0, 1}});
    }


    private static void check(int expected, int[][] grid) {
        int[][] copy = Arrays.stream(grid).map(int[]::clone).toArray(int[][]::new);
        String call = "minutesToRot(" + Arrays.deepToString(grid) + ")";
        assertThat(Solution.minutesToRot(grid)).as(call).isEqualTo(expected);
        assertThat(grid).as(call + " must not modify the grid").isEqualTo(copy);
    }

    private static int[][] fresh(int rows, int cols) {
        int[][] grid = new int[rows][cols];
        for (int[] row : grid) {
            Arrays.fill(row, 1);
        }
        return grid;
    }

    private static int[][] parseGrid(String text) {
        String body = text.trim();
        if (body.equals("[]")) {
            return new int[0][];
        }
        if (!body.startsWith("[[") || !body.endsWith("]]")) {
            throw new IllegalArgumentException("not a grid");
        }
        String[] rows = body.substring(2, body.length() - 2).split("\\]\\s*,\\s*\\[");
        int[][] grid = new int[rows.length][];
        for (int r = 0; r < rows.length; r++) {
            String[] cells = rows[r].trim().split("\\s*,\\s*");
            grid[r] = new int[cells.length];
            for (int c = 0; c < cells.length; c++) {
                grid[r][c] = cells[c].equalsIgnoreCase("inf") ? Integer.MAX_VALUE : Integer.parseInt(cells[c]);
            }
        }
        return grid;
    }
}
