package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

class CorrectnessTest {
    @Test
    void twoBalloons() {
        check(new int[]{2, 3}, 9);
    }

    @Test
    void zeroBalloonsInsideStillHelp() {
        check(new int[]{5, 0, 5}, 30);
    }

    @Test
    void zeroCenterAmongNines() {
        check(new int[]{0, 9, 0}, 9);
    }

    @Test
    void fourBalloons() {
        check(new int[]{4, 2, 6, 1}, 84);
    }

    @Test
    void allOnesAddUpToCount() {
        check(new int[]{1, 1, 1, 1, 1}, 5);
    }

    @Test
    void alternatingLargeAndSmall() {
        check(new int[]{9, 1, 9, 1, 9}, 981);
    }

    @Test
    void moreBalloons() {
        check(new int[]{2, 4, 3}, 33);
    }

    private static void check(int[] values, int expected) {
        int[] copy = values.clone();
        String call = "maxCoins(%s)".formatted(Arrays.toString(values));
        assertThat(Solution.maxCoins(values)).as(call).isEqualTo(expected);
        assertThat(values).as(call + " must not modify its input").containsExactly(copy);
    }
}
