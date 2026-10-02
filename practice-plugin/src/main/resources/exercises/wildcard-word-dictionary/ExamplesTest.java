package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

class ExamplesTest {
    @Test
    void wildcardsMatchAnyLetter() {
        Solution dictionary = new Solution();
        dictionary.add("tree");
        dictionary.add("trap");
        dictionary.add("tone");
        assertThat(dictionary.matches("tr.e")).as("tr.e").isTrue();
        assertThat(dictionary.matches("t..e")).as("t..e").isTrue();
        assertThat(dictionary.matches("t.o")).as("t.o").isFalse();
    }

    @Test
    void patternMustMatchTheWholeWord() {
        Solution dictionary = new Solution();
        dictionary.add("cat");
        assertThat(dictionary.matches("cat")).isTrue();
        assertThat(dictionary.matches("ca")).isFalse();
        assertThat(dictionary.matches("cats")).isFalse();
    }

    @Test
    void emptyDictionaryMatchesNothing() {
        Solution dictionary = new Solution();
        assertThat(dictionary.matches("a")).isFalse();
        assertThat(dictionary.matches(".")).isFalse();
    }
}
