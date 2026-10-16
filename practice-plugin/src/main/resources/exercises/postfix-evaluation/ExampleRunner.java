package com.jinloes.practice;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

public final class ExampleRunner {
    private static final String USAGE =
            "Input: tokens separated by spaces, each an integer or one of + - * /, for example: 6 4 5 + * 3 -";

    private ExampleRunner() {
    }

    public static void main(String[] args) {
        if (args.length > 0) {
            runInput(String.join(" ", args).trim());
            return;
        }
        assertThat(Solution.evaluate(new String[]{"7", "3", "-", "2", "*"}))
                .as("Example 1: 7 3 - 2 *").isEqualTo(8);
        assertThat(Solution.evaluate(new String[]{"-7", "2", "/"}))
                .as("Example 2: -7 2 /").isEqualTo(-3);
        assertThat(Solution.evaluate(new String[]{"6", "4", "5", "+", "*", "3", "-"}))
                .as("Example 3: 6 4 5 + * 3 -").isEqualTo(51);
        System.out.println("Examples passed: 3");
    }

    private static void runInput(String input) {
        String[] tokens = input.isEmpty() ? new String[0] : input.split("\\s+");
        if (!wellFormed(tokens)) {
            System.out.println("Could not read \"" + input + "\" as a postfix expression. " + USAGE);
            return;
        }
        System.out.println("evaluate(" + Arrays.toString(tokens) + ") = " + Solution.evaluate(tokens));
    }

    private static boolean wellFormed(String[] tokens) {
        int depth = 0;
        for (String token : tokens) {
            if (token.matches("[-+*/]")) {
                if (depth < 2) {
                    return false;
                }
                depth--;
            } else if (token.matches("-?\\d{1,9}")) {
                depth++;
            } else {
                return false;
            }
        }
        return depth == 1;
    }
}
