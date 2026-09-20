package com.jinloes.practice;

import static org.assertj.core.api.Assertions.assertThat;

public final class ExampleRunner {
    private static final String USAGE =
            "Input: a delimiter string, for example: {[()]}";

    private ExampleRunner() {
    }

    public static void main(String[] args) {
        if (args.length > 0) {
            runInput(String.join(" ", args).trim());
            return;
        }
        assertThat(Solution.isBalanced("{[()]}")).as("Example 1: isBalanced(\"{[()]}\")").isTrue();
        assertThat(Solution.isBalanced("([]{})")).as("Example 2: isBalanced(\"([]{})\")").isTrue();
        assertThat(Solution.isBalanced("([)]")).as("Example 3: isBalanced(\"([)]\")").isFalse();
        System.out.println("Examples passed: 3");
    }

    private static void runInput(String input) {
        String delimiters = unquote(input).replaceAll("\\s+", "");
        for (int i = 0; i < delimiters.length(); i++) {
            if ("()[]{}".indexOf(delimiters.charAt(i)) < 0) {
                System.out.println("The contract allows only ( ) [ ] { }, but the input contains '"
                        + delimiters.charAt(i) + "'. " + USAGE);
                return;
            }
        }
        System.out.println("isBalanced(\"" + delimiters + "\") = " + Solution.isBalanced(delimiters));
    }

    private static String unquote(String input) {
        return input.length() >= 2 && input.startsWith("\"") && input.endsWith("\"")
                ? input.substring(1, input.length() - 1)
                : input;
    }
}
