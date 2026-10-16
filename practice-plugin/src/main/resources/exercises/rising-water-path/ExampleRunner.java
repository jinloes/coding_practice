package com.jinloes.practice;

import static org.assertj.core.api.Assertions.assertThat;

public final class ExampleRunner {
    private static final String USAGE =
            "Input: grid rows separated by / with cells separated by commas, for example: 4,1,9/3,8,2/7,5,6";

    private ExampleRunner() {
    }

    public static void main(String[] args) {
        if (args.length > 0) {
            runInput(String.join(" ", args).trim());
            return;
        }
        assertThat(Solution.swimTime(new int[][]{{4, 1, 9}, {3, 8, 2}, {7, 5, 6}})).as("Example 1").isEqualTo(7);
        assertThat(Solution.swimTime(new int[][]{{5}})).as("Example 2").isEqualTo(5);
        assertThat(Solution.swimTime(new int[][]{{1, 8, 2}, {1, 8, 2}, {1, 1, 3}})).as("Example 3").isEqualTo(3);
        System.out.println("Examples passed: 3");
    }

    private static void runInput(String input) {
        try {
            String[] rows = input.split("/");
            int[][] grid = new int[rows.length][];
            for (int r = 0; r < rows.length; r++) {
                String[] cells = rows[r].trim().split(",");
                grid[r] = new int[cells.length];
                for (int c = 0; c < cells.length; c++) {
                    grid[r][c] = Integer.parseInt(cells[c].trim());
                    if (grid[r][c] < 0) {
                        throw new IllegalArgumentException();
                    }
                }
                if (cells.length != grid[0].length) {
                    throw new IllegalArgumentException();
                }
            }
            System.out.println("swimTime(" + rows.length + "x" + grid[0].length + " grid) = " + Solution.swimTime(grid));
        } catch (RuntimeException exception) {
            System.out.println("Could not parse input. " + USAGE);
        }
    }
}
