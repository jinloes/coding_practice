package com.jinloes.practice;

import java.util.Arrays;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class CorrectnessTest {
    @Test
    void emptyGridHasNoIsland() {
        check(0, new int[0][]);
        check(0, new int[][]{{}});
    }

    @Test
    void singleLandCell() {
        check(1, new int[][]{{1}});
    }

    @Test
    void solidGridIsOneIsland() {
        check(12, filled(3, 4, 1));
    }

    @Test
    void ringAroundWaterCountsOnlyLand() {
        check(8, new int[][]{{1, 1, 1}, {1, 0, 1}, {1, 1, 1}});
    }

    @Test
    void twoIslandsOfEqualSize() {
        check(2, new int[][]{{1, 1, 0, 1, 1}});
    }

    @Test
    void snakeIsOneIsland() {
        check(11, new int[][]{{1, 1, 1, 1, 1}, {0, 0, 0, 0, 1}, {1, 1, 1, 1, 1}});
    }

    @Test
    void islandsSeparatedByAColumnOfWater() {
        check(6, new int[][]{{1, 0, 1, 1}, {1, 0, 1, 1}, {1, 0, 1, 1}});
    }

    @Test
    void largeSolidGridDoesNotOverflowTheStack() {
        check(40000, filled(200, 200, 1));
    }


    private static void check(int expected, int[][] grid) {
        int[][] copy = Arrays.stream(grid).map(int[]::clone).toArray(int[][]::new);
        String call = "largestIsland(" + Arrays.deepToString(grid) + ")";
        assertThat(Solution.largestIsland(grid)).as(call).isEqualTo(expected);
        assertThat(grid).as(call + " must not modify the grid").isEqualTo(copy);
    }

    private static int[][] filled(int rows, int cols, int value) {
        int[][] grid = new int[rows][cols];
        for (int[] row : grid) {
            Arrays.fill(row, value);
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
