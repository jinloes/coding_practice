package com.jinloes.practice;

import static org.assertj.core.api.Assertions.assertThat;

public final class ExampleRunner {
    private static final String USAGE =
            "Input: a lowercase word between single quotes, for example: 'abca'";

    private ExampleRunner() {
    }

    public static void main(String[] args) {
        if (args.length > 0) {
            runInput(String.join(" ", args).trim());
            return;
        }
        assertThat(Solution.canBePalindrome("aba"))
                .as("Example 1: canBePalindrome(\"aba\")").isTrue();
        assertThat(Solution.canBePalindrome("abca"))
                .as("Example 2: canBePalindrome(\"abca\")").isTrue();
        assertThat(Solution.canBePalindrome("abc"))
                .as("Example 3: canBePalindrome(\"abc\")").isFalse();
        System.out.println("Examples passed: 3");
    }

    private static void runInput(String input) {
        if (input.length() < 2 || input.charAt(0) != '\'' || input.charAt(input.length() - 1) != '\'') {
            System.out.println("Put the word between single quotes. " + USAGE);
            return;
        }
        String text = input.substring(1, input.length() - 1);
        if (!text.chars().allMatch(c -> c >= 'a' && c <= 'z')) {
            System.out.println("Only lowercase English letters a-z are allowed. " + USAGE);
            return;
        }
        System.out.println("canBePalindrome(\"" + text + "\") = " + Solution.canBePalindrome(text));
    }
}
