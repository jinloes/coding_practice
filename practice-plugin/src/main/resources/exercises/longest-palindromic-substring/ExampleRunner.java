package com.jinloes.practice;

import static org.assertj.core.api.Assertions.assertThat;

public final class ExampleRunner {
    private static final String USAGE =
            "Input: one piece of text with no spaces, for example: xabbay";

    private ExampleRunner() {
    }

    public static void main(String[] args) {
        if (args.length > 0) {
            runInput(String.join(" ", args));
            return;
        }
        assertLongest("xabbay", 4);
        assertLongest("qrsr", 3);
        assertLongest("ab", 1);
        System.out.println("Examples passed: 3");
    }

    private static void assertLongest(String text, int expectedLength) {
        String result = Solution.longestPalindrome(text);
        assertThat(result).as("longestPalindrome(\"%s\") must not be null", text).isNotNull();
        assertThat(result.length())
                .as("longestPalindrome(\"%s\") returned \"%s\"", text, result)
                .isEqualTo(expectedLength);
        assertThat(new StringBuilder(result).reverse().toString())
                .as("\"%s\" returned for \"%s\" must read the same backwards", result, text)
                .isEqualTo(result);
        assertThat(text).as("\"%s\" must be a contiguous part of \"%s\"", result, text).contains(result);
    }

    private static void runInput(String input) {
        String text = input;
        if (text.isBlank() || text.chars().anyMatch(Character::isWhitespace)) {
            System.out.println("Could not use that text. " + USAGE);
            return;
        }
        String result = Solution.longestPalindrome(text);
        System.out.println("longestPalindrome(\"" + text + "\") = \"" + result + "\" (length "
                + (result == null ? 0 : result.length()) + ")");
    }
}
