package com.jinloes.practice;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

public final class ExampleRunner {
    private static final String USAGE =
            "Input: grid rows separated by semicolons, for example: 5,1,2;4,0,3";
    private ExampleRunner() {}
    public static void main(String[] args) {
        if (args.length > 0) { runInput(String.join(" ", args).trim()); return; }
        assertThat(Solution.maxPathSum(new int[][]{{5, 1, 2}, {4, 0, 3}})).isEqualTo(12);
        assertThat(Solution.maxPathSum(new int[][]{{-1, -2}, {-3, -4}})).isEqualTo(-7);
        System.out.println("Examples passed: 2");
    }
    private static void runInput(String input) {
        try { System.out.println(Solution.maxPathSum(parseGrid(input))); }
        catch (NumberFormatException exception) { System.out.println("Could not read input. " + USAGE); }
    }
    private static int[][] parseGrid(String input) {
        String trimmed = input.trim();
        if (trimmed.isEmpty()) return new int[0][0];
        String[] rows = trimmed.split("\s*;\s*");
        int[][] grid = new int[rows.length][];
        for (int r = 0; r < rows.length; r++) {
            grid[r] = Arrays.stream(rows[r].replace('[', ' ').replace(']', ' ').trim().split("\s*,\s*|\s+"))
                    .filter(token -> !token.isEmpty()).mapToInt(Integer::parseInt).toArray();
        }
        return grid;
    }
}
