package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

class CorrectnessTest {
    @Test
    void handlesEmptyAndSingletonInputs() {
        assertEmpty(new int[0], 0);
        assertEmpty(new int[]{4}, 8);
    }

    @Test
    void handlesNegativeAndZeroValues() {
        int[] numbers = {-8, 0, 3, 8};
        assertValid(numbers, 0, Solution.findPair(numbers, 0));
    }

    @Test
    void usesLongArithmeticAtIntegerBoundaries() {
        int[] numbers = {Integer.MAX_VALUE, Integer.MAX_VALUE, -1, -2};
        assertEmpty(numbers, -2);
        assertValid(numbers, -3, Solution.findPair(numbers, -3));
    }

    @Test
    void acceptsAnyValidPairWhenManyPairsExist() {
        int[] numbers = {1, 4, 1, 4, 1};
        assertValid(numbers, 5, Solution.findPair(numbers, 5));
    }

    @Test
    void doesNotMutateTheInput() {
        int[] numbers = {9, -2, 6, 11, -5};
        int[] before = numbers.clone();
        Solution.findPair(numbers, 4);
        assertThat(numbers).as("findPair must not mutate %s", Arrays.toString(before))
                .containsExactly(before);
    }

    @Test
    void rejectsAFalseOverflowPair() {
        assertEmpty(new int[]{Integer.MAX_VALUE, Integer.MAX_VALUE}, -2);
    }

    @Test
    void findsPairAtTheArrayBoundaries() {
        int[] numbers = {12, 5, 0, -7};
        assertValid(numbers, 5, Solution.findPair(numbers, 5));
    }

    @Test
    void returnsEmptyWhenNoValuesCanMatch() {
        assertEmpty(new int[]{-10, -4, 2, 9}, 100);
    }

    private static void assertEmpty(int[] numbers, int target) {
        assertThat(Solution.findPair(numbers, target))
                .as("%s must report that no pair exists", call(numbers, target))
                .isEmpty();
    }

    private static void assertValid(int[] numbers, int target, int[] result) {
        String call = call(numbers, target);
        assertThat(result).as("%s must not return null", call).isNotNull();
        if (result.length == 0) {
            return;
        }
        assertThat(result).as("%s returned %s", call, Arrays.toString(result)).hasSize(2);
        assertThat(result[0]).as("%s returned first index %d", call, result[0])
                .isBetween(0, numbers.length - 1);
        assertThat(result[1]).as("%s returned second index %d", call, result[1])
                .isBetween(0, numbers.length - 1)
                .isNotEqualTo(result[0]);
        assertThat((long) numbers[result[0]] + numbers[result[1]])
                .as("%s returned indices %s selecting values %d and %d", call,
                        Arrays.toString(result), numbers[result[0]], numbers[result[1]])
                .isEqualTo(target);
    }

    private static String call(int[] numbers, int target) {
        return "findPair(%s, %d)".formatted(Arrays.toString(numbers), target);
    }
}
