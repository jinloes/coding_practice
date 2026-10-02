package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ExamplesTest {
    @Test
    void countsBitsOfTypicalValue() {
        check(11, 3);
    }

    @Test
    void countsNothingInZero() {
        check(0, 0);
    }

    @Test
    void countsEveryBitOfMinusOne() {
        check(-1, 32);
    }

    private static void check(int value, int expected) {
        assertThat(Solution.countSetBits(value)).as("countSetBits(%d)", value).isEqualTo(expected);
    }
}
