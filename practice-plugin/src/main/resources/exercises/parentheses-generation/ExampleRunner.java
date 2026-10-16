package com.jinloes.practice;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public final class ExampleRunner {
    private static final String USAGE =
            "Input: the number of pairs, from 0 to 10, for example: 3";

    private ExampleRunner() {
    }

    public static void main(String[] args) {
        if (args.length > 0) {
            runInput(String.join(" ", args).trim());
            return;
        }
        assertThat(Solution.generate(1)).as("Example 1: generate(1)").containsExactly("()");
        assertThat(Solution.generate(2)).as("Example 2: generate(2)").containsExactlyInAnyOrder("(())", "()()");
        assertThat(Solution.generate(3)).as("Example 3: generate(3)")
                .containsExactlyInAnyOrder("((()))", "(()())", "(())()", "()(())", "()()()");
        System.out.println("Examples passed: 3");
    }

    private static void runInput(String input) {
        int pairs;
        try {
            pairs = Integer.parseInt(input);
        } catch (NumberFormatException exception) {
            System.out.println("Could not read \"" + input + "\" as a number. " + USAGE);
            return;
        }
        if (pairs < 0 || pairs > 10) {
            System.out.println(pairs + " is out of range. " + USAGE);
            return;
        }
        List<String> result = Solution.generate(pairs);
        System.out.println("generate(" + pairs + ") returned " + result.size() + " strings: " + result);
    }
}
