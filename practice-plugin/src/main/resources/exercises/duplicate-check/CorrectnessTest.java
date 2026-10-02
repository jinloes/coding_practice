package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class CorrectnessTest {
    @Test
    void acceptsSingleValue() {
        check(new int[]{7}, false);
    }

    @Test
    void findsRepeatAtTheArrayBoundaries() {
        check(new int[]{5, 1, 2, 3, 5}, true);
    }

    @Test
    void findsAdjacentRepeat() {
        check(new int[]{1, 2, 2, 3}, true);
    }

    @Test
    void handlesNegativesAndZero() {
        check(new int[]{-3, 0, 4, 0}, true);
        check(new int[]{-3, 0, 4, -4}, false);
    }

    @Test
    void handlesIntegerExtremes() {
        check(new int[]{Integer.MIN_VALUE, Integer.MAX_VALUE, 0, -1}, false);
        check(new int[]{Integer.MIN_VALUE, 5, Integer.MIN_VALUE}, true);
    }

    @Test
    void findsRepeatThatIsNotNextToItsTwin() {
        check(new int[]{8, 1, 6, 2, 9, 3, 8}, true);
    }

    @Test
    void doesNotMutateTheInput() {
        int[] numbers = {9, 2, 9, 4};
        int[] before = numbers.clone();
        Solution.hasDuplicate(numbers);
        assertThat(numbers).as("hasDuplicate must not mutate %s", Arrays.toString(before)).containsExactly(before);
    }

    @Test
    void handlesLargeDistinctInput() {
        int[] numbers = new int[5000];
        for (int i = 0; i < numbers.length; i++) {
            numbers[i] = i * 7 - 9000;
        }
        check(numbers, false);
        numbers[4999] = numbers[0];
        check(numbers, true);
    }

    private static void check(int[] numbers, boolean expected) {
        String call = "hasDuplicate(%s)".formatted(Arrays.toString(numbers));
        assertThat(Solution.hasDuplicate(numbers)).as(call).isEqualTo(expected);
    }
}
