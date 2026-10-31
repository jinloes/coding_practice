package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ExamplesTest {
    @Test
    void multiplicationBindsTighterThanAddition() {
        check(7, "1 + 2 * 3");
    }

    @Test
    void parenthesesGroupFirst() {
        check(9, "(1 + 2) * 3");
    }

    @Test
    void unaryMinusNegatesATruncatedQuotient() {
        check(-2, "-(8 - 3) / 2");
    }

    private static void check(int expected, String expression) {
        assertThat(Solution.evaluate(expression)).as("evaluate(\"%s\")", expression).isEqualTo(expected);
    }
}
