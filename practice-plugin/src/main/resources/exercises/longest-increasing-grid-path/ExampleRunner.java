package com.jinloes.practice;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;

public final class ExampleRunner {
    private static final String USAGE =
            "Input: the grid as rows, for example: [[3, 4, 5], [2, 9, 6], [1, 8, 7]]";

    private ExampleRunner() {
    }

    public static void main(String[] args) {
        if (args.length > 0) {
            runInput(String.join(" ", args).trim());
            return;
        }
        assertThat(Solution.longestRise(new int[][]{{3, 4, 5}, {2, 9, 6}, {1, 8, 7}})).as("longestRise([[3, 4, 5], [2, 9, 6], [1, 8, 7]])").isEqualTo(9);
        assertThat(Solution.longestRise(new int[][]{{5, 5}, {5, 5}})).as("longestRise([[5, 5], [5, 5]])").isEqualTo(1);
        assertThat(Solution.longestRise(new int[][]{{1, 4, 2, 8}})).as("longestRise([[1, 4, 2, 8]])").isEqualTo(2);
        System.out.println("Examples passed: 3");
    }

    private static void runInput(String input) {
        int[][] grid;
        try {
            grid = parseGrid(input);
        } catch (RuntimeException exception) {
            System.out.println("Could not read \"" + input + "\". " + USAGE);
            return;
        }
        System.out.println("longestRise(" + Arrays.deepToString(grid) + ")) = " + Solution.longestRise(grid));
    }

    private static int[][] parseGrid(String text) {
        Matcher matcher = Pattern.compile("\\[([^\\[\\]]*)\\]").matcher(text);
        List<int[]> rows = new ArrayList<>();
        while (matcher.find()) {
            String cleaned = matcher.group(1).replace(',', ' ').trim();
            if (cleaned.isEmpty()) {
                throw new IllegalArgumentException("empty row");
            }
            String[] tokens = cleaned.split("\\s+");
            int[] row = new int[tokens.length];
            for (int i = 0; i < tokens.length; i++) {
                row[i] = Integer.parseInt(tokens[i]);
            }
            if (!rows.isEmpty() && rows.get(0).length != row.length) {
                throw new IllegalArgumentException("ragged rows");
            }
            rows.add(row);
        }
        if (rows.isEmpty()) {
            throw new IllegalArgumentException("no rows");
        }
        return rows.toArray(new int[0][]);
    }
}
