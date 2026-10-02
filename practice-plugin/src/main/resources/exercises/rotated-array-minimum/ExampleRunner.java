package com.jinloes.practice;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public final class ExampleRunner {
    private static final String USAGE =
            "Input: the rotated array values (at least one), for example: [5, 6, 7, 1, 2, 3]";

    private ExampleRunner() {
    }

    public static void main(String[] args) {
        if (args.length > 0) {
            runInput(String.join(" ", args).trim());
            return;
        }
        check(new int[]{5, 6, 7, 1, 2, 3}, 1);
        check(new int[]{1, 2, 3}, 1);
        check(new int[]{9}, 9);
        System.out.println("Examples passed: 3");
    }

    private static void check(int[] rotated, int expected) {
        String call = "findMinimum(%s)".formatted(Arrays.toString(rotated));
        assertThat(Solution.findMinimum(rotated)).as(call).isEqualTo(expected);
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
        System.out.println("findMinimum(" + Arrays.toString(numbers) + ") = " + Solution.findMinimum(numbers));
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
