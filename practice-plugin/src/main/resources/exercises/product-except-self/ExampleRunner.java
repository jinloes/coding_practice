package com.jinloes.practice;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public final class ExampleRunner {
    private static final String USAGE =
            "Input: the array values, for example: [2, 3, 4]";

    private ExampleRunner() {
    }

    public static void main(String[] args) {
        if (args.length > 0) {
            runInput(String.join(" ", args).trim());
            return;
        }
        check(new int[]{2, 3, 4}, new int[]{12, 8, 6});
        check(new int[]{1, 0, 3}, new int[]{0, 3, 0});
        check(new int[]{5}, new int[]{1});
        System.out.println("Examples passed: 3");
    }

    private static void check(int[] numbers, int[] expected) {
        String call = "productExceptSelf(%s)".formatted(Arrays.toString(numbers));
        int[] actual = Solution.productExceptSelf(numbers);
        assertThat(actual).as(call).isNotNull().containsExactly(expected);
    }

    private static void runInput(String input) {
        int[] numbers;
        try {
            numbers = parseInts(input);
        } catch (NumberFormatException exception) {
            System.out.println("Could not read \"" + input + "\" as integers. " + USAGE);
            return;
        }
        System.out.println("productExceptSelf(" + Arrays.toString(numbers) + ") = " + Arrays.toString(Solution.productExceptSelf(numbers)));
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
