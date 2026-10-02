package com.jinloes.practice;

import static org.assertj.core.api.Assertions.assertThat;

public final class ExampleRunner {
    private static final String USAGE =
            "Input: two integers separated by a space, for example: 13 29";

    private ExampleRunner() {
    }

    public static void main(String[] args) {
        if (args.length > 0) {
            runInput(String.join(" ", args).trim());
            return;
        }
        assertThat(Solution.add(13, 29)).as("Example 1: add(13, 29)").isEqualTo(42);
        assertThat(Solution.add(-5, 3)).as("Example 2: add(-5, 3)").isEqualTo(-2);
        assertThat(Solution.add(7, -7)).as("Example 3: add(7, -7)").isEqualTo(0);
        System.out.println("Examples passed: 3");
    }

    private static void runInput(String input) {
        String[] tokens = input.replace(',', ' ').trim().split("\\s+");
        int a;
        int b;
        try {
            if (tokens.length != 2) {
                throw new NumberFormatException("expected two integers");
            }
            a = Integer.parseInt(tokens[0]);
            b = Integer.parseInt(tokens[1]);
        } catch (NumberFormatException exception) {
            System.out.println("Could not read \"" + input + "\" as two integers. " + USAGE);
            return;
        }
        System.out.println("add(" + a + ", " + b + ") = " + Solution.add(a, b));
    }


}
