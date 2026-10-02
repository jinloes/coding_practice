package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class ExamplesTest {
    @Test
    void findsBestMiddleSlice() {
        check(new int[]{3, -5, 4, 2, -1, 6, -8}, 11);
    }

    @Test
    void returnsLargestNegativeWhenAllNegative() {
        check(new int[]{-7, -3, -9}, -3);
    }

    @Test
    void handlesSingleElement() {
        check(new int[]{5}, 5);
    }

    private static void check(int[] numbers, long expected) {
        String call = "maxSubarraySum(%s)".formatted(Arrays.toString(numbers));
        assertThat(Solution.maxSubarraySum(numbers)).as(call).isEqualTo(expected);
    }
}
