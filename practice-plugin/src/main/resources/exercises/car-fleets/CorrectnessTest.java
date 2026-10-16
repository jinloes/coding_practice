package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;

class CorrectnessTest {
    @Test
    void noCarsMeansNoFleets() {
        check(10, new int[]{}, new int[]{}, 0);
    }

    @Test
    void carsMeetingExactlyAtTheLineShareAFleet() {
        check(10, new int[]{0, 5}, new int[]{2, 1}, 1);
    }

    @Test
    void inputOrderDoesNotMatter() {
        check(10, new int[]{8, 2, 5}, new int[]{1, 3, 2}, 3);
    }

    @Test
    void chainOfMergesAndSeparations() {
        check(100, new int[]{0, 10, 20, 30, 40}, new int[]{10, 9, 1, 8, 7}, 3);
    }

    @Test
    void everyCarReachesTheLineTogether() {
        check(12, new int[]{0, 4, 8}, new int[]{3, 2, 1}, 1);
    }

    @Test
    void slowCarGathersEveryoneBehindIt() {
        check(1000, new int[]{999, 0, 100, 500}, new int[]{1, 100, 50, 1}, 2);
    }

    @Test
    void tieWithLargeValuesMerges() {
        check(1_000_000_000, new int[]{0, 1}, new int[]{1_000_000_000, 999_999_999}, 1);
    }

    @Test
    void closeArrivalTimesCompareExactly() {
        check(1_000_000_000, new int[]{0, 1}, new int[]{999_999_999, 999_999_998}, 1);
    }

    private static void check(int target, int[] positions, int[] speeds, int expected) {
        int[] originalPositions = positions.clone();
        int[] originalSpeeds = speeds.clone();
        String call = "countFleets(%d, %s, %s)".formatted(target, Arrays.toString(originalPositions), Arrays.toString(originalSpeeds));
        assertThat(Solution.countFleets(target, positions, speeds)).as(call).isEqualTo(expected);
        assertThat(positions).as(call + " must not change positions").containsExactly(originalPositions);
        assertThat(speeds).as(call + " must not change speeds").containsExactly(originalSpeeds);
    }
}
