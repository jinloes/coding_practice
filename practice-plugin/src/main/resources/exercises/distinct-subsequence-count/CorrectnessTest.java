package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CorrectnessTest {
    @Test
    void emptyPatternHasOneWay() {
        assertThat(Solution.countSubsequences("abc", "")).as("countSubsequences(\"abc\", \"\")").isEqualTo(1);
    }

    @Test
    void bothEmpty() {
        assertThat(Solution.countSubsequences("", "")).as("countSubsequences(\"\", \"\")").isEqualTo(1);
    }

    @Test
    void emptySourceNonEmptyPattern() {
        assertThat(Solution.countSubsequences("", "a")).as("countSubsequences(\"\", \"a\")").isEqualTo(0);
    }

    @Test
    void patternLongerThanSource() {
        assertThat(Solution.countSubsequences("ab", "abc")).as("countSubsequences(\"ab\", \"abc\")").isEqualTo(0);
    }

    @Test
    void identicalStrings() {
        assertThat(Solution.countSubsequences("hello", "hello")).as("countSubsequences(\"hello\", \"hello\")").isEqualTo(1);
    }

    @Test
    void repeatedLettersCountPositions() {
        assertThat(Solution.countSubsequences("rabbbit", "rabbit")).as("countSubsequences(\"rabbbit\", \"rabbit\")").isEqualTo(3);
    }

    @Test
    void orderMustBeKept() {
        assertThat(Solution.countSubsequences("cba", "abc")).as("countSubsequences(\"cba\", \"abc\")").isEqualTo(0);
    }

    @Test
    void singleLetterPattern() {
        assertThat(Solution.countSubsequences("banana", "a")).as("countSubsequences(\"banana\", \"a\")").isEqualTo(3);
    }

    @Test
    void overlappingChoices() {
        assertThat(Solution.countSubsequences("babgbag", "bag")).as("countSubsequences(\"babgbag\", \"bag\")").isEqualTo(5);
    }

    @Test
    void manyRepeats() {
        assertThat(Solution.countSubsequences("aaaaaaaaaaaaaaaaaaaa", "aaaaaaaaaa")).as("countSubsequences(\"aaaaaaaaaaaaaaaaaaaa\", \"aaaaaaaaaa\")").isEqualTo(184756);
    }

    @Test
    void randomLetters() {
        assertThat(Solution.countSubsequences("aaabbbababbaabaabbababbb", "abab")).as("countSubsequences(\"aaabbbababbaabaabbababbb\", \"abab\")").isEqualTo(1255);
    }

    @Test
    void caseSensitive() {
        assertThat(Solution.countSubsequences("AaAa", "Aa")).as("countSubsequences(\"AaAa\", \"Aa\")").isEqualTo(3);
    }
}
