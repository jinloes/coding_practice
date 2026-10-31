package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CorrectnessTest {
    @Test
    void singleNumberIsItsOwnValue() {
        check(42, "42");
        check(0, "0");
        check(7, "  7  ");
        check(1234, "1234");
    }

    @Test
    void appliesPrecedence() {
        check(9, "2 + 3 * 4 - 5");
        check(8, "10 - 4 / 2");
        check(26, "2 * 3 + 4 * 5");
        check(14, "2 + 3 * 4");
    }

    @Test
    void equalPrecedenceAppliesLeftToRight() {
        check(5, "10 - 3 - 2");
        check(2, "100 / 10 / 5");
        check(4, "8 / 4 * 2");
        check(3, "2 - 3 + 4");
        check(0, "1 / 3 * 3");
    }

    @Test
    void evaluatesParentheses() {
        check(15, "(2 + 3) * (4 - 1)");
        check(7, "((((7))))");
        check(18, "2 * (3 + (4 - 1) * 2)");
        check(9, "10 - (3 - 2)");
        check(1, "(10 - 3) - (8 - 2)");
    }

    @Test
    void handlesUnaryMinus() {
        check(-5, "-5");
        check(-2, "-5 + 3");
        check(-8, "(-4) * 2");
        check(-1, "3 * (-2 + 1) + 2");
        check(4, "-(-(4))");
        check(-10, "-(2 + 3) * 2");
        check(-6, "-2 * 3");
    }

    @Test
    void divisionTruncatesTowardZero() {
        check(3, "7 / 2");
        check(-3, "-7 / 2");
        check(-3, "7 / (-2)");
        check(3, "(0 - 7) / (0 - 2)");
        check(-1, "(-7) / 4");
    }

    @Test
    void spacingDoesNotMatter() {
        check(7, "1+2*3");
        check(9, "  ( 1+2 )*  3 ");
        check(46, "12 + 34");
        check(46, "12+34");
    }

    @Test
    void handlesValuesAtTheIntLimits() {
        check(Integer.MAX_VALUE, "2147483647");
        check(Integer.MAX_VALUE, "2147483647 - 1 + 1");
        check(Integer.MIN_VALUE, "-2147483647 - 1");
        check(2_147_395_600, "46340 * 46340");
        check(-2_147_483_647, "-(2147483647)");
    }

    @Test
    void handlesDeepNesting() {
        check(2, "(".repeat(1_000) + "1 + 1" + ")".repeat(1_000));
        check(3, "-(".repeat(500) + "3" + ")".repeat(500));
        check(-3, "-(".repeat(499) + "3" + ")".repeat(499));
    }

    @Test
    void evaluatesALongExpression() {
        check(20_000, "1" + "+1".repeat(19_999));
        check(5_001, "1" + "+(2*3-5)".repeat(5_000));
        check(1, "2" + "-1".repeat(20_000) + "+1".repeat(19_999) + "*1");
    }

    private static void check(int expected, String expression) {
        String shown = expression.length() > 60 ? expression.substring(0, 60) + "..." : expression;
        assertThat(Solution.evaluate(expression)).as("evaluate(\"%s\")", shown).isEqualTo(expected);
    }
}
