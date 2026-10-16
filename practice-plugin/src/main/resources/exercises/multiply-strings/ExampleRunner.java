package com.jinloes.practice;


import java.math.BigInteger;

import static org.assertj.core.api.Assertions.assertThat;

public final class ExampleRunner {
    private static final String USAGE = "Input: two non-negative decimal numbers separated by a space, for example: 123 456";

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
        check("12", "13", "156");
    }

    private static void example2() {
        check("999", "999", "998001");
    }

    private static void example3() {
        check("0", "5", "0");
    }

    private static void check(String left, String right, String expected) {
        assertThat(Solution.multiply(left, right)).as("multiply(\"%s\", \"%s\")".formatted(left, right)).isEqualTo(expected);
    }

    private static void runInput(String input) {
        String[] tokens = input.split("\\s+");
        if (tokens.length != 2 || !tokens[0].matches("0|[1-9][0-9]*") || !tokens[1].matches("0|[1-9][0-9]*")) {
            System.out.println("Could not read \"" + input + "\" as two numbers. " + USAGE);
            return;
        }
        System.out.println("multiply(\"" + tokens[0] + "\", \"" + tokens[1] + "\") = \"" + Solution.multiply(tokens[0], tokens[1]) + "\"");
    }
}
