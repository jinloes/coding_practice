package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CorrectnessTest {
    @Test
    void countsSingleCharactersOnly() {
        assertCount("abc", 3);
    }

    @Test
    void countsOverlappingPalindromes() {
        assertCount("aaa", 6);
    }

    @Test
    void countsEvenAndOddPalindromes() {
        assertCount("abba", 6);
    }

    @Test
    void countsNothingForEmptyText() {
        assertCount("", 0);
    }

    @Test
    void countsSingleCharacter() {
        assertCount("q", 1);
    }

    @Test
    void countsEvenPalindromesAlone() {
        assertCount("aa", 3);
        assertCount("abccba", 9);
    }

    @Test
    void countsEqualSubstringsAtDifferentPositions() {
        assertCount("abab", 6);
        assertCount("aba aba", 12);
    }

    @Test
    void isCaseSensitive() {
        assertCount("aA", 2);
        assertCount("AbBa", 4);
    }

    @Test
    void countsSpacesAsCharacters() {
        assertCount("a a", 4);
    }

    @Test
    void countsMixedLengthPalindromes() {
        assertCount("racecar", 10);
    }

    @Test
    void doesNotOverflowOnLongRepeatedText() {
        assertCount("a".repeat(50_000), 1_250_025_000);
    }

    private static void assertCount(String text, int expected) {
        assertThat(Solution.countPalindromes(text))
                .as("countPalindromes(\"%s\")", text)
                .isEqualTo(expected);
    }
}
