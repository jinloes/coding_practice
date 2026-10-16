package com.jinloes.practice;


import static org.assertj.core.api.Assertions.withinPercentage;

import static org.assertj.core.api.Assertions.assertThat;

public final class ExampleRunner {
    private static final String USAGE = "Input: a base and an integer exponent, for example: 2 10";

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
        check(2.0, 10, 1024.0);
    }

    private static void example2() {
        check(2.0, -2, 0.25);
    }

    private static void example3() {
        check(5.0, 0, 1.0);
    }

    private static void check(double base, int exponent, double expected) {
        assertThat(Solution.power(base, exponent)).as("power(%s, %d)".formatted(base, exponent))
                .isCloseTo(expected, withinPercentage(1e-9));
    }

    private static void exact(double base, int exponent, double expected) {
        assertThat(Solution.power(base, exponent)).as("power(%s, %d)".formatted(base, exponent)).isEqualTo(expected);
    }

    private static void runInput(String input) {
        try {
            String[] tokens = input.split("\\s+");
            if (tokens.length != 2) {
                throw new IllegalArgumentException("two values needed");
            }
            double base = Double.parseDouble(tokens[0]);
            int exponent = Integer.parseInt(tokens[1]);
            System.out.println("power(" + base + ", " + exponent + ") = " + Solution.power(base, exponent));
        } catch (RuntimeException exception) {
            System.out.println("Could not read \"" + input + "\". " + USAGE);
        }
    }
}
