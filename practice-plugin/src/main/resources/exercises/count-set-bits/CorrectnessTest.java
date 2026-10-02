package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CorrectnessTest {
    @Test
    void countsSingleBit() {
        check(1, 1);
        check(2, 1);
        check(1 << 20, 1);
    }

    @Test
    void countsEveryPowerOfTwo() {
        for (int shift = 0; shift < 32; shift++) {
            check(1 << shift, 1);
        }
    }

    @Test
    void countsSignBitAlone() {
        check(Integer.MIN_VALUE, 1);
    }

    @Test
    void countsLargestPositive() {
        check(Integer.MAX_VALUE, 31);
    }

    @Test
    void countsNegativeValues() {
        check(-2, 31);
        check(-8, 29);
        check(-1000, Integer.bitCount(-1000));
    }

    @Test
    void countsAlternatingPatterns() {
        check(0x55555555, 16);
        check(0xAAAAAAAA, 16);
        check(0xF0F0F0F0, 16);
    }

    @Test
    void countsAllOnesBelowSignBit() {
        check(0x7FFFFFFE, 30);
        check(0x0000FFFF, 16);
    }

    @Test
    void matchesBuiltInOnManyValues() {
        int seed = 99;
        for (int i = 0; i < 2000; i++) {
            seed = seed * 1664525 + 1013904223;
            check(seed, Integer.bitCount(seed));
        }
    }

    private static void check(int value, int expected) {
        assertThat(Solution.countSetBits(value)).as("countSetBits(%d)", value).isEqualTo(expected);
    }
}
