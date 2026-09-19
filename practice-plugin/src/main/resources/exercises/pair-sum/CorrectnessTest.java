package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CorrectnessTest {
    @Test
    void handlesEmptyAndSingletonInputs() {
        assertThat(Solution.findPair(new int[0], 0)).isEmpty();
        assertThat(Solution.findPair(new int[]{4}, 8)).isEmpty();
    }

    @Test
    void handlesNegativeAndZeroValues() {
        int[] numbers = {-8, 0, 3, 8};
        assertValid(numbers, 0, Solution.findPair(numbers, 0));
    }

    @Test
    void usesLongArithmeticAtIntegerBoundaries() {
        int[] numbers = {Integer.MAX_VALUE, Integer.MAX_VALUE, -1, -2};
        assertThat(Solution.findPair(numbers, -2)).isEmpty();
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
        assertThat(numbers).containsExactly(before);
    }

    @Test
    void rejectsAFalseOverflowPair() {
        int[] numbers = {Integer.MAX_VALUE, Integer.MAX_VALUE};
        assertThat(Solution.findPair(numbers, -2)).isEmpty();
    }

    @Test
    void findsPairAtTheArrayBoundaries() {
        int[] numbers = {12, 5, 0, -7};
        assertValid(numbers, 5, Solution.findPair(numbers, 5));
    }

    @Test
    void returnsEmptyWhenNoValuesCanMatch() {
        assertThat(Solution.findPair(new int[]{-10, -4, 2, 9}, 100)).isEmpty();
    }

    private static void assertValid(int[] numbers, int target, int[] result) {
        assertThat(result).isNotNull();
        if (result.length == 0) {
            return;
        }
        assertThat(result).hasSize(2);
        assertThat(result[0]).isBetween(0, numbers.length - 1);
        assertThat(result[1]).isBetween(0, numbers.length - 1);
        assertThat(result[0]).isNotEqualTo(result[1]);
        assertThat((long) numbers[result[0]] + numbers[result[1]]).isEqualTo(target);
    }
}
