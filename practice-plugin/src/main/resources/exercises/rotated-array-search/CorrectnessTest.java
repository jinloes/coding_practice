package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class CorrectnessTest {
    @Test
    void handlesEmptyArray() {
        check(new int[0], 3, -1);
    }

    @Test
    void findsTargetBeforeThePivot() {
        check(new int[]{6, 7, 8, 1, 2, 3, 4}, 7, 1);
    }

    @Test
    void findsTargetsAtThePivotBoundary() {
        check(new int[]{6, 7, 8, 1, 2, 3, 4}, 8, 2);
        check(new int[]{6, 7, 8, 1, 2, 3, 4}, 1, 3);
    }

    @Test
    void findsTargetsAtBothEnds() {
        check(new int[]{6, 7, 8, 1, 2, 3, 4}, 6, 0);
        check(new int[]{6, 7, 8, 1, 2, 3, 4}, 4, 6);
    }

    @Test
    void handlesTwoElements() {
        check(new int[]{3, 1}, 1, 1);
        check(new int[]{3, 1}, 3, 0);
        check(new int[]{3, 1}, 2, -1);
    }

    @Test
    void handlesUnrotatedArray() {
        check(new int[]{1, 2, 3, 4, 5}, 4, 3);
        check(new int[]{1, 2, 3, 4, 5}, 0, -1);
        check(new int[]{1, 2, 3, 4, 5}, 10, -1);
    }

    @Test
    void rejectsValuesOutsideTheRange() {
        check(new int[]{6, 7, 8, 1, 2, 3, 4}, 0, -1);
        check(new int[]{6, 7, 8, 1, 2, 3, 4}, 9, -1);
    }

    @Test
    void handlesIntegerExtremes() {
        check(new int[]{Integer.MAX_VALUE, Integer.MIN_VALUE, 0}, Integer.MIN_VALUE, 1);
        check(new int[]{Integer.MAX_VALUE, Integer.MIN_VALUE, 0}, Integer.MAX_VALUE, 0);
    }

    @Test
    void findsEveryValueInEveryRotation() {
        int[] sorted = new int[16];
        for (int i = 0; i < sorted.length; i++) {
            sorted[i] = i * 2 + 1;
        }
        for (int shift = 0; shift < sorted.length; shift++) {
            int[] rotated = new int[sorted.length];
            for (int j = 0; j < sorted.length; j++) {
                rotated[j] = sorted[(j + shift) % sorted.length];
            }
            for (int i = 0; i < sorted.length; i++) {
                check(rotated, sorted[i], (i - shift + sorted.length) % sorted.length);
                check(rotated, sorted[i] + 1, -1);
            }
        }
    }

    @Test
    void doesNotMutateTheInput() {
        int[] rotated = {6, 7, 1, 2, 3};
        int[] before = rotated.clone();
        Solution.search(rotated, 2);
        assertThat(rotated).as("search must not mutate %s", Arrays.toString(before)).containsExactly(before);
    }

    private static void check(int[] rotated, int target, int expected) {
        String call = "search(%s, %d)".formatted(Arrays.toString(rotated), target);
        assertThat(Solution.search(rotated, target)).as(call).isEqualTo(expected);
    }
}
