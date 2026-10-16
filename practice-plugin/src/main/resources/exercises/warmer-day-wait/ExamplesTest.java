package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

class ExamplesTest {
    @Test
    void countsDaysUntilWarmer() {
        check(new int[]{68, 70, 65, 66, 72, 60}, 1, 3, 1, 1, 0, 0);
    }

    @Test
    void equalTemperatureIsNotWarmer() {
        check(new int[]{50, 50, 50}, 0, 0, 0);
    }

    @Test
    void risingTemperaturesWaitOneDay() {
        check(new int[]{60, 61, 62, 63}, 1, 1, 1, 0);
    }

    private static void check(int[] temperatures, int... expected) {
        int[] original = temperatures.clone();
        String call = "daysUntilWarmer(%s)".formatted(Arrays.toString(original));
        assertThat(Solution.daysUntilWarmer(temperatures)).as(call).containsExactly(expected);
        assertThat(temperatures).as(call + " must not change the input").containsExactly(original);
    }
}
