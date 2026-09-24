package com.jinloes.practice;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

public final class ExampleRunner {
    private static final String USAGE =
            "Input: the values, one of which fills more than half of them, for example: [2, 2, 1, 1, 1, 2, 2]";

    private ExampleRunner() {
    }

    public static void main(String[] args) {
        if (args.length > 0) {
            runInput(String.join(" ", args).trim());
            return;
        }
        assertMajority(new int[]{3, 2, 3}, 3);
        assertMajority(new int[]{2, 2, 1, 1, 1, 2, 2}, 2);
        assertMajority(new int[]{7}, 7);
        System.out.println("Examples passed: 3");
    }

    private static void assertMajority(int[] numbers, int expected) {
        assertThat(Solution.majorityElement(numbers))
                .as("majorityElement(%s) must return %d", Arrays.toString(numbers), expected)
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
        if (numbers.length == 0) {
            System.out.println("The contract requires at least one value. " + USAGE);
            return;
        }
        int result = Solution.majorityElement(numbers.clone());
        System.out.println("majorityElement(" + Arrays.toString(numbers) + ") = " + result);
        long occurrences = Arrays.stream(numbers).filter(value -> value == result).count();
        if (occurrences * 2 <= numbers.length) {
            System.out.println("  " + result + " appears " + occurrences + " of " + numbers.length
                    + " times, which is not more than half. Check that the input has a majority value.");
        }
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
