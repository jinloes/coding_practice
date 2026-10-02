package com.jinloes.practice;

import static org.assertj.core.api.Assertions.assertThat;

public final class ExampleRunner {
    private static final String USAGE =
            "Input: a single integer, for example: 11";

    private ExampleRunner() {
    }

    public static void main(String[] args) {
        if (args.length > 0) {
            runInput(String.join(" ", args).trim());
            return;
        }
        assertThat(Solution.countSetBits(11)).as("Example 1: countSetBits(11)").isEqualTo(3);
        assertThat(Solution.countSetBits(0)).as("Example 2: countSetBits(0)").isEqualTo(0);
        assertThat(Solution.countSetBits(-1)).as("Example 3: countSetBits(-1)").isEqualTo(32);
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
        System.out.println("countSetBits(" + value + ") = " + Solution.countSetBits(value));
    }


}
