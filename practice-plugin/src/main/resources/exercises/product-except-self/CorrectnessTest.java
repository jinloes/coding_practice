package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class CorrectnessTest {
    @Test
    void returnsEmptyForEmptyInput() {
        check(new int[0], new int[0]);
    }

    @Test
    void handlesTwoValues() {
        check(new int[]{3, -4}, new int[]{-4, 3});
    }

    @Test
    void handlesTwoZeros() {
        check(new int[]{0, 0, 4}, new int[]{0, 0, 0});
    }

    @Test
    void handlesZeroAtTheEnds() {
        check(new int[]{0, 2, 3}, new int[]{6, 0, 0});
        check(new int[]{2, 3, 0}, new int[]{0, 0, 6});
    }

    @Test
    void handlesNegativeValues() {
        check(new int[]{-1, 2, -3, 4}, new int[]{-24, 12, -8, 6});
    }

    @Test
    void wrapsOnIntOverflow() {
        check(new int[]{65536, 65536, 1}, new int[]{65536, 65536, 0});
    }

    @Test
    void handlesRepeatedValues() {
        check(new int[]{2, 2, 2, 2}, new int[]{8, 8, 8, 8});
    }

    @Test
    void doesNotMutateTheInput() {
        int[] numbers = {3, 1, 4, 1};
        int[] before = numbers.clone();
        Solution.productExceptSelf(numbers);
        assertThat(numbers).as("productExceptSelf must not mutate %s", Arrays.toString(before)).containsExactly(before);
    }

    @Test
    void handlesLongAlternatingInput() {
        int[] numbers = new int[2001];
        int[] expected = new int[2001];
        Arrays.fill(numbers, 1);
        Arrays.fill(expected, 1);
        numbers[1000] = -1;
        Arrays.fill(expected, -1);
        expected[1000] = 1;
        check(numbers, expected);
    }

    private static void check(int[] numbers, int[] expected) {
        String call = "productExceptSelf(%s)".formatted(Arrays.toString(numbers));
        int[] actual = Solution.productExceptSelf(numbers);
        assertThat(actual).as(call).isNotNull().containsExactly(expected);
    }
}
