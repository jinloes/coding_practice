package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ExamplesTest {
    @Test
    void prefixShorterWordCanComeFirst() {
        assertThat(Solution.isSorted(new String[]{"app", "apple"}, "abcdefghijklmnopqrstuvwxyz")).isTrue();
    }

    @Test
    void longerPrefixWordCannotComeFirst() {
        assertThat(Solution.isSorted(new String[]{"apple", "app"}, "abcdefghijklmnopqrstuvwxyz")).isFalse();
    }

    @Test
    void firstDifferentLetterUsesAlienRank() {
        assertThat(Solution.isSorted(new String[]{"zen", "zip"}, "abcdefghijklmnopqrstuvwxyz")).isTrue();
    }
}
