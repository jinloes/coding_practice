package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class ExamplesTest {
    @Test
    void findsTheWrapPoint() {
        check(new int[]{5, 6, 7, 1, 2, 3}, 1);
    }

    @Test
    void handlesUnrotatedArray() {
        check(new int[]{1, 2, 3}, 1);
    }

    @Test
    void handlesSingleElement() {
        check(new int[]{9}, 9);
    }

    private static void check(int[] rotated, int expected) {
        String call = "findMinimum(%s)".formatted(Arrays.toString(rotated));
        assertThat(Solution.findMinimum(rotated)).as(call).isEqualTo(expected);
    }
}
