package com.jinloes.practice;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

public final class ExampleRunner {
    private static final String USAGE =
            "Input: the jump lengths in brackets or separated by spaces, for example: [2, 3, 1, 1, 4]";

    private ExampleRunner() {
    }

    public static void main(String[] args) {
        if (args.length > 0) {
            runInput(String.join(" ", args).trim());
            return;
        }
        assertThat(Solution.canReach(new int[]{2, 3, 1, 1, 4}))
                .as("Example 1: canReach([2, 3, 1, 1, 4])")
                .isEqualTo(true);
        assertThat(Solution.canReach(new int[]{3, 2, 1, 0, 4}))
                .as("Example 2: canReach([3, 2, 1, 0, 4])")
                .isEqualTo(false);
        assertThat(Solution.canReach(new int[]{0}))
                .as("Example 3: canReach([0])")
                .isEqualTo(true);
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
        System.out.println("canReach(" + Arrays.toString(values) + ") = " + Solution.canReach(values));
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
