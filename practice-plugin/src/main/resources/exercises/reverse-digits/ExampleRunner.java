package com.jinloes.practice;

import static org.assertj.core.api.Assertions.assertThat;

public final class ExampleRunner {
    private static final String USAGE =
            "Input: one 32-bit integer, for example: 1203";

    private ExampleRunner() {
    }

    public static void main(String[] args) {
        if (args.length > 0) {
            runInput(String.join(" ", args).trim());
            return;
        }
        assertThat(Solution.reverseDigits(1203)).as("Example 1").isEqualTo(3021);
        assertThat(Solution.reverseDigits(-450)).as("Example 2").isEqualTo(-54);
        assertThat(Solution.reverseDigits(1563847412)).as("Example 3").isZero();
        System.out.println("Examples passed: 3");
    }

    private static void runInput(String input) {
        try {
            int value = Integer.parseInt(input);
            System.out.println("reverseDigits(" + value + ") = " + Solution.reverseDigits(value));
        } catch (NumberFormatException exception) {
            System.out.println("Could not parse input. " + USAGE);
        }
    }
}
