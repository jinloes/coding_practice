package com.jinloes.practice;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

public final class ExampleRunner {
    private static final String USAGE =
            "Input: a single non-negative integer n, for example: 5";

    private ExampleRunner() {
    }

    public static void main(String[] args) {
        if (args.length > 0) {
            runInput(String.join(" ", args).trim());
            return;
        }
        assertThat(Solution.countBits(5)).as("Example 1: countBits(5)").containsExactly(0, 1, 1, 2, 1, 2);
        assertThat(Solution.countBits(0)).as("Example 2: countBits(0)").containsExactly(0);
        assertThat(Solution.countBits(2)).as("Example 3: countBits(2)").containsExactly(0, 1, 1);
        System.out.println("Examples passed: 3");
    }

    private static void runInput(String input) {
        int n;
        try {
            n = Integer.parseInt(input.trim());
        } catch (NumberFormatException exception) {
            System.out.println("Could not read \"" + input + "\" as an integer. " + USAGE);
            return;
        }
        if (n < 0 || n > 1_000_000) {
            System.out.println("n must be between 0 and 1000000. " + USAGE);
            return;
        }
        System.out.println("countBits(" + n + ") = " + Arrays.toString(Solution.countBits(n)));
    }


}
