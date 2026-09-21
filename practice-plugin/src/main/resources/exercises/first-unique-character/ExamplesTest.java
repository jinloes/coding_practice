package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ExamplesTest {
    @Test
    void findsAUniqueFirstCharacter() {
        assertThat(Solution.firstUniqueIndex("leetcode"))
                .as("firstUniqueIndex(\"leetcode\") must return the index of 'l'")
                .isEqualTo(0);
    }

    @Test
    void skipsRepeatedCharactersBeforeTheAnswer() {
        assertThat(Solution.firstUniqueIndex("loveleetcode"))
                .as("firstUniqueIndex(\"loveleetcode\") must return the index of 'v'")
                .isEqualTo(2);
    }

    @Test
    void reportsThatEveryCharacterRepeats() {
        assertThat(Solution.firstUniqueIndex("aabb"))
                .as("firstUniqueIndex(\"aabb\") must return -1 because nothing is unique")
                .isEqualTo(-1);
    }
}
