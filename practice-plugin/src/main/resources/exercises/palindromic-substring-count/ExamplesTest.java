package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ExamplesTest {
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

    private static void assertCount(String text, int expected) {
        assertThat(Solution.countPalindromes(text))
                .as("countPalindromes(\"%s\")", text)
                .isEqualTo(expected);
    }
}
