package com.jinloes.practice;

import static org.assertj.core.api.Assertions.assertThat;

public final class ExampleRunner {
    private static final String USAGE =
            "Input: one piece of text with no spaces, for example: abba";

    private ExampleRunner() {
    }

    public static void main(String[] args) {
        if (args.length > 0) {
            runInput(String.join(" ", args));
            return;
        }
        assertCount("abc", 3);
        assertCount("aaa", 6);
        assertCount("abba", 6);
        System.out.println("Examples passed: 3");
    }

    private static void assertCount(String text, int expected) {
        assertThat(Solution.countPalindromes(text))
                .as("countPalindromes(\"%s\")", text)
                .isEqualTo(expected);
    }

    private static void runInput(String input) {
        String text = input;
        if (text.isBlank() || text.chars().anyMatch(Character::isWhitespace) || text.length() > 50_000) {
            System.out.println("Expected 1 to 50000 characters without spaces. " + USAGE);
            return;
        }
        System.out.println("countPalindromes(\"" + text + "\") = " + Solution.countPalindromes(text));
    }
}
