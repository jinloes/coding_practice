package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ExamplesTest {
    @Test
    void addsTwoPositives() {
        check(13, 29, 42);
    }

    @Test
    void addsNegativeAndPositive() {
        check(-5, 3, -2);
    }

    @Test
    void cancelsOppositeValues() {
        check(7, -7, 0);
    }

    private static void check(int a, int b, int expected) {
        assertThat(Solution.add(a, b)).as("add(%d, %d)", a, b).isEqualTo(expected);
    }
}
