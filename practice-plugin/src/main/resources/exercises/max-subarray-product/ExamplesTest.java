package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class ExamplesTest {
    @Test
    void usesTwoNegativesToFlipTheSign() {
        check(new int[]{1, -2, 3, -4}, 24);
    }

    @Test
    void resetsAtZero() {
        check(new int[]{-3, 0, -2}, 0);
    }

    @Test
    void handlesSingleNegative() {
        check(new int[]{-6}, -6);
    }

    private static void check(int[] numbers, long expected) {
        String call = "maxSubarrayProduct(%s)".formatted(Arrays.toString(numbers));
        assertThat(Solution.maxSubarrayProduct(numbers)).as(call).isEqualTo(expected);
    }
}
