package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class ExamplesTest {
    @Test
    void findsTargetAfterThePivot() {
        check(new int[]{6, 7, 8, 1, 2, 3, 4}, 2, 4);
    }

    @Test
    void reportsMissingTarget() {
        check(new int[]{6, 7, 8, 1, 2, 3, 4}, 5, -1);
    }

    @Test
    void handlesSingleElement() {
        check(new int[]{1}, 1, 0);
    }

    private static void check(int[] rotated, int target, int expected) {
        String call = "search(%s, %d)".formatted(Arrays.toString(rotated), target);
        assertThat(Solution.search(rotated, target)).as(call).isEqualTo(expected);
    }
}
