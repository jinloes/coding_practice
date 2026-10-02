package com.jinloes.practice;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public final class ExampleRunner {
    private static final String USAGE =
            "Input: the array values, for example: [31, 8, 9, 30, 10, 32, 7]";

    private ExampleRunner() {
    }

    public static void main(String[] args) {
        if (args.length > 0) {
            runInput(String.join(" ", args).trim());
            return;
        }
        check(new int[]{31, 8, 9, 30, 10, 32, 7}, 4);
        check(new int[]{5, 5, 5}, 1);
        check(new int[0], 0);
        System.out.println("Examples passed: 3");
    }

    private static void check(int[] numbers, int expected) {
        String call = "longestRun(%s)".formatted(Arrays.toString(numbers));
        assertThat(Solution.longestRun(numbers)).as(call).isEqualTo(expected);
    }

    private static void runInput(String input) {
        int[] numbers;
        try {
            numbers = parseInts(input);
        } catch (NumberFormatException exception) {
            System.out.println("Could not read \"" + input + "\" as integers. " + USAGE);
            return;
        }
        System.out.println("longestRun(" + Arrays.toString(numbers) + ") = " + Solution.longestRun(numbers));
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
