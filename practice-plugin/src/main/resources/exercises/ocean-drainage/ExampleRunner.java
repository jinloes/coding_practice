package com.jinloes.practice;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public final class ExampleRunner {
    private static final String USAGE =
            "Input: grid rows separated by /, each row as space-separated integer heights, for example: 1 2 2 / 3 2 3 / 2 4 5";

    private ExampleRunner() {
    }

    public static void main(String[] args) {
        if (args.length > 0) {
            runInput(String.join(" ", args).trim());
            return;
        }
        assertThat(cells(new int[][]{{5}})).as("Example 1").containsExactlyInAnyOrder("[0, 0]");
        assertThat(cells(new int[][]{{1, 2, 3}})).as("Example 2")
                .containsExactlyInAnyOrder("[0, 0]", "[0, 1]", "[0, 2]");
        assertThat(cells(new int[][]{{1, 2, 2}, {3, 2, 3}, {2, 4, 5}})).as("Example 3")
                .containsExactlyInAnyOrder("[0, 1]", "[0, 2]", "[1, 0]", "[1, 1]", "[1, 2]", "[2, 0]", "[2, 1]", "[2, 2]");
        System.out.println("Examples passed: 3");
    }

    private static void runInput(String input) {
        try {
            String[] rowTexts = input.split("/");
            int[][] grid = new int[rowTexts.length][];
            for (int r = 0; r < rowTexts.length; r++) {
                String text = rowTexts[r].trim();
                if (text.isEmpty()) {
                    throw new NumberFormatException();
                }
                String[] tokens = text.split("\\s+");
                grid[r] = new int[tokens.length];
                for (int c = 0; c < tokens.length; c++) {
                    grid[r][c] = Integer.parseInt(tokens[c]);
                }
                if (grid[r].length != grid[0].length) {
                    System.out.println("Rows must all have the same length. " + USAGE);
                    return;
                }
            }
            System.out.println("drainageCells(" + input + ") = " + cells(grid));
        } catch (NumberFormatException exception) {
            System.out.println("Could not parse input. " + USAGE);
        }
    }

    private static List<String> cells(int[][] grid) {
        List<String> result = new ArrayList<>();
        for (int[] cell : Solution.drainageCells(grid)) {
            result.add("[" + cell[0] + ", " + cell[1] + "]");
        }
        return result;
    }
}
