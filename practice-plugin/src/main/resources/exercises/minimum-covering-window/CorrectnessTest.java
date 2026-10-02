package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CorrectnessTest {
    @Test
    void returnsEmptyWhenRequiredIsEmpty() {
        assertThat(Solution.minimumWindow("abc", "")).isEmpty();
    }

    @Test
    void returnsEmptyForAnEmptySource() {
        assertThat(Solution.minimumWindow("", "a")).isEmpty();
    }

    @Test
    void returnsEmptyWhenRequiredIsLongerThanSource() {
        assertThat(Solution.minimumWindow("ab", "abc")).isEmpty();
    }

    @Test
    void honorsMultiplicity() {
        assertThat(Solution.minimumWindow("abaab", "aab")).isEqualTo("aba");
    }

    @Test
    void returnsEmptyWhenACountIsTooLow() {
        assertThat(Solution.minimumWindow("abc", "aa")).isEmpty();
    }

    @Test
    void prefersTheLeftmostOfEqualShortestWindows() {
        assertThat(Solution.minimumWindow("abcdab", "ab")).isEqualTo("ab");
    }

    @Test
    void findsAShorterLaterWindow() {
        assertThat(Solution.minimumWindow("BAXCBA", "ABC")).isEqualTo("CBA");
    }

    @Test
    void returnsTheWholeSourceWhenAllIsNeeded() {
        assertThat(Solution.minimumWindow("xyz", "zyx")).isEqualTo("xyz");
    }

    @Test
    void isCaseSensitive() {
        assertThat(Solution.minimumWindow("aA", "Aa")).isEqualTo("aA");
    }

    @Test
    void countsSpacesAndSymbols() {
        assertThat(Solution.minimumWindow("a b c", " c")).isEqualTo(" c");
    }

    @Test
    void contractsPastIrrelevantLeadingCharacters() {
        assertThat(Solution.minimumWindow("zzzzabzzzzcz", "abc")).isEqualTo("abzzzzc");
    }

    @Test
    void handlesALongSource() {
        assertThat(Solution.minimumWindow("x".repeat(50_000) + "abc" + "x".repeat(50_000), "cba")).isEqualTo("abc");
    }
}
