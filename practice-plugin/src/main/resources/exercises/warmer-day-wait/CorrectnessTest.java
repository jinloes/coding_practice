package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

class CorrectnessTest {
    @Test
    void emptyLogHasNoAnswers() {
        check(new int[]{});
    }

    @Test
    void singleDayNeverWaits() {
        check(new int[]{5}, 0);
    }

    @Test
    void fallingTemperaturesNeverWarm() {
        check(new int[]{90, 80, 70, 60}, 0, 0, 0, 0);
    }

    @Test
    void lateJumpSettlesEveryEarlierDay() {
        check(new int[]{30, 20, 10, 40}, 3, 2, 1, 0);
    }

    @Test
    void negativeTemperaturesWork() {
        check(new int[]{-5, -10, -3, -3, -8}, 2, 1, 0, 0, 0);
    }

    @Test
    void equalReadingsBeforeAWarmerDay() {
        check(new int[]{5, 5, 6}, 2, 1, 0);
    }

    @Test
    void mixedPlateausAndPeaks() {
        check(new int[]{9, 3, 7, 7, 7, 2, 8, 8, 1}, 0, 1, 4, 3, 2, 1, 0, 0, 0);
    }

    @Test
    void extremeValuesDoNotOverflow() {
        check(new int[]{Integer.MIN_VALUE, Integer.MAX_VALUE, Integer.MIN_VALUE}, 1, 0, 0);
    }

    private static void check(int[] temperatures, int... expected) {
        int[] original = temperatures.clone();
        String call = "daysUntilWarmer(%s)".formatted(Arrays.toString(original));
        assertThat(Solution.daysUntilWarmer(temperatures)).as(call).containsExactly(expected);
        assertThat(temperatures).as(call + " must not change the input").containsExactly(original);
    }
}
