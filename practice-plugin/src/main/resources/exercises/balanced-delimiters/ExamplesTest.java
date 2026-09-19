package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ExamplesTest {
    @Test
    void acceptsNestedDelimiters() {
        assertThat(Solution.isBalanced("{[()]}")).isTrue();
    }

    @Test
    void acceptsAdjacentGroups() {
        assertThat(Solution.isBalanced("([]{})")).isTrue();
    }

    @Test
    void rejectsCrossedDelimiters() {
        assertThat(Solution.isBalanced("([)]")).isFalse();
    }
}
