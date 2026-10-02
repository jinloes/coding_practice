package com.jinloes.practice;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

public final class ExampleRunner {
    private static final String USAGE =
            "Input: the values in brackets or separated by spaces, for example: [4, 1, 7, 3]";

    private ExampleRunner() {
    }

    public static void main(String[] args) {
        if (args.length > 0) {
            runInput(String.join(" ", args).trim());
            return;
        }
        assertThat(Solution.maxSum(new int[]{4, 1, 7, 3}))
                .as("Example 1: maxSum([4, 1, 7, 3])")
                .isEqualTo(11L);
        assertThat(Solution.maxSum(new int[]{5}))
                .as("Example 2: maxSum([5])")
                .isEqualTo(5L);
        assertThat(Solution.maxSum(new int[]{2, 9, 2}))
                .as("Example 3: maxSum([2, 9, 2])")
                .isEqualTo(9L);
        System.out.println("Examples passed: 3");
    }

    private static void runInput(String input) {
        int[] values;
        try {
            values = parseInts(input);
        } catch (NumberFormatException exception) {
            System.out.println("Could not read \"" + input + "\" as integers. " + USAGE);
            return;
        }
        if (values.length == 0 || Arrays.stream(values).anyMatch(value -> value < 0)) {
            System.out.println("Need at least one non-negative value. " + USAGE);
            return;
        }
        System.out.println("maxSum(" + Arrays.toString(values) + ") = " + Solution.maxSum(values));
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
}
