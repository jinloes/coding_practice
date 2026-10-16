package com.jinloes.practice;

import static org.assertj.core.api.Assertions.assertThat;

public final class ExampleRunner {
    private static final String USAGE =
            "Input: points as x,y pairs separated by spaces, for example: 0,0 2,2 3,10 5,2 7,0";

    private ExampleRunner() {
    }

    public static void main(String[] args) {
        if (args.length > 0) {
            runInput(String.join(" ", args).trim());
            return;
        }
        assertThat(Solution.minimumCost(new int[][]{{0, 0}, {2, 2}, {3, 10}, {5, 2}, {7, 0}}))
                .as("Example 1").isEqualTo(20);
        assertThat(Solution.minimumCost(new int[][]{{4, 4}})).as("Example 2").isEqualTo(0);
        assertThat(Solution.minimumCost(new int[][]{{1, 1}, {4, 5}})).as("Example 3").isEqualTo(7);
        System.out.println("Examples passed: 3");
    }

    private static void runInput(String input) {
        try {
            String[] tokens = input.split("\\s+");
            int[][] points = new int[tokens.length][];
            for (int i = 0; i < tokens.length; i++) {
                String[] pair = tokens[i].split(",");
                if (pair.length != 2) {
                    throw new IllegalArgumentException();
                }
                points[i] = new int[]{Integer.parseInt(pair[0]), Integer.parseInt(pair[1])};
            }
            System.out.println("minimumCost(" + tokens.length + " points) = " + Solution.minimumCost(points));
        } catch (RuntimeException exception) {
            System.out.println("Could not parse input. " + USAGE);
        }
    }
}
