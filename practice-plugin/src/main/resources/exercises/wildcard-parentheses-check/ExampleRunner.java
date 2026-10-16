package com.jinloes.practice;


import static org.assertj.core.api.Assertions.assertThat;

public final class ExampleRunner {
    private static final String USAGE = "Input: a string of ( ) and * characters, for example: (*))";

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
        check("(*))", true);
    }

    private static void example2() {
        check("*(", false);
    }

    private static void example3() {
        check("", true);
    }

    private static void check(String pattern, boolean expected) {
        assertThat(Solution.isBalanced(pattern)).as("isBalanced(\"%s\")".formatted(pattern)).isEqualTo(expected);
    }

    private static void runInput(String input) {
        if (!input.matches("[()*]+")) {
            System.out.println("Could not read \"" + input + "\" as ( ) and * characters. " + USAGE);
            return;
        }
        System.out.println("isBalanced(\"" + input + "\") = " + Solution.isBalanced(input));
    }
}
