package com.jinloes.practice;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

public final class ExampleRunner {
    private static final String USAGE = "Input: the grid rows as nested lists using 0 (empty), 1 (fresh) and 2 (rotten), for example: [[0, 1, 1, 1], [1, 2, 1, 0]]";

    private ExampleRunner() {
    }

    public static void main(String[] args) {
        if (args.length > 0) {
            runInput(String.join(" ", args).trim());
            return;
        }
        check(3, new int[][]{{0, 1, 1, 1}, {1, 2, 1, 0}});
        check(-1, new int[][]{{2, 0, 1}});
        check(0, new int[][]{{0, 2}, {0, 0}});
        System.out.println("Examples passed: 3");
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

    private static void runInput(String input) {
        int[][] grid;
        try {
            grid = parseGrid(input);
        } catch (RuntimeException exception) {
            System.out.println("Could not read \"" + input + "\" as a grid. " + USAGE);
            return;
        }
        System.out.println("minutesToRot(" + Arrays.deepToString(grid) + ") = " + Solution.minutesToRot(grid));
    }
}
