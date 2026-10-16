package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ExamplesTest {
    @Test
    void starCanOpen() {
        check("(*))", true);
    }

    @Test
    void trailingOpenCanNeverClose() {
        check("*(", false);
    }

    @Test
    void emptyIsBalanced() {
        check("", true);
    }

    private static void check(String pattern, boolean expected) {
        assertThat(Solution.isBalanced(pattern)).as("isBalanced(\"%s\")".formatted(pattern)).isEqualTo(expected);
    }
}
