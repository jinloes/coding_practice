package com.jinloes.practice;


import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

public final class ExampleRunner {
    private static final String USAGE = "Input: the digits, most significant first, for example: [1, 2, 9]";

    private ExampleRunner() {
    }

    public static void main(String[] args) {
        if (args.length > 0) {
            runInput(String.join(" ", args).trim());
            return;
        }
        example1();
        example2();
        example3();
        System.out.println("Examples passed: 3");
    }

    private static void example1() {
        check(new int[]{1, 2, 9}, 1, 3, 0);
    }

    private static void example2() {
        check(new int[]{9, 9}, 1, 0, 0);
    }

    private static void example3() {
        check(new int[]{0}, 1);
    }

    private static void check(int[] digits, int... expected) {
        int[] original = digits.clone();
        String call = "increment(%s)".formatted(Arrays.toString(original));
        assertThat(Solution.increment(digits)).as(call).containsExactly(expected);
        assertThat(digits).as(call + " must not mutate the input").containsExactly(original);
    }

    private static void runInput(String input) {
        try {
            if (!input.matches("[\\[\\]\\d,\\s]*")) {
                throw new IllegalArgumentException("characters");
            }
            String cleaned = input.replace('[', ' ').replace(']', ' ').replace(',', ' ').trim();
            String[] tokens = cleaned.split("\\s+");
            int[] digits = new int[tokens.length];
            for (int i = 0; i < tokens.length; i++) {
                digits[i] = Integer.parseInt(tokens[i]);
                if (digits[i] < 0 || digits[i] > 9) {
                    throw new IllegalArgumentException("digit");
                }
            }
            System.out.println("increment(" + Arrays.toString(digits) + ") = " + Arrays.toString(Solution.increment(digits)));
        } catch (RuntimeException exception) {
            System.out.println("Could not read \"" + input + "\" as digits. " + USAGE);
        }
    }
}
