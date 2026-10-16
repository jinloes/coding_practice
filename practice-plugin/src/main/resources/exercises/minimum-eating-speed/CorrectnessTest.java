package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;

class CorrectnessTest {
    @Test
    void singleHugePileInOneHour() {
        check(new int[]{1_000_000_000}, 1, 1_000_000_000);
    }

    @Test
    void hugePilesSplitAcrossTwoHoursEach() {
        check(new int[]{1_000_000_000, 1_000_000_000, 1_000_000_000}, 6, 500_000_000);
    }

    @Test
    void leftoverHoursAreWasted() {
        check(new int[]{5, 8, 2}, 6, 3);
    }

    @Test
    void hoursEqualToPileCount() {
        check(new int[]{3, 9, 6}, 3, 9);
    }

    @Test
    void allPilesOfOne() {
        check(new int[]{1, 1, 1}, 3, 1);
    }

    @Test
    void manySmallPiles() {
        check(new int[]{2, 2, 2, 2}, 8, 1);
    }

    @Test
    void mixedPiles() {
        check(new int[]{12, 5, 19, 8}, 7, 8);
    }

    @Test
    void barelyEnoughHours() {
        check(new int[]{1_000_000_000, 1_000_000_000}, 3, 1_000_000_000);
    }

    private static void check(int[] piles, int hours, int expected) {
        int[] original = piles.clone();
        String call = "minimumSpeed(%s, %d)".formatted(Arrays.toString(original), hours);
        assertThat(Solution.minimumSpeed(piles, hours)).as(call).isEqualTo(expected);
        assertThat(piles).as(call + " must not change the input").containsExactly(original);
    }
}
