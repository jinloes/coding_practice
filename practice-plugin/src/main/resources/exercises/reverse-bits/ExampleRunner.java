package com.jinloes.practice;

import static org.assertj.core.api.Assertions.assertThat;

public final class ExampleRunner {
    private static final String USAGE =
            "Input: a single integer, for example: 6";

    private ExampleRunner() {
    }

    public static void main(String[] args) {
        if (args.length > 0) {
            runInput(String.join(" ", args).trim());
            return;
        }
        assertThat(Solution.reverseBits(1)).as("Example 1: reverseBits(1)").isEqualTo(Integer.MIN_VALUE);
        assertThat(Solution.reverseBits(6)).as("Example 2: reverseBits(6)").isEqualTo(1610612736);
        assertThat(Solution.reverseBits(-1)).as("Example 3: reverseBits(-1)").isEqualTo(-1);
        System.out.println("Examples passed: 3");
    }

    private static void runInput(String input) {
        int value;
        try {
            value = Integer.parseInt(input.trim());
        } catch (NumberFormatException exception) {
            System.out.println("Could not read \"" + input + "\" as an integer. " + USAGE);
            return;
        }
        System.out.println("reverseBits(" + value + ") = " + Solution.reverseBits(value));
    }


}
