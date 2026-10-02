package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ExamplesTest {
    @Test
    void findsEvenLengthPalindrome() {
        assertLongest("xabbay", 4);
    }

    @Test
    void findsOddLengthPalindrome() {
        assertLongest("qrsr", 3);
    }

    @Test
    void acceptsAnySingleCharacterWhenNothingRepeats() {
        assertLongest("ab", 1);
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
}
