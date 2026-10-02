package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ExamplesTest {
    @Test
    void movesLowestBitToTop() {
        check(1, Integer.MIN_VALUE);
    }

    @Test
    void reversesShortPattern() {
        check(6, 0x60000000);
    }

    @Test
    void keepsAllOnesUnchanged() {
        check(-1, -1);
    }

    private static void check(int value, int expected) {
        assertThat(Solution.reverseBits(value)).as("reverseBits(%d)", value).isEqualTo(expected);
    }
}
