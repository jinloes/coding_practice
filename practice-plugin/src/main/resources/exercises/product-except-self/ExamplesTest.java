package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class ExamplesTest {
    @Test
    void multipliesTheOtherValues() {
        check(new int[]{2, 3, 4}, new int[]{12, 8, 6});
    }

    @Test
    void handlesASingleZero() {
        check(new int[]{1, 0, 3}, new int[]{0, 3, 0});
    }

    @Test
    void returnsOneForASingleValue() {
        check(new int[]{5}, new int[]{1});
    }

    private static void check(int[] numbers, int[] expected) {
        String call = "productExceptSelf(%s)".formatted(Arrays.toString(numbers));
        int[] actual = Solution.productExceptSelf(numbers);
        assertThat(actual).as(call).isNotNull().containsExactly(expected);
    }
}
