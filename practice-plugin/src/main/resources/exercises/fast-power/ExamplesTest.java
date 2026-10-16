package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.withinPercentage;

import static org.assertj.core.api.Assertions.assertThat;

class ExamplesTest {
    @Test
    void positiveExponent() {
        check(2.0, 10, 1024.0);
    }

    @Test
    void negativeExponent() {
        check(2.0, -2, 0.25);
    }

    @Test
    void zeroExponent() {
        check(5.0, 0, 1.0);
    }

    private static void check(double base, int exponent, double expected) {
        assertThat(Solution.power(base, exponent)).as("power(%s, %d)".formatted(base, exponent))
                .isCloseTo(expected, withinPercentage(1e-9));
    }

    private static void exact(double base, int exponent, double expected) {
        assertThat(Solution.power(base, exponent)).as("power(%s, %d)".formatted(base, exponent)).isEqualTo(expected);
    }
}
