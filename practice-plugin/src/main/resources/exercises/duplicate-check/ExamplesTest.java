package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class ExamplesTest {
    @Test
    void findsRepeatedValue() {
        check(new int[]{4, 9, 1, 4}, true);
    }

    @Test
    void acceptsDistinctValues() {
        check(new int[]{3, 8, 5}, false);
    }

    @Test
    void acceptsEmptyArray() {
        check(new int[0], false);
    }

    private static void check(int[] numbers, boolean expected) {
        String call = "hasDuplicate(%s)".formatted(Arrays.toString(numbers));
        assertThat(Solution.hasDuplicate(numbers)).as(call).isEqualTo(expected);
    }
}
