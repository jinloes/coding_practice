package com.jinloes.practice;

import static org.assertj.core.api.Assertions.assertThat;

public final class ExampleRunner {
    private static final String USAGE =
            "Input: a string of digits, for example: 226";

    private ExampleRunner() {
    }

    public static void main(String[] args) {
        if (args.length > 0) {
            runInput(String.join(" ", args).trim());
            return;
        }
        assertThat(Solution.count("12")).as("Example 1: count(\"12\")").isEqualTo(2);
        assertThat(Solution.count("226")).as("Example 2: count(\"226\")").isEqualTo(3);
        assertThat(Solution.count("06")).as("Example 3: count(\"06\")").isEqualTo(0);
        System.out.println("Examples passed: 3");
    }

    private static void runInput(String input) {
        if (!input.matches("[0-9]+")) {
            System.out.println("Could not read \"" + input + "\" as digits. " + USAGE);
            return;
        }
        System.out.println("count(\"" + input + "\") = " + Solution.count(input));
    }
}
