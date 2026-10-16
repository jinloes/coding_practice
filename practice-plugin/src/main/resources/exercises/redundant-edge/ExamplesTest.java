package com.jinloes.practice;

import java.util.Arrays;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class ExamplesTest {
    @Test
    void cycleOfFourNodes() {
        check(new int[]{3, 4}, new int[][]{{4, 1}, {2, 3}, {1, 2}, {3, 4}});
    }

    @Test
    void answerIsNotAlwaysTheLastEdge() {
        check(new int[]{4, 1}, new int[][]{{1, 2}, {2, 3}, {3, 4}, {4, 1}, {5, 1}});
    }

    @Test
    void repeatedEdgeClosesATwoNodeCycle() {
        check(new int[]{1, 2}, new int[][]{{2, 1}, {1, 2}});
    }


    private static void check(int[] expected, int[][] edges) {
        String call = "redundantEdge(" + Arrays.deepToString(edges) + ")";
        assertThat(Solution.redundantEdge(edges)).as(call).containsExactly(expected);
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
