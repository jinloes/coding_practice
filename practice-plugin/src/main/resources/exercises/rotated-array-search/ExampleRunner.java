package com.jinloes.practice;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public final class ExampleRunner {
    private static final String USAGE =
            "Input: the rotated array values followed by the target, for example: [6, 7, 8, 1, 2, 3, 4] 2";

    private ExampleRunner() {
    }

    public static void main(String[] args) {
        if (args.length > 0) {
            runInput(String.join(" ", args).trim());
            return;
        }
        check(new int[]{6, 7, 8, 1, 2, 3, 4}, 2, 4);
        check(new int[]{6, 7, 8, 1, 2, 3, 4}, 5, -1);
        check(new int[]{1}, 1, 0);
        System.out.println("Examples passed: 3");
    }

    private static void check(int[] rotated, int target, int expected) {
        String call = "search(%s, %d)".formatted(Arrays.toString(rotated), target);
        assertThat(Solution.search(rotated, target)).as(call).isEqualTo(expected);
    }

    private static void runInput(String input) {
        int[] values;
        try {
            values = parseInts(input);
        } catch (NumberFormatException exception) {
            System.out.println("Could not read \"" + input + "\" as integers. " + USAGE);
            return;
        }
        if (values.length < 2) {
            System.out.println("Need at least one array value and a target. " + USAGE);
            return;
        }
        int target = values[values.length - 1];
        int[] numbers = Arrays.copyOf(values, values.length - 1);
        System.out.println("search(" + Arrays.toString(numbers) + ", " + target + ") = "
                + Solution.search(numbers, target));
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
