package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.withinPercentage;

import static org.assertj.core.api.Assertions.assertThat;

class CorrectnessTest {
    @Test
    void negativeBase() {
        check(-2.0, 3, -8.0);
        check(-2.0, 4, 16.0);
    }

    @Test
    void fractionalBase() {
        check(1.5, 5, 7.59375);
    }

    @Test
    void negativePowerOfTen() {
        check(10.0, -3, 0.001);
    }

    @Test
    void zeroBase() {
        exact(0.0, 5, 0.0);
        exact(0.0, 0, 1.0);
        exact(0.0, -1, Double.POSITIVE_INFINITY);
    }

    @Test
    void minValueExponentWithUnitBases() {
        exact(1.0, Integer.MIN_VALUE, 1.0);
        exact(-1.0, Integer.MIN_VALUE, 1.0);
        exact(-1.0, Integer.MAX_VALUE, -1.0);
    }

    @Test
    void minValueExponentUnderflowsAndOverflows() {
        exact(2.0, Integer.MIN_VALUE, 0.0);
        exact(0.5, Integer.MIN_VALUE, Double.POSITIVE_INFINITY);
        exact(2.0, Integer.MAX_VALUE, Double.POSITIVE_INFINITY);
    }

    @Test
    void largeExponentNearOne() {
        check(1.0000001, 1_000_000, Math.pow(1.0000001, 1_000_000));
    }

    @Test
    void bigIntegerPower() {
        check(3.0, 20, 3486784401.0);
    }

    private static void check(double base, int exponent, double expected) {
        assertThat(Solution.power(base, exponent)).as("power(%s, %d)".formatted(base, exponent))
                .isCloseTo(expected, withinPercentage(1e-9));
    }

    private static void exact(double base, int exponent, double expected) {
        assertThat(Solution.power(base, exponent)).as("power(%s, %d)".formatted(base, exponent)).isEqualTo(expected);
    }
}
