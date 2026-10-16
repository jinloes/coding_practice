package com.jinloes.practice;


import static org.assertj.core.api.Assertions.assertThat;

public final class ExampleRunner {
    private static final String USAGE = "Input: a positive integer, for example: 19";

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
        check(19, true);
    }

    private static void example2() {
        check(4, false);
    }

    private static void example3() {
        check(1, true);
    }

    private static void check(int n, boolean expected) {
        assertThat(Solution.isHappy(n)).as("isHappy(%d)".formatted(n)).isEqualTo(expected);
    }

    private static void runInput(String input) {
        try {
            int n = Integer.parseInt(input);
            if (n < 1) {
                throw new NumberFormatException("not positive");
            }
            System.out.println("isHappy(" + n + ") = " + Solution.isHappy(n));
        } catch (NumberFormatException exception) {
            System.out.println("Could not read \"" + input + "\" as a positive integer. " + USAGE);
        }
    }
}
