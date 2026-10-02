package com.jinloes.practice;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

public final class ExampleRunner {
    private static final String USAGE =
            "Input: the array values, for example: [4, 10, 5, 8, 6, 9]";

    private ExampleRunner() {
    }

    public static void main(String[] args) {
        if (args.length > 0) {
            runInput(String.join(" ", args).trim());
            return;
        }
        assertThat(Solution.lengthOfLis(new int[]{4, 10, 5, 8, 6, 9}))
                .as("Example 1: lengthOfLis([4, 10, 5, 8, 6, 9])").isEqualTo(4);
        assertThat(Solution.lengthOfLis(new int[]{3, 3, 3}))
                .as("Example 2: lengthOfLis([3, 3, 3])").isEqualTo(1);
        assertThat(Solution.lengthOfLis(new int[]{9, 7, 5, 1}))
                .as("Example 3: lengthOfLis([9, 7, 5, 1])").isEqualTo(1);
        System.out.println("Examples passed: 3");
    }

    private static void runInput(String input) {
        int[] numbers;
        try {
            numbers = parseInts(input);
        } catch (NumberFormatException exception) {
            System.out.println("Could not read \"" + input + "\" as integers. " + USAGE);
            return;
        }
        System.out.println("lengthOfLis(" + Arrays.toString(numbers) + ") = " + Solution.lengthOfLis(numbers));
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
