package com.jinloes.practice;

import static org.assertj.core.api.Assertions.assertThat;

public final class ExampleRunner {
    private static final String USAGE =
            "Input: the digits 2-9, for example: 23";

    private ExampleRunner() {
    }

    public static void main(String[] args) {
        if (args.length > 0) {
            runInput(String.join(" ", args).trim());
            return;
        }
        assertThat(Solution.combinations("23"))
                .as("Example 1: combinations(\"23\")").containsExactlyInAnyOrder("ad", "ae", "af", "bd", "be", "bf", "cd", "ce", "cf");
        assertThat(Solution.combinations("7"))
                .as("Example 2: combinations(\"7\")").containsExactlyInAnyOrder("p", "q", "r", "s");
        assertThat(Solution.combinations(""))
                .as("Example 3: combinations(\"\")").isEmpty();
        System.out.println("Examples passed: 3");
    }

    private static void runInput(String input) {
        if (input.isEmpty() || input.length() > 8 || !input.matches("[2-9]+")) {
            System.out.println("Give 1 to 8 digits between 2 and 9. " + USAGE);
            return;
        }
        java.util.List<String> result = Solution.combinations(input);
        System.out.println("combinations(" + input + ") = " + result.size() + " strings " + result);
    }
}
