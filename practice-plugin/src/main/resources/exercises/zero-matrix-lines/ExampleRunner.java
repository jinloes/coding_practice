package com.jinloes.practice;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;

public final class ExampleRunner {
    private static final String USAGE = "Input: the matrix as nested lists of integers, for example: [[1, 1, 1], [1, 0, 1], [1, 1, 1]]";

    private ExampleRunner() {
    }

    public static void main(String[] args) {
        if (args.length > 0) {
            runInput(String.join(" ", args).trim());
            return;
        }
        int[][] first = {{1, 1, 1}, {1, 0, 1}, {1, 1, 1}};
        Solution.zeroLines(first);
        assertThat(first).isDeepEqualTo(new int[][]{{1, 0, 1}, {0, 0, 0}, {1, 0, 1}});
        int[][] second = {{0, 2, 3, 4}, {5, 6, 7, 8}, {9, 10, 11, 12}};
        Solution.zeroLines(second);
        assertThat(second).isDeepEqualTo(new int[][]{{0, 0, 0, 0}, {0, 6, 7, 8}, {0, 10, 11, 12}});
        int[][] third = {{1, 2}, {3, 4}};
        Solution.zeroLines(third);
        assertThat(third).isDeepEqualTo(new int[][]{{1, 2}, {3, 4}});
        System.out.println("Examples passed: 3");
    }

    private static void runInput(String input) {
        try {
            int[][] matrix = parseMatrix(input);
            if (matrix == null) {
                System.out.println("Could not read a rectangular matrix. " + USAGE);
                return;
            }
            Solution.zeroLines(matrix);
            System.out.println("zeroLines(...) leaves " + Arrays.deepToString(matrix));
        } catch (NumberFormatException exception) {
            System.out.println("Could not parse input. " + USAGE);
        }
    }

    private static int[][] parseMatrix(String input) {
        if (!input.startsWith("[[") || !input.endsWith("]]")) {
            return null;
        }
        Matcher matcher = Pattern.compile("\\[([^\\[\\]]*)\\]").matcher(input);
        List<int[]> rows = new ArrayList<>();
        while (matcher.find()) {
            String body = matcher.group(1).replace(',', ' ').trim();
            if (body.isEmpty()) {
                return null;
            }
            String[] tokens = body.split("\\s+");
            int[] row = new int[tokens.length];
            for (int i = 0; i < tokens.length; i++) {
                row[i] = Integer.parseInt(tokens[i]);
            }
            rows.add(row);
        }
        for (int[] row : rows) {
            if (row.length != rows.get(0).length) {
                return null;
            }
        }
        return rows.isEmpty() ? null : rows.toArray(new int[0][]);
    }
}
