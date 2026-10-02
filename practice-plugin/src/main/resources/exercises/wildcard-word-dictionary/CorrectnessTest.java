package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

class CorrectnessTest {
    @Test
    void leadingWildcard() {
        Solution dictionary = new Solution();
        dictionary.add("bat");
        assertThat(dictionary.matches(".at")).isTrue();
        assertThat(dictionary.matches(".ot")).isFalse();
    }

    @Test
    void allWildcardsMatchAnyWordOfThatLength() {
        Solution dictionary = new Solution();
        dictionary.add("dog");
        assertThat(dictionary.matches("...")).isTrue();
        assertThat(dictionary.matches("..")).isFalse();
        assertThat(dictionary.matches("....")).isFalse();
    }

    @Test
    void backtracksAcrossWildcardBranches() {
        Solution dictionary = new Solution();
        dictionary.add("ab");
        dictionary.add("ac");
        dictionary.add("bz");
        assertThat(dictionary.matches(".c")).isTrue();
        assertThat(dictionary.matches(".z")).isTrue();
        assertThat(dictionary.matches(".y")).isFalse();
    }

    @Test
    void wildcardInTheLastBranchOnly() {
        Solution dictionary = new Solution();
        dictionary.add("aax");
        dictionary.add("abx");
        dictionary.add("azy");
        assertThat(dictionary.matches("a.y")).isTrue();
        assertThat(dictionary.matches("a.z")).isFalse();
    }

    @Test
    void prefixOfAStoredWordIsNotAMatch() {
        Solution dictionary = new Solution();
        dictionary.add("apple");
        assertThat(dictionary.matches("app..")).isTrue();
        assertThat(dictionary.matches("app.")).isFalse();
        assertThat(dictionary.matches("app")).isFalse();
    }

    @Test
    void addingTwiceIsHarmless() {
        Solution dictionary = new Solution();
        dictionary.add("go");
        dictionary.add("go");
        assertThat(dictionary.matches("g.")).isTrue();
        assertThat(dictionary.matches(".o")).isTrue();
    }

    @Test
    void laterWordsExtendEarlierOnes() {
        Solution dictionary = new Solution();
        dictionary.add("car");
        assertThat(dictionary.matches("car.")).isFalse();
        dictionary.add("cart");
        assertThat(dictionary.matches("car.")).isTrue();
        assertThat(dictionary.matches("car")).isTrue();
    }

    @Test
    void handlesLongPatternsWithManyWildcards() {
        Solution dictionary = new Solution();
        String word = "abcdefghij".repeat(50);
        dictionary.add(word);
        assertThat(dictionary.matches(".".repeat(500))).isTrue();
        assertThat(dictionary.matches(".".repeat(499))).isFalse();
        assertThat(dictionary.matches(word.substring(0, 499) + ".")).isTrue();
        assertThat(dictionary.matches(word.substring(0, 499) + "z")).isFalse();
    }
}
