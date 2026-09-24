package com.jinloes.practice;

import static org.assertj.core.api.Assertions.assertThat;

public final class ExampleRunner {
    private static final String USAGE =
            "Input: the two numbers separated by a space, for example: 456 77";

    private ExampleRunner() {
    }

    public static void main(String[] args) {
        if (args.length > 0) {
            runInput(String.join(" ", args).trim());
            return;
        }
        assertThat(Solution.addStrings("456", "77"))
                .as("Example 1: addStrings(\"456\", \"77\")").isEqualTo("533");
        assertThat(Solution.addStrings("1", "999"))
                .as("Example 2: addStrings(\"1\", \"999\")").isEqualTo("1000");
        assertThat(Solution.addStrings("0", "0"))
                .as("Example 3: addStrings(\"0\", \"0\")").isEqualTo("0");
        System.out.println("Examples passed: 3");
    }

    private static void runInput(String input) {
        String[] numbers = input.replace(',', ' ').replace('"', ' ').trim().split("\\s+");
        if (numbers.length != 2) {
            System.out.println("Expected exactly two numbers but read " + numbers.length + ". " + USAGE);
            return;
        }
        for (String number : numbers) {
            if (!number.matches("0|[1-9][0-9]*")) {
                System.out.println("\"" + number + "\" is not a non-negative decimal number without leading zeros. "
                        + USAGE);
                return;
            }
        }
        System.out.println("addStrings(\"" + numbers[0] + "\", \"" + numbers[1] + "\") = \""
                + Solution.addStrings(numbers[0], numbers[1]) + "\"");
    }
}
