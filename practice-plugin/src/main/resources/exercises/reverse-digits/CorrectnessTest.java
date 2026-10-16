package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CorrectnessTest {
    @Test
    void zeroAndSingleDigits() {
        assertThat(Solution.reverseDigits(0)).isZero();
        assertThat(Solution.reverseDigits(7)).isEqualTo(7);
        assertThat(Solution.reverseDigits(-9)).isEqualTo(-9);
    }

    @Test
    void trailingZerosVanish() {
        assertThat(Solution.reverseDigits(1000)).isEqualTo(1);
        assertThat(Solution.reverseDigits(-120)).isEqualTo(-21);
    }

    @Test
    void largestValuesThatStillFit() {
        assertThat(Solution.reverseDigits(1463847412)).as("reversal is 2147483641").isEqualTo(2147483641);
        assertThat(Solution.reverseDigits(-1463847412)).as("reversal is -2147483641").isEqualTo(-2147483641);
        assertThat(Solution.reverseDigits(2147447412)).as("a palindrome-like value that fits").isEqualTo(2147447412);
    }

    @Test
    void positiveOverflowReturnsZero() {
        assertThat(Solution.reverseDigits(Integer.MAX_VALUE)).isZero();
        assertThat(Solution.reverseDigits(1463847413)).as("reversal 3147483641 is too big").isZero();
        assertThat(Solution.reverseDigits(1563847412)).isZero();
    }

    @Test
    void negativeOverflowReturnsZero() {
        assertThat(Solution.reverseDigits(Integer.MIN_VALUE)).isZero();
        assertThat(Solution.reverseDigits(-1563847412)).isZero();
        assertThat(Solution.reverseDigits(-2147483412)).as("reversal -2143847412 fits").isEqualTo(-2143847412);
    }

    @Test
    void matchesWideArithmeticForManyValues() {
        int[] seeds = {-100000, 99999, 123456789, 1000000009, -1000000009, 2000000000, -2000000000};
        for (int start : seeds) {
            for (int offset = 0; offset < 500; offset++) {
                int value = start + offset * 7919;
                assertThat(Solution.reverseDigits(value)).as("reverseDigits(%d)", value).isEqualTo(expected(value));
            }
        }
    }

    private static int expected(int value) {
        long magnitude = Math.abs((long) value);
        long reversed = 0;
        while (magnitude > 0) {
            reversed = reversed * 10 + magnitude % 10;
            magnitude /= 10;
        }
        long signed = value < 0 ? -reversed : reversed;
        return signed > Integer.MAX_VALUE || signed < Integer.MIN_VALUE ? 0 : (int) signed;
    }
}
