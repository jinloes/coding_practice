package com.jinloes.practice;

import static org.assertj.core.api.Assertions.assertThat;

public final class ExampleRunner {
    private static final String USAGE =
            "Input: the two strings separated by a space, for example: abcde ace";

    private ExampleRunner() {
    }

    public static void main(String[] args) {
        if (args.length > 0) {
            runInput(String.join(" ", args).trim());
            return;
        }
        assertThat(Solution.lcsLength("abcde", "ace")).as("Example 1: lcsLength(abcde, ace)").isEqualTo(3);
        assertThat(Solution.lcsLength("abc", "xyz")).as("Example 2: lcsLength(abc, xyz)").isEqualTo(0);
        assertThat(Solution.lcsLength("axbycz", "abc")).as("Example 3: lcsLength(axbycz, abc)").isEqualTo(3);
        System.out.println("Examples passed: 3");
    }

    private static void runInput(String input) {
        String[] parts = input.split("\\s+");
        if (parts.length != 2 || parts[0].isEmpty()) {
            System.out.println("Expected exactly two strings. " + USAGE);
            return;
        }
        System.out.println("lcsLength(" + parts[0] + ", " + parts[1] + ") = "
                + Solution.lcsLength(parts[0], parts[1]));
    }
}
