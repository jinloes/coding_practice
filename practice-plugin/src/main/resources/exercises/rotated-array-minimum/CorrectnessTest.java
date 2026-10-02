package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class CorrectnessTest {
    @Test
    void handlesTwoElements() {
        check(new int[]{2, 1}, 1);
        check(new int[]{1, 2}, 1);
    }

    @Test
    void handlesRotationByOne() {
        check(new int[]{8, 1, 2, 3, 4, 5, 6, 7}, 1);
    }

    @Test
    void handlesRotationByLengthMinusOne() {
        check(new int[]{2, 3, 4, 5, 6, 7, 8, 1}, 1);
    }

    @Test
    void handlesNegativeValues() {
        check(new int[]{-1, 0, 3, -9, -5}, -9);
    }

    @Test
    void handlesIntegerExtremes() {
        check(new int[]{Integer.MAX_VALUE, Integer.MIN_VALUE, 0}, Integer.MIN_VALUE);
        check(new int[]{0, Integer.MAX_VALUE, Integer.MIN_VALUE}, Integer.MIN_VALUE);
    }

    @Test
    void handlesThreeElementRotations() {
        check(new int[]{1, 2, 3}, 1);
        check(new int[]{2, 3, 1}, 1);
        check(new int[]{3, 1, 2}, 1);
    }

    @Test
    void handlesEveryRotationOfALongerArray() {
        int[] sorted = new int[17];
        for (int i = 0; i < sorted.length; i++) {
            sorted[i] = i * 3 - 20;
        }
        for (int shift = 0; shift < sorted.length; shift++) {
            check(rotate(sorted, shift), sorted[0]);
        }
    }

    @Test
    void doesNotMutateTheInput() {
        int[] rotated = {6, 7, 1, 2, 3};
        int[] before = rotated.clone();
        Solution.findMinimum(rotated);
        assertThat(rotated).as("findMinimum must not mutate %s", Arrays.toString(before)).containsExactly(before);
    }

    private static void check(int[] rotated, int expected) {
        String call = "findMinimum(%s)".formatted(Arrays.toString(rotated));
        assertThat(Solution.findMinimum(rotated)).as(call).isEqualTo(expected);
    }

    private static int[] rotate(int[] sorted, int shift) {
        int[] result = new int[sorted.length];
        for (int i = 0; i < sorted.length; i++) {
            result[i] = sorted[(i + shift) % sorted.length];
        }
        return result;
    }
}
