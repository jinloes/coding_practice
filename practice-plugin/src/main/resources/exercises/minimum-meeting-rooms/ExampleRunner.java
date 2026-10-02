package com.jinloes.practice;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

public final class ExampleRunner {
    private static final String USAGE =
            "Input: the meetings, for example: [[1, 4], [2, 5], [4, 6]]";

    private ExampleRunner() {
    }

    public static void main(String[] args) {
        if (args.length > 0) {
            runInput(String.join(" ", args).trim());
            return;
        }
        assertThat(Solution.minRooms(pairs(new int[]{1, 4, 2, 5, 4, 6}, 0, 6)))
                .as("Example 1: minRooms([[1, 4], [2, 5], [4, 6]])").isEqualTo(2);
        assertThat(Solution.minRooms(pairs(new int[]{1, 2, 2, 3}, 0, 4)))
                .as("Example 2: minRooms([[1, 2], [2, 3]])").isEqualTo(1);
        assertThat(Solution.minRooms(pairs(new int[]{1, 5, 2, 6, 3, 7}, 0, 6)))
                .as("Example 3: minRooms([[1, 5], [2, 6], [3, 7]])").isEqualTo(3);
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
        System.out.println("minRooms(" + Arrays.deepToString(intervals) + ") = "
                + Solution.minRooms(intervals));
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
