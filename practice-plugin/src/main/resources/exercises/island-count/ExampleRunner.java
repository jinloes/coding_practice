package com.jinloes.practice;

import static org.assertj.core.api.Assertions.assertThat;

public final class ExampleRunner {
    private static final String USAGE =
            "Input: grid rows of 0 and 1 characters separated by spaces, for example: 1101 0100 0011";

    private ExampleRunner() {
    }

    public static void main(String[] args) {
        if (args.length > 0) {
            runInput(String.join(" ", args).trim());
            return;
        }
        assertThat(Solution.countIslands(grid("110", "010", "001"))).as("Example 1").isEqualTo(2);
        assertThat(Solution.countIslands(grid("000", "000"))).as("Example 2").isZero();
        assertThat(Solution.countIslands(grid("101", "010", "101"))).as("Example 3").isEqualTo(5);
        System.out.println("Examples passed: 3");
    }

    private static void runInput(String input) {
        String[] rows = input.split("\\s+");
        for (String row : rows) {
            if (!row.matches("[01]+") || row.length() != rows[0].length()) {
                System.out.println("Could not read \"" + input + "\" as a grid. " + USAGE);
                return;
            }
        }
        System.out.println("countIslands(" + input + ") = " + Solution.countIslands(grid(rows)));
    }

    private static char[][] grid(String... rows) {
        char[][] cells = new char[rows.length][];
        for (int i = 0; i < rows.length; i++) {
            cells[i] = rows[i].toCharArray();
        }
        return cells;
    }
}
