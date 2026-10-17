package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CorrectnessTest {
    @Test
    void emptyAndSingleElementArraysHaveNoRepeat() { assertThat(Solution.nearestDistance(new String[]{})).isEqualTo(-1); assertThat(Solution.nearestDistance(new String[]{"one"})).isEqualTo(-1); }
    @Test
    void exactCaseSensitiveMatchingOnly() { assertThat(Solution.nearestDistance(new String[]{"Word", "word", "WORD"})).isEqualTo(-1); }
    @Test
    void laterCloserRepeatOverridesEarlierWideRepeat() { assertThat(Solution.nearestDistance(new String[]{"a", "b", "a", "c", "b"})).isEqualTo(2); }
    @Test
    void firstAdjacentPairShortCircuitsBestPossibleDistance() { assertThat(Solution.nearestDistance(new String[]{"a", "a", "b", "c", "b"})).isEqualTo(1); }
    @Test
    void handlesLargeInput() { String[] words = new String[5000]; for (int i = 0; i < words.length; i++) words[i] = "w" + i; words[4999] = "w2500"; words[4001] = "w4000"; assertThat(Solution.nearestDistance(words)).isEqualTo(1); }
}
