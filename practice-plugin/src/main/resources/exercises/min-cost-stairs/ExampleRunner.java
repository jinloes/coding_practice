package com.jinloes.practice;

import static org.assertj.core.api.Assertions.assertThat;

public final class ExampleRunner {
    private static final String USAGE =
            "Input: step tolls separated by spaces (non-negative integers), for example: 4 9 2 7 1 8";

    private ExampleRunner() {
    }

    public static void main(String[] args) {
        if (args.length > 0) {
            runInput(String.join(" ", args).trim());
            return;
        }
        assertThat(Solution.minCostToTop(new int[]{10, 15, 20})).as("Example 1").isEqualTo(15);
        assertThat(Solution.minCostToTop(new int[]{4, 9, 2, 7, 1, 8})).as("Example 2").isEqualTo(7);
        assertThat(Solution.minCostToTop(new int[0])).as("Example 3").isEqualTo(0);
        System.out.println("Examples passed: 3");
    }

    private static void runInput(String input) {
        try {
            String[] tokens = input.split("\\s+");
            int[] costs = new int[tokens.length];
            for (int i = 0; i < tokens.length; i++) {
                costs[i] = Integer.parseInt(tokens[i]);
                if (costs[i] < 0) {
                    throw new IllegalArgumentException();
                }
            }
            System.out.println("minCostToTop(" + tokens.length + " steps) = " + Solution.minCostToTop(costs));
        } catch (RuntimeException exception) {
            System.out.println("Could not parse input. " + USAGE);
        }
    }
}
