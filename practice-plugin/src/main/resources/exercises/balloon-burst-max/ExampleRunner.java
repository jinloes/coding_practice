package com.jinloes.practice;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

public final class ExampleRunner {
    private static final String USAGE =
            "Input: the balloon values (0 to 100), for example: [3, 1, 5]";

    private ExampleRunner() {
    }

    public static void main(String[] args) {
        if (args.length > 0) {
            runInput(String.join(" ", args).trim());
            return;
        }
        check(new int[]{3, 1, 5}, 35);
        check(new int[]{7}, 7);
        check(new int[]{}, 0);
        System.out.println("Examples passed: 3");
    }

    private static void check(int[] values, int expected) {
        int[] copy = values.clone();
        String call = "maxCoins(%s)".formatted(Arrays.toString(values));
        assertThat(Solution.maxCoins(values)).as(call).isEqualTo(expected);
        assertThat(values).as(call + " must not modify its input").containsExactly(copy);
    }

    private static void runInput(String input) {
        int[] values;
        try {
            values = parseInts(input);
            for (int value : values) {
                if (value < 0 || value > 100) {
                    throw new IllegalArgumentException("out of range");
                }
            }
        } catch (RuntimeException exception) {
            System.out.println("Could not read \"" + input + "\" as balloon values. " + USAGE);
            return;
        }
        System.out.println("maxCoins(" + Arrays.toString(values) + ") = " + Solution.maxCoins(values));
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
