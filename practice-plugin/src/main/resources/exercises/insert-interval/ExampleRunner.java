package com.jinloes.practice;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

public final class ExampleRunner {
    private static final String USAGE =
            "Input: the sorted intervals followed by the new interval, for example: [[1, 3], [6, 9]] [2, 5]";

    private ExampleRunner() {
    }

    public static void main(String[] args) {
        if (args.length > 0) {
            runInput(String.join(" ", args).trim());
            return;
        }
        assertThat(Solution.insert(pairs(new int[]{1, 3, 6, 9}, 0, 4), new int[]{2, 5}))
                .as("Example 1: insert([[1, 3], [6, 9]], [2, 5])").isDeepEqualTo(pairs(new int[]{1, 5, 6, 9}, 0, 4));
        assertThat(Solution.insert(new int[0][], new int[]{4, 7}))
                .as("Example 2: insert([], [4, 7])").isDeepEqualTo(pairs(new int[]{4, 7}, 0, 2));
        assertThat(Solution.insert(pairs(new int[]{1, 2, 5, 6}, 0, 4), new int[]{3, 4}))
                .as("Example 3: insert([[1, 2], [5, 6]], [3, 4])").isDeepEqualTo(pairs(new int[]{1, 2, 3, 4, 5, 6}, 0, 6));
        System.out.println("Examples passed: 3");
    }

    private static void runInput(String input) {
        int[] flat;
        try {
            flat = parseInts(input);
        } catch (NumberFormatException exception) {
            System.out.println("Could not read \"" + input + "\" as integers. " + USAGE);
            return;
        }
        if (flat.length < 2 || flat.length % 2 != 0) {
            System.out.println("Give the intervals, then the new interval. " + USAGE);
            return;
        }
        int[][] intervals = pairs(flat, 0, flat.length - 2);
        int[] added = {flat[flat.length - 2], flat[flat.length - 1]};
        System.out.println("insert(" + Arrays.deepToString(intervals) + ", " + Arrays.toString(added) + ") = "
                + Arrays.deepToString(Solution.insert(intervals, added)));
    }

    private static int[] parseInts(String input) {
        String cleaned = input.replace('[', ' ').replace(']', ' ').replace(',', ' ').trim();
        if (cleaned.isEmpty()) {
            return new int[0];
        }
        String[] tokens = cleaned.split("\\s+");
        int[] values = new int[tokens.length];
        for (int i = 0; i < tokens.length; i++) {
            values[i] = Integer.parseInt(tokens[i]);
        }
        return values;
    }

    private static int[][] pairs(int[] flat, int from, int to) {
        int[][] result = new int[(to - from) / 2][];
        for (int i = 0; i < result.length; i++) {
            result[i] = new int[]{flat[from + 2 * i], flat[from + 2 * i + 1]};
        }
        return result;
    }
}
