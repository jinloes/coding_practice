package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class ExamplesTest {
    @Test
    void findsTheLongestRun() {
        check(new int[]{31, 8, 9, 30, 10, 32, 7}, 4);
    }

    @Test
    void countsRepeatedValuesOnce() {
        check(new int[]{5, 5, 5}, 1);
    }

    @Test
    void returnsZeroForEmptyInput() {
        check(new int[0], 0);
    }

    private static void check(int[] numbers, int expected) {
        String call = "longestRun(%s)".formatted(Arrays.toString(numbers));
        assertThat(Solution.longestRun(numbers)).as(call).isEqualTo(expected);
    }
}
