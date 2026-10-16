package com.jinloes.practice;

import java.util.Arrays;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class CorrectnessTest {
    @Test
    void selfLoopIsRedundant() {
        check(new int[]{1, 1}, new int[][]{{1, 1}, {1, 2}});
    }

    @Test
    void selfLoopAfterATreeEdge() {
        check(new int[]{2, 2}, new int[][]{{2, 1}, {2, 2}});
    }

    @Test
    void triangleWithATail() {
        check(new int[]{3, 1}, new int[][]{{1, 2}, {2, 3}, {3, 1}, {3, 4}});
    }

    @Test
    void edgeOrientationIsPreserved() {
        check(new int[]{1, 3}, new int[][]{{3, 2}, {2, 1}, {1, 3}});
    }

    @Test
    void lastCycleEdgeWinsEvenWhenBranchesFollow() {
        check(new int[]{2, 5}, new int[][]{{1, 2}, {2, 3}, {3, 4}, {4, 5}, {2, 5}, {5, 6}, {6, 7}});
    }

    @Test
    void shuffledEdgesOfALargerCycle() {
        check(new int[]{6, 2}, new int[][]{{6, 7}, {1, 2}, {4, 5}, {3, 4}, {5, 6}, {2, 3}, {6, 2}});
    }

    @Test
    void returnsACopyOfTheEdge() {
        int[][] edges = {{1, 2}, {2, 1}};
        int[] result = Solution.redundantEdge(edges);
        result[0] = 99;
        assertThat(edges[1]).as("input edge after changing the result").containsExactly(2, 1);
    }

    @Test
    void longRingIsHandledEfficiently() {
        int n = 20000;
        int[][] ring = new int[n][];
        for (int i = 1; i < n; i++) {
            ring[i - 1] = new int[]{i, i + 1};
        }
        ring[n - 1] = new int[]{n, 1};
        check(new int[]{n, 1}, ring);
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
