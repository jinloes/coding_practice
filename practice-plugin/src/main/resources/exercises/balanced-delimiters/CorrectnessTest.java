package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CorrectnessTest {
    @Test
    void acceptsEmptyInput() {
        assertThat(Solution.isBalanced("")).isTrue();
    }

    @Test
    void handlesEachPairType() {
        assertThat(Solution.isBalanced("()")).isTrue();
        assertThat(Solution.isBalanced("[]")).isTrue();
        assertThat(Solution.isBalanced("{}")).isTrue();
    }

    @Test
    void rejectsAClosingDelimiterWithoutAnOpeningOne() {
        assertThat(Solution.isBalanced("]")).isFalse();
        assertThat(Solution.isBalanced("())")).isFalse();
    }

    @Test
    void rejectsAnUnclosedOpeningDelimiter() {
        assertThat(Solution.isBalanced("{{")).isFalse();
        assertThat(Solution.isBalanced("[{()}")).isFalse();
    }

    @Test
    void distinguishesDifferentDelimiterKinds() {
        assertThat(Solution.isBalanced("(]")).isFalse();
        assertThat(Solution.isBalanced("{[}]")).isFalse();
    }

    @Test
    void handlesAdjacentAndDeepSequences() {
        StringBuilder input = new StringBuilder();
        for (int i = 0; i < 128; i++) {
            input.append("([{".charAt(i % 3));
        }
        for (int i = 127; i >= 0; i--) {
            input.append(")]}".charAt(i % 3));
        }
        assertThat(Solution.isBalanced(input.toString())).isTrue();
    }

    @Test
    void rejectsADeepSequenceWithOneWrongCloser() {
        assertThat(Solution.isBalanced("{{[()]}}]")).isFalse();
    }

    @Test
    void handlesLongAdjacentPairs() {
        StringBuilder input = new StringBuilder();
        for (int i = 0; i < 100; i++) {
            input.append("()");
        }
        assertThat(Solution.isBalanced(input.toString())).isTrue();
    }
}
