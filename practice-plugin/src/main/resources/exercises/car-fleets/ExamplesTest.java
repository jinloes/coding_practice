package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;

class ExamplesTest {
    @Test
    void mergingCarsFormOneFleet() {
        check(100, new int[]{10, 40, 70}, new int[]{5, 3, 1}, 1);
    }

    @Test
    void fasterCarsAheadStaySeparate() {
        check(50, new int[]{0, 10, 20}, new int[]{1, 2, 4}, 3);
    }

    @Test
    void singleCarIsOneFleet() {
        check(12, new int[]{3}, new int[]{2}, 1);
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
