package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

class CorrectnessTest {
    @Test
    void noCarry() {
        check(new int[]{4, 2}, 4, 3);
    }

    @Test
    void carryStopsInTheMiddle() {
        check(new int[]{1, 9, 9}, 2, 0, 0);
    }

    @Test
    void singleNine() {
        check(new int[]{9}, 1, 0);
    }

    @Test
    void nineInTheMiddleOnly() {
        check(new int[]{8, 9, 1}, 8, 9, 2);
    }

    @Test
    void leadingNineTrailingEight() {
        check(new int[]{9, 8}, 9, 9);
    }

    @Test
    void longAllNines() {
        int[] digits = new int[500];
        Arrays.fill(digits, 9);
        int[] expected = new int[501];
        expected[0] = 1;
        check(digits, expected);
    }

    @Test
    void longNumberBeyondLongRange() {
        int[] digits = new int[40];
        Arrays.fill(digits, 7);
        int[] expected = digits.clone();
        expected[39] = 8;
        check(digits, expected);
    }

    private static void check(int[] digits, int... expected) {
        int[] original = digits.clone();
        String call = "increment(%s)".formatted(Arrays.toString(original));
        assertThat(Solution.increment(digits)).as(call).containsExactly(expected);
        assertThat(digits).as(call + " must not mutate the input").containsExactly(original);
    }
}
