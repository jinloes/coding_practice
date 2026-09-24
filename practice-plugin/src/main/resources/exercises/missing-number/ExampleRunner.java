package com.jinloes.practice;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

public final class ExampleRunner {
    private static final String USAGE =
            "Input: the distinct values from 0 to n with one missing, for example: [3, 0, 1]";

    private ExampleRunner() {
    }

    public static void main(String[] args) {
        if (args.length > 0) {
            runInput(String.join(" ", args).trim());
            return;
        }
        assertMissing(new int[]{3, 0, 1}, 2);
        assertMissing(new int[]{0, 1}, 2);
        assertMissing(new int[]{9, 6, 4, 2, 3, 5, 7, 0, 1}, 8);
        System.out.println("Examples passed: 3");
    }

    private static void assertMissing(int[] numbers, int expected) {
        assertThat(Solution.missingNumber(numbers))
                .as("missingNumber(%s) must return %d", Arrays.toString(numbers), expected)
                .isEqualTo(expected);
    }

    private static void runInput(String input) {
        int[] numbers;
        try {
            numbers = parseInts(input);
        } catch (NumberFormatException exception) {
            System.out.println("Could not read \"" + input + "\" as integers. " + USAGE);
            return;
        }
        boolean[] seen = new boolean[numbers.length + 1];
        for (int value : numbers) {
            if (value < 0 || value > numbers.length || seen[value]) {
                System.out.println("The values must be distinct and between 0 and " + numbers.length
                        + ", but " + value + " is not. " + USAGE);
                return;
            }
            seen[value] = true;
        }
        System.out.println("missingNumber(" + Arrays.toString(numbers) + ") = "
                + Solution.missingNumber(numbers.clone()));
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
