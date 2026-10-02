package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CorrectnessTest {
    @Test
    void handlesZeros() {
        check(0, 0, 0);
        check(0, 99, 99);
        check(-99, 0, -99);
    }

    @Test
    void addsTwoNegatives() {
        check(-1, -1, -2);
        check(-1000, -234, -1234);
    }

    @Test
    void carriesAcrossManyBits() {
        check(0xFF, 1, 0x100);
        check(0x7FFF, 0x7FFF, 0xFFFE);
        check(1 << 30, 1 << 30, Integer.MIN_VALUE);
    }

    @Test
    void wrapsAroundAtMaxValue() {
        check(Integer.MAX_VALUE, 1, Integer.MIN_VALUE);
        check(Integer.MAX_VALUE, Integer.MAX_VALUE, -2);
    }

    @Test
    void wrapsAroundAtMinValue() {
        check(Integer.MIN_VALUE, -1, Integer.MAX_VALUE);
        check(Integer.MIN_VALUE, Integer.MIN_VALUE, 0);
    }

    @Test
    void combinesExtremesOfOppositeSign() {
        check(Integer.MAX_VALUE, Integer.MIN_VALUE, -1);
        check(Integer.MIN_VALUE, Integer.MAX_VALUE, -1);
        check(Integer.MIN_VALUE, 0, Integer.MIN_VALUE);
    }

    @Test
    void isCommutative() {
        check(12345, -6789, 12345 + -6789);
        check(-6789, 12345, 12345 + -6789);
    }

    @Test
    void matchesBuiltInAdditionOnManyValues() {
        int seed = 7;
        for (int i = 0; i < 2000; i++) {
            seed = seed * 1664525 + 1013904223;
            int a = seed;
            seed = seed * 1664525 + 1013904223;
            int b = seed;
            check(a, b, a + b);
        }
    }

    private static void check(int a, int b, int expected) {
        assertThat(Solution.add(a, b)).as("add(%d, %d)", a, b).isEqualTo(expected);
    }
}
