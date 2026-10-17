package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CorrectnessTest {
    @Test
    void handlesEmptyAndSingleWordLists() {
        assertThat(Solution.hasOneCharacterDifference(new String[]{})).isFalse();
        assertThat(Solution.hasOneCharacterDifference(new String[]{"alone"})).isFalse();
    }

    @Test
    void findsDifferenceAtLastCharacter() {
        assertThat(Solution.hasOneCharacterDifference(new String[]{"stone", "stony", "crane"})).isTrue();
    }

    @Test
    void ignoresPairsWithZeroOrMultipleDifferences() {
        assertThat(Solution.hasOneCharacterDifference(new String[]{"aaaa", "aaaa", "bbbb", "cccc"})).isFalse();
    }

    @Test
    void handlesLargerSeededSet() {
        String[] words = new String[300];
        for (int i = 0; i < words.length; i++) {
            words[i] = "word" + (char) ('a' + (i % 26)) + (char) ('a' + ((i / 26) % 26));
        }
        words[123] = "abcde";
        words[260] = "abfde";
        assertThat(Solution.hasOneCharacterDifference(words)).isTrue();
    }
}
