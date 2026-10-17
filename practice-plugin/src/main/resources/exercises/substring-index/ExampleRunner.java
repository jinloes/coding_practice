package com.jinloes.practice;

import static org.assertj.core.api.Assertions.assertThat;

public final class ExampleRunner {
    private static final String USAGE =
            "Input: text, then pattern separated by |, for example: abc123|c12";

    private ExampleRunner() {
    }

    public static void main(String[] args) {
        if (args.length > 0) {
            runInput(String.join(" ", args));
            return;
        }
        assertThat(Solution.substringIndex("abc123", "abc")).isEqualTo(0);
        assertThat(Solution.substringIndex("abc123", "c12")).isEqualTo(2);
        assertThat(Solution.substringIndex("abc123", "xyz")).isEqualTo(-1);
        System.out.println("Examples passed: 3");
    }

    private static void runInput(String input) {
        int separator = input.indexOf('|');
        if (separator < 0 || input.indexOf('|', separator + 1) >= 0) {
            System.out.println("Expected exactly one | separator. " + USAGE);
            return;
        }
        String text = input.substring(0, separator);
        String pattern = input.substring(separator + 1);
        System.out.println("substringIndex(\"" + text + "\", \"" + pattern + "\") = "
                + Solution.substringIndex(text, pattern));
    }
}
