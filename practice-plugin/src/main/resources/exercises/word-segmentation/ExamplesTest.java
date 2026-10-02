package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ExamplesTest {
    @Test
    void splitsIntoTwoWords() {
        assertThat(Solution.canSegment("applepie", List.of("apple", "pie"))).isTrue();
    }

    @Test
    void needsAnEarlierShortWordInsteadOfTheLongestMatch() {
        assertThat(Solution.canSegment("abcd", List.of("a", "abc", "b", "cd"))).isTrue();
    }

    @Test
    void rejectsATextWithAnUncoveredTail() {
        assertThat(Solution.canSegment("abce", List.of("abc", "d"))).isFalse();
    }
}
