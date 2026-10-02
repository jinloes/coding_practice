package com.jinloes.practice;

import static org.assertj.core.api.Assertions.assertThat;

public final class ExampleRunner {
    private static final String USAGE =
            "Input: the number of stairs from 0 to 90, for example: 10";

    private ExampleRunner() {
    }

    public static void main(String[] args) {
        if (args.length > 0) {
            runInput(String.join(" ", args).trim());
            return;
        }
        assertThat(Solution.countWays(1)).as("Example 1: countWays(1)").isEqualTo(1L);
        assertThat(Solution.countWays(4)).as("Example 2: countWays(4)").isEqualTo(5L);
        assertThat(Solution.countWays(10)).as("Example 3: countWays(10)").isEqualTo(89L);
        System.out.println("Examples passed: 3");
    }

    private static void runInput(String input) {
        int steps;
        try {
            steps = Integer.parseInt(input);
        } catch (NumberFormatException exception) {
            System.out.println("Could not read \"" + input + "\" as a number of stairs. " + USAGE);
            return;
        }
        if (steps < 0 || steps > 90) {
            System.out.println("The number of stairs is out of range. " + USAGE);
            return;
        }
        System.out.println("countWays(" + steps + ") = " + Solution.countWays(steps));
    }
}
