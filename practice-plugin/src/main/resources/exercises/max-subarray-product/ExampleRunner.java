package com.jinloes.practice;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public final class ExampleRunner {
    private static final String USAGE =
            "Input: the array values (at least one), for example: [1, -2, 3, -4]";

    private ExampleRunner() {
    }

    public static void main(String[] args) {
        if (args.length > 0) {
            runInput(String.join(" ", args).trim());
            return;
        }
        check(new int[]{1, -2, 3, -4}, 24);
        check(new int[]{-3, 0, -2}, 0);
        check(new int[]{-6}, -6);
        System.out.println("Examples passed: 3");
    }

    private static void check(int[] numbers, long expected) {
        String call = "maxSubarrayProduct(%s)".formatted(Arrays.toString(numbers));
        assertThat(Solution.maxSubarrayProduct(numbers)).as(call).isEqualTo(expected);
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
            System.out.println("No values given. " + USAGE);
            return;
        }
        System.out.println("maxSubarrayProduct(" + Arrays.toString(numbers) + ") = " + Solution.maxSubarrayProduct(numbers));
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
