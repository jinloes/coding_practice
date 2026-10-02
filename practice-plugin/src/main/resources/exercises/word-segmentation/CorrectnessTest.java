package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CorrectnessTest {
    @Test
    void treatsAnEmptyTextAsSegmentable() {
        assertThat(Solution.canSegment("", List.of("a"))).isTrue();
        assertThat(Solution.canSegment("", List.of())).isTrue();
    }

    @Test
    void rejectsAnyTextWithAnEmptyDictionary() {
        assertThat(Solution.canSegment("a", List.of())).isFalse();
    }

    @Test
    void reusesWords() {
        assertThat(Solution.canSegment("aaaaaaa", List.of("aaaa", "aaa"))).isTrue();
        assertThat(Solution.canSegment("aaaaa", List.of("aaaa", "aaa"))).isFalse();
    }

    @Test
    void backtracksPastAGreedyPrefix() {
        assertThat(Solution.canSegment("abc", List.of("a", "ab", "bc"))).isTrue();
        assertThat(Solution.canSegment("catsandog", List.of("cats", "dog", "sand", "and", "cat"))).isFalse();
    }

    @Test
    void matchesWholeWordsOnly() {
        assertThat(Solution.canSegment("cat", List.of("cats"))).isFalse();
        assertThat(Solution.canSegment("cats", List.of("cat"))).isFalse();
    }

    @Test
    void isCaseSensitive() {
        assertThat(Solution.canSegment("Applepie", List.of("apple", "pie"))).isFalse();
    }

    @Test
    void handlesDuplicateDictionaryWords() {
        assertThat(Solution.canSegment("abab", List.of("ab", "ab"))).isTrue();
    }

    @Test
    void handlesALongUnsegmentableText() {
        assertThat(Solution.canSegment("a".repeat(5000) + "b", List.of("a", "aa", "aaa"))).isFalse();
        assertThat(Solution.canSegment("a".repeat(5000), List.of("aa", "aaa"))).isTrue();
    }

    @Test
    void doesNotMutateTheDictionary() {
        List<String> words = new ArrayList<>(List.of("b", "a"));
        Solution.canSegment("ab", words);
        assertThat(words).containsExactly("b", "a");
    }
}
