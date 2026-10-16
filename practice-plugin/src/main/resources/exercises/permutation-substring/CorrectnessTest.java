package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CorrectnessTest {
    @Test
    void emptyPatternAlwaysMatches() {
        check("", "", true);
        check("abc", "", true);
    }

    @Test
    void patternLongerThanTextNeverMatches() {
        check("ab", "abc", false);
        check("", "a", false);
    }

    @Test
    void matchesTheWholeTextOrASingleLetter() {
        check("listen", "silent", true);
        check("xyz", "y", true);
        check("xyz", "w", false);
    }

    @Test
    void matchesAtTheStartAndTheEnd() {
        check("abcdefg", "bac", true);
        check("zzzabc", "cba", true);
    }

    @Test
    void countsLettersNotJustPresence() {
        check("abbbc", "aabc", false);
        check("xabbcx", "aabc", false);
        check("xabacx", "aabc", true);
    }

    @Test
    void requiresTheLettersToBeContiguous() {
        check("abxc", "abc", false);
        check("axbxc", "abc", false);
    }

    @Test
    void windowDropsLettersThatLeave() {
        check("aaabbbccc", "abc", false);
        check("aaabcbbb", "bca", true);
    }

    @Test
    void scansALongText() {
        check("a".repeat(50_000), "ab", false);
        check("a".repeat(50_000) + "ba", "ab", true);
    }

    private static void check(String text, String pattern, boolean expected) {
        String call = "containsPermutation(\"%s\", \"%s\")".formatted(text, pattern);
        assertThat(Solution.containsPermutation(text, pattern)).as(call).isEqualTo(expected);
    }
}
