package com.jinloes.practice;

import static org.assertj.core.api.Assertions.assertThat;

public final class ExampleRunner {
    private static final String USAGE =
            "Input: a Y/N log between single quotes, for example: 'YYNY'";

    private ExampleRunner() {
    }

    public static void main(String[] args) {
        if (args.length > 0) {
            runInput(String.join(" ", args).trim());
            return;
        }
        assertThat(Solution.bestClosingHour("YYNY"))
                .as("Example 1: bestClosingHour(\"YYNY\")").isEqualTo(2);
        assertThat(Solution.bestClosingHour("NNNN"))
                .as("Example 2: bestClosingHour(\"NNNN\")").isEqualTo(0);
        assertThat(Solution.bestClosingHour("YYYY"))
                .as("Example 3: bestClosingHour(\"YYYY\")").isEqualTo(4);
        System.out.println("Examples passed: 3");
    }

    private static void runInput(String input) {
        if (input.length() < 2 || input.charAt(0) != '\'' || input.charAt(input.length() - 1) != '\'') {
            System.out.println("Put the log between single quotes. " + USAGE);
            return;
        }
        String log = input.substring(1, input.length() - 1);
        if (!log.chars().allMatch(c -> c == 'Y' || c == 'N')) {
            System.out.println("Log may contain only 'Y' and 'N'. " + USAGE);
            return;
        }
        System.out.println("bestClosingHour(\"" + log + "\") = " + Solution.bestClosingHour(log));
    }
}
