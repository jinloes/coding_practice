package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ExamplesTest {
    @Test
    void findsAnExactSubstring() {
        check("deadbeef", "bee", true);
    }

    @Test
    void findsARearrangedSubstring() {
        check("abcxyzcba", "cab", true);
    }

    @Test
    void rejectsWindowsWithRepeatedLetters() {
        check("aabbcc", "abc", false);
    }

    private static void check(String text, String pattern, boolean expected) {
        String call = "containsPermutation(\"%s\", \"%s\")".formatted(text, pattern);
        assertThat(Solution.containsPermutation(text, pattern)).as(call).isEqualTo(expected);
    }
}
