package com.jinloes.practice;

import java.util.Arrays;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class CorrectnessTest {
    @Test
    void emptyGridIsAccepted() {
        check(new int[0][], new int[0][]);
    }

    @Test
    void gridWithoutGatesIsUnchanged() {
        check(new int[][]{{INF, INF}, {-1, INF}}, new int[][]{{INF, INF}, {-1, INF}});
    }

    @Test
    void wallsAndGatesAreNotChanged() {
        check(new int[][]{{-1, 0}, {0, -1}}, new int[][]{{-1, 0}, {0, -1}});
    }

    @Test
    void twoGatesMeetInTheMiddle() {
        check(new int[][]{{0, 1, 2, 1, 0}}, new int[][]{{0, INF, INF, INF, 0}});
    }

    @Test
    void pathsGoAroundWalls() {
        check(new int[][]{{0, -1, 6}, {1, -1, 5}, {2, 3, 4}}, new int[][]{{0, -1, INF}, {INF, -1, INF}, {INF, INF, INF}});
    }

    @Test
    void closerGateWinsInBothDirections() {
        check(new int[][]{{1, 0, 1, 2, 1, 0}}, new int[][]{{INF, 0, INF, INF, INF, 0}});
    }

    @Test
    void gateEnclosedByWallsLeavesOutsideOpen() {
        check(new int[][]{{INF, -1, 0}, {INF, -1, -1}}, new int[][]{{INF, -1, 0}, {INF, -1, -1}});
    }

    @Test
    void largeGridFromACorner() {
        int[][] rooms = filled(40, 40, INF);
        rooms[0][0] = 0;
        Solution.fillDistances(rooms);
        assertThat(rooms[39][39]).as("far corner").isEqualTo(78);
        assertThat(rooms[20][5]).as("cell (20, 5)").isEqualTo(25);
    }


    private static final int INF = Integer.MAX_VALUE;

    private static void check(int[][] expected, int[][] rooms) {
        String call = "fillDistances(" + Arrays.deepToString(rooms) + ")";
        Solution.fillDistances(rooms);
        assertThat(rooms).as(call).isEqualTo(expected);
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
