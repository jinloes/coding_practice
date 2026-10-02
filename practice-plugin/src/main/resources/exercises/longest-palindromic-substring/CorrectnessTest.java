package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CorrectnessTest {
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

    @Test
    void returnsEmptyForEmptyText() {
        assertLongest("", 0);
    }

    @Test
    void returnsTheSingleCharacter() {
        assertLongest("z", 1);
    }

    @Test
    void handlesWholeStringPalindromes() {
        assertLongest("racecar", 7);
        assertLongest("abccba", 6);
    }

    @Test
    void findsPalindromeAtTheStartAndEnd() {
        assertLongest("abacxyz", 3);
        assertLongest("xyzabba", 4);
    }

    @Test
    void prefersTheLongerOfSeveralPalindromes() {
        assertLongest("aabxyzyxbaa", 11);
        assertLongest("abaxcdeedcy", 6);
    }

    @Test
    void handlesRepeatedCharacters() {
        assertLongest("aaaa", 4);
        assertLongest("aaaaa", 5);
    }

    @Test
    void isCaseSensitiveAndKeepsSpaces() {
        assertLongest("Aa", 1);
        assertLongest("ab ba", 5);
        assertLongest("a b", 1);
    }

    @Test
    void handlesLongNestedEvenPalindrome() {
        String half = "abcdefghij".repeat(30);
        String text = "#" + half + new StringBuilder(half).reverse() + "$";
        assertLongest(text, 600);
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
