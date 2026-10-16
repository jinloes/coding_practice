package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CorrectnessTest {
    @Test
    void allEmptyStrings() {
        assertThat(Solution.isInterleaving("", "", "")).as("isInterleaving(\"\", \"\", \"\")").isEqualTo(true);
    }

    @Test
    void firstEmptyOtherMustMatch() {
        assertThat(Solution.isInterleaving("", "abc", "abd")).as("isInterleaving(\"\", \"abc\", \"abd\")").isEqualTo(false);
    }

    @Test
    void lengthMismatch() {
        assertThat(Solution.isInterleaving("ab", "cd", "abc")).as("isInterleaving(\"ab\", \"cd\", \"abc\")").isEqualTo(false);
    }

    @Test
    void plainConcatenation() {
        assertThat(Solution.isInterleaving("abc", "def", "abcdef")).as("isInterleaving(\"abc\", \"def\", \"abcdef\")").isEqualTo(true);
    }

    @Test
    void ambiguousPrefixNeedsBacktracking() {
        assertThat(Solution.isInterleaving("aab", "aac", "aaabac")).as("isInterleaving(\"aab\", \"aac\", \"aaabac\")").isEqualTo(true);
    }

    @Test
    void sameLetterEverywhere() {
        assertThat(Solution.isInterleaving("aaa", "aa", "aaaaa")).as("isInterleaving(\"aaa\", \"aa\", \"aaaaa\")").isEqualTo(true);
    }

    @Test
    void letterCountsMismatch() {
        assertThat(Solution.isInterleaving("abc", "abc", "abcabd")).as("isInterleaving(\"abc\", \"abc\", \"abcabd\")").isEqualTo(false);
    }

    @Test
    void orderWithinStringMatters() {
        assertThat(Solution.isInterleaving("ab", "cd", "cadb")).as("isInterleaving(\"ab\", \"cd\", \"cadb\")").isEqualTo(true);
    }

    @Test
    void orderViolated() {
        assertThat(Solution.isInterleaving("ab", "cd", "bacd")).as("isInterleaving(\"ab\", \"cd\", \"bacd\")").isEqualTo(false);
    }

    @Test
    void longerWeave() {
        assertThat(Solution.isInterleaving("abcde", "vwxyz", "avbwcxdyez")).as("isInterleaving(\"abcde\", \"vwxyz\", \"avbwcxdyez\")").isEqualTo(true);
    }

    @Test
    void caseSensitive() {
        assertThat(Solution.isInterleaving("Ab", "c", "abc")).as("isInterleaving(\"Ab\", \"c\", \"abc\")").isEqualTo(false);
    }
}
