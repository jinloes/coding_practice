package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;

class ExamplesTest {
    @Test
    void findsTheSlowestFeasibleSpeed() {
        check(new int[]{4, 9, 11}, 5, 6);
    }

    @Test
    void oneHourPerPileNeedsTheBiggestPile() {
        check(new int[]{10, 10}, 2, 10);
    }

    @Test
    void plentyOfTimeAllowsSpeedOne() {
        check(new int[]{7, 1, 3}, 100, 1);
    }

    private static void check(int[] piles, int hours, int expected) {
        int[] original = piles.clone();
        String call = "minimumSpeed(%s, %d)".formatted(Arrays.toString(original), hours);
        assertThat(Solution.minimumSpeed(piles, hours)).as(call).isEqualTo(expected);
        assertThat(piles).as(call + " must not change the input").containsExactly(original);
    }
}
