package com.jinloes.practice;

import static org.assertj.core.api.Assertions.assertThat;

public final class ExampleRunner {
    private static final String USAGE =
            "Input: a lowercase text, a comma, then a pattern of letters, . and *, for example: moon, mo*n";

    private ExampleRunner() {
    }

    public static void main(String[] args) {
        if (args.length > 0) {
            runInput(String.join(" ", args).trim());
            return;
        }
        check("moon", "mo*n", true);
        check("moon", "mo.", false);
        check("", "a*b*", true);
        System.out.println("Examples passed: 3");
    }

    private static void check(String text, String pattern, boolean expected) {
        String call = "matches(\"%s\", \"%s\")".formatted(text, pattern);
        assertThat(Solution.matches(text, pattern)).as(call).isEqualTo(expected);
    }

    private static void runInput(String input) {
        String[] parts = input.split(",", -1);
        if (parts.length != 2 || !validText(parts[0].trim()) || !validPattern(parts[1].trim())) {
            System.out.println("Could not read \"" + input + "\" as a text and a pattern. " + USAGE);
            return;
        }
        String text = parts[0].trim();
        String pattern = parts[1].trim();
        System.out.println("matches(\"" + text + "\", \"" + pattern + "\") = " + Solution.matches(text, pattern));
    }

    private static boolean validText(String text) {
        return text.chars().allMatch(c -> c >= 'a' && c <= 'z');
    }

    private static boolean validPattern(String pattern) {
        for (int i = 0; i < pattern.length(); i++) {
            char c = pattern.charAt(i);
            if (c == '*') {
                if (i == 0 || pattern.charAt(i - 1) == '*') {
                    return false;
                }
            } else if (c != '.' && (c < 'a' || c > 'z')) {
                return false;
            }
        }
        return true;
    }
}
