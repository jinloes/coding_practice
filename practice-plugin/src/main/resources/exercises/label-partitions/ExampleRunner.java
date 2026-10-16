package com.jinloes.practice;


import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public final class ExampleRunner {
    private static final String USAGE = "Input: a lowercase string, for example: abcabdxyx";

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
        check("abcabdxyx", 5, 1, 3);
    }

    private static void example2() {
        check("zzzz", 4);
    }

    private static void example3() {
        check("qrst", 1, 1, 1, 1);
    }

    private static void check(String text, Integer... expected) {
        assertThat(Solution.partitionSizes(text)).as("partitionSizes(\"%s\")".formatted(text)).containsExactly(expected);
    }

    private static void runInput(String input) {
        if (!input.matches("[a-z]+")) {
            System.out.println("Could not read \"" + input + "\" as a lowercase string. " + USAGE);
            return;
        }
        System.out.println("partitionSizes(\"" + input + "\") = " + Solution.partitionSizes(input));
    }
}
