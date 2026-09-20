package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ExamplesTest {
    @Test
    void acceptsNestedDelimiters() {
        assertThat(Solution.isBalanced("{[()]}"))
                .as("isBalanced(\"{[()]}\") must accept this balanced input").isTrue();
    }

    @Test
    void acceptsAdjacentGroups() {
        assertThat(Solution.isBalanced("([]{})"))
                .as("isBalanced(\"([]{})\") must accept this balanced input").isTrue();
    }

    @Test
    void rejectsCrossedDelimiters() {
        assertThat(Solution.isBalanced("([)]"))
                .as("isBalanced(\"([)]\") must reject these crossed delimiters").isFalse();
    }
}
