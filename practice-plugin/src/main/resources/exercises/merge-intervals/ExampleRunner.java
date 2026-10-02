package com.jinloes.practice;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

public final class ExampleRunner {
    private static final String USAGE =
            "Input: the intervals, for example: [[8, 10], [1, 3], [2, 6]]";

    private ExampleRunner() {
    }

    public static void main(String[] args) {
        if (args.length > 0) {
            runInput(String.join(" ", args).trim());
            return;
        }
        assertThat(Solution.merge(pairs(new int[]{8, 10, 1, 3, 2, 6}, 0, 6)))
                .as("Example 1: merge([[8, 10], [1, 3], [2, 6]])").isDeepEqualTo(pairs(new int[]{1, 6, 8, 10}, 0, 4));
        assertThat(Solution.merge(pairs(new int[]{1, 4, 4, 5}, 0, 4)))
                .as("Example 2: merge([[1, 4], [4, 5]])").isDeepEqualTo(pairs(new int[]{1, 5}, 0, 2));
        assertThat(Solution.merge(pairs(new int[]{5, 6}, 0, 2)))
                .as("Example 3: merge([[5, 6]])").isDeepEqualTo(pairs(new int[]{5, 6}, 0, 2));
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
        if (flat.length % 2 != 0) {
            System.out.println("Every interval needs a start and an end. " + USAGE);
            return;
        }
        int[][] intervals = pairs(flat, 0, flat.length);
        System.out.println("merge(" + Arrays.deepToString(intervals) + ") = "
                + Arrays.deepToString(Solution.merge(intervals)));
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
