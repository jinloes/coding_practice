package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CorrectnessTest {
    @Test
    void keepsZeroUnchanged() {
        check(0, 0);
    }

    @Test
    void movesTopBitToBottom() {
        check(Integer.MIN_VALUE, 1);
    }

    @Test
    void reversesEveryPowerOfTwo() {
        for (int shift = 0; shift < 32; shift++) {
            check(1 << shift, 1 << (31 - shift));
        }
    }

    @Test
    void reversesLargestPositive() {
        check(Integer.MAX_VALUE, -2);
    }

    @Test
    void reversesByteBoundaries() {
        check(0xFF, 0xFF000000);
        check(0x0000FF00, 0x00FF0000);
    }

    @Test
    void keepsPalindromicPatterns() {
        check(0x80000001, 0x80000001);
        check(0x0FF00FF0, 0x0FF00FF0);
    }

    @Test
    void isItsOwnInverse() {
        int seed = 5;
        for (int i = 0; i < 500; i++) {
            seed = seed * 1664525 + 1013904223;
            assertThat(Solution.reverseBits(Solution.reverseBits(seed))).as("double reverse of %d", seed).isEqualTo(seed);
        }
    }

    @Test
    void matchesBuiltInOnManyValues() {
        int seed = 31;
        for (int i = 0; i < 2000; i++) {
            seed = seed * 1664525 + 1013904223;
            check(seed, Integer.reverse(seed));
        }
    }

    private static void check(int value, int expected) {
        assertThat(Solution.reverseBits(value)).as("reverseBits(%d)", value).isEqualTo(expected);
    }
}
