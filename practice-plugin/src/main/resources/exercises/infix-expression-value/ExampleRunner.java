package com.jinloes.practice;

import static org.assertj.core.api.Assertions.assertThat;

public final class ExampleRunner {
    private static final String USAGE =
            "Input: an arithmetic expression using integers, + - * /, and parentheses, for example: 2 * (3 + 4) - 10 / 3";

    private ExampleRunner() {
    }

    public static void main(String[] args) {
        if (args.length > 0) {
            runInput(String.join(" ", args).trim());
            return;
        }
        assertThat(Solution.evaluate("1 + 2 * 3")).as("Example 1: 1 + 2 * 3").isEqualTo(7);
        assertThat(Solution.evaluate("(1 + 2) * 3")).as("Example 2: (1 + 2) * 3").isEqualTo(9);
        assertThat(Solution.evaluate("-(8 - 3) / 2")).as("Example 3: -(8 - 3) / 2").isEqualTo(-2);
        System.out.println("Examples passed: 3");
    }

    private static void runInput(String input) {
        if (!wellFormed(input)) {
            System.out.println("Could not read \"" + input + "\" as an arithmetic expression. " + USAGE);
            return;
        }
        try {
            System.out.println("evaluate(\"" + input + "\") = " + Solution.evaluate(input));
        } catch (ArithmeticException exception) {
            System.out.println("evaluate(\"" + input + "\") threw " + exception
                    + "; the contract excludes division by zero.");
        }
    }

    /** Checks tokens, operand and operator order, unary minus placement, and balanced parentheses. */
    private static boolean wellFormed(String input) {
        int depth = 0;
        boolean expectOperand = true;
        boolean unaryAllowed = true;
        int i = 0;
        while (i < input.length()) {
            char ch = input.charAt(i);
            if (ch == ' ') {
                i++;
                continue;
            }
            if (ch >= '0' && ch <= '9') {
                int end = i;
                while (end < input.length() && input.charAt(end) >= '0' && input.charAt(end) <= '9') {
                    end++;
                }
                if (!expectOperand || end - i > 10 || Long.parseLong(input.substring(i, end)) > Integer.MAX_VALUE) {
                    return false;
                }
                expectOperand = false;
                unaryAllowed = false;
                i = end;
                continue;
            }
            if (ch == '(') {
                if (!expectOperand) {
                    return false;
                }
                depth++;
                unaryAllowed = true;
            } else if (ch == ')') {
                if (expectOperand || depth == 0) {
                    return false;
                }
                depth--;
            } else if ("+-*/".indexOf(ch) >= 0) {
                if (expectOperand && !(ch == '-' && unaryAllowed)) {
                    return false;
                }
                expectOperand = true;
                unaryAllowed = false;
            } else {
                return false;
            }
            i++;
        }
        return !expectOperand && depth == 0;
    }
}
