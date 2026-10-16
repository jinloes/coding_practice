package com.jinloes.practice;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

public final class ExampleRunner {
    private static final String USAGE = "Input: the edge list as nested [a, b] pairs, for example: [[4, 1], [2, 3], [1, 2], [3, 4]]";

    private ExampleRunner() {
    }

    public static void main(String[] args) {
        if (args.length > 0) {
            runInput(String.join(" ", args).trim());
            return;
        }
        check(new int[]{3, 4}, new int[][]{{4, 1}, {2, 3}, {1, 2}, {3, 4}});
        check(new int[]{4, 1}, new int[][]{{1, 2}, {2, 3}, {3, 4}, {4, 1}, {5, 1}});
        check(new int[]{1, 2}, new int[][]{{2, 1}, {1, 2}});
        System.out.println("Examples passed: 3");
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

    private static void runInput(String input) {
        int[][] edges;
        try {
            edges = parseGrid(input);
            for (int[] edge : edges) {
                if (edge.length != 2 || edge[0] < 1 || edge[1] < 1 || edge[0] > edges.length || edge[1] > edges.length) {
                    throw new IllegalArgumentException("bad edge");
                }
            }
        } catch (RuntimeException exception) {
            System.out.println("Could not read \"" + input + "\" as an edge list. " + USAGE);
            return;
        }
        System.out.println("redundantEdge(" + Arrays.deepToString(edges) + ") = " + Arrays.toString(Solution.redundantEdge(edges)));
    }
}
