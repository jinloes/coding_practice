package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CorrectnessTest {
    @Test
    void equalStringsAreZeroEditsAway() { assertThat(Solution.isOneEditAway("algorithm", "algorithm")).isTrue(); }
    @Test
    void oneReplacementCanMatch() { assertThat(Solution.isOneEditAway("algorithm", "algorXthm")).isTrue(); }
    @Test
    void lengthGapGreaterThanOneFails() { assertThat(Solution.isOneEditAway("cat", "catalog")).isFalse(); }
    @Test
    void transposedCharactersStillNeedTwoEdits() { assertThat(Solution.isOneEditAway("abcd", "abdc")).isFalse(); }
    @Test
    void longTailInsertionIsAccepted() { assertThat(Solution.isOneEditAway("a".repeat(5000), "a".repeat(5000) + "z")).isTrue(); }
}
