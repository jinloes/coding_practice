package com.jinloes.practice;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

public final class ExampleRunner {
    private static final String USAGE =
            "Input: the values to rearrange, for example: [0, 1, 0, 3, 12]";

    private ExampleRunner() {
    }

    public static void main(String[] args) {
        if (args.length > 0) {
            runInput(String.join(" ", args).trim());
            return;
        }
        assertRearranged(new int[]{0, 1, 0, 3, 12}, 1, 3, 12, 0, 0);
        assertRearranged(new int[]{0, 0}, 0, 0);
        assertRearranged(new int[]{1, 2}, 1, 2);
        System.out.println("Examples passed: 3");
    }

    private static void assertRearranged(int[] numbers, int... expected) {
        String before = Arrays.toString(numbers);
        Solution.moveZeroes(numbers);
        assertThat(numbers).as("moveZeroes(%s) must leave %s", before, Arrays.toString(expected))
                .containsExactly(expected);
    }

    private static void runInput(String input) {
        int[] numbers;
        try {
            numbers = parseInts(input);
        } catch (NumberFormatException exception) {
            System.out.println("Could not read \"" + input + "\" as integers. " + USAGE);
            return;
        }
        String before = Arrays.toString(numbers);
        Solution.moveZeroes(numbers);
        System.out.println("moveZeroes(" + before + ") left " + Arrays.toString(numbers));
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
