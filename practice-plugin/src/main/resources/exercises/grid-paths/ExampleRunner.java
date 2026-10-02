package com.jinloes.practice;

import static org.assertj.core.api.Assertions.assertThat;

public final class ExampleRunner {
    private static final String USAGE =
            "Input: the number of rows then the number of columns, for example: 3 7";

    private ExampleRunner() {
    }

    public static void main(String[] args) {
        if (args.length > 0) {
            runInput(String.join(" ", args).trim());
            return;
        }
        assertThat(Solution.count(3, 7)).as("Example 1: count(3, 7)").isEqualTo(28);
        assertThat(Solution.count(3, 2)).as("Example 2: count(3, 2)").isEqualTo(3);
        assertThat(Solution.count(1, 1)).as("Example 3: count(1, 1)").isEqualTo(1);
        System.out.println("Examples passed: 3");
    }

    private static void runInput(String input) {
        String[] tokens = input.split("\\s+");
        int rows;
        int cols;
        try {
            if (tokens.length != 2) {
                throw new NumberFormatException();
            }
            rows = Integer.parseInt(tokens[0]);
            cols = Integer.parseInt(tokens[1]);
        } catch (NumberFormatException exception) {
            System.out.println("Could not read \"" + input + "\" as two integers. " + USAGE);
            return;
        }
        if (rows < 1 || cols < 1 || rows > 10_000 || cols > 10_000) {
            System.out.println("Rows and columns must each be between 1 and 10000. " + USAGE);
            return;
        }
        System.out.println("count(" + rows + ", " + cols + ") = " + Solution.count(rows, cols));
    }
}
