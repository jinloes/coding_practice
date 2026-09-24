package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

class CorrectnessTest {
    @Test
    void acceptsEmptyText() {
        assertPalindrome("");
    }

    @Test
    void acceptsASingleCharacter() {
        assertPalindrome("a");
        assertPalindrome("7");
        assertPalindrome("?");
    }

    @Test
    void acceptsTextMadeOnlyOfSpacesAndPunctuation() {
        assertPalindrome("  ,.;:!? ");
    }

    @Test
    void comparesLettersWithoutRegardToCase() {
        assertPalindrome("Aa");
        assertPalindrome("AbBa");
        assertPalindrome("No 'x' in Nixon");
    }

    @Test
    void requiresDigitsToMatchExactly() {
        assertPalindrome("12321");
        assertNotPalindrome("1231");
        assertNotPalindrome("0P");
    }

    @Test
    void handlesLettersMixedWithDigits() {
        assertPalindrome("a1b2b1a");
        assertNotPalindrome("1a2");
    }

    @Test
    void skipsPunctuationAtEitherEnd() {
        assertPalindrome("!!a!!");
        assertPalindrome("a.");
        assertPalindrome(".a");
        assertNotPalindrome(".ab.");
    }

    @Test
    void handlesEvenAndOddLengths() {
        assertPalindrome("abba");
        assertPalindrome("abcba");
        assertNotPalindrome("abca");
        assertNotPalindrome("ab");
    }

    @Test
    void findsAMismatchNextToTheMiddle() {
        assertNotPalindrome("abcdxcba");
        assertNotPalindrome("ab, c; d x c? b a");
    }

    @Test
    void checksLongTextWithNoise() {
        StringBuilder half = new StringBuilder();
        for (int i = 0; i < 1_000; i++) {
            half.append((char) ('a' + i % 26)).append(i % 3 == 0 ? ", " : "");
        }
        String palindrome = half + "Q" + new StringBuilder(half).reverse().toString().toUpperCase();
        assertPalindrome(palindrome);
        char[] broken = palindrome.toCharArray();
        broken[3] = broken[3] == 'z' ? 'y' : 'z';
        assertNotPalindrome(new String(broken));
    }

    private static void assertPalindrome(String text) {
        assertThat(isPalindrome(text))
                .as("%s must accept letters and digits that read the same both ways", call(text))
                .isTrue();
    }

    private static void assertNotPalindrome(String text) {
        assertThat(isPalindrome(text))
                .as("%s must reject letters and digits that differ when reversed", call(text))
                .isFalse();
    }

    private static boolean isPalindrome(String text) {
        try {
            return Solution.isPalindrome(text);
        } catch (Throwable thrown) {
            return fail("%s threw %s".formatted(call(text), thrown), thrown);
        }
    }

    private static String call(String text) {
        String shown = text.length() <= 40 ? text : text.substring(0, 40) + "... (" + text.length() + " characters)";
        return "isPalindrome(\"%s\")".formatted(shown);
    }
}
