package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ExamplesTest {
    @Test
    void findsWordsThatDifferOnce() {
        assertThat(Solution.hasOneCharacterDifference(new String[]{"abcd", "acbd", "aacd"})).isTrue();
    }

    @Test
    void rejectsWordsThatDifferInBothPositions() {
        assertThat(Solution.hasOneCharacterDifference(new String[]{"ab", "cd", "yz"})).isFalse();
    }

    @Test
    void duplicateEqualWordsDoNotCount() {
        assertThat(Solution.hasOneCharacterDifference(new String[]{"same", "same"})).isFalse();
    }
}
