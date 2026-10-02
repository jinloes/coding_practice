package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class CorrectnessTest {
    @Test
    void handlesSingleValue() {
        check(new int[]{42}, 1);
    }

    @Test
    void handlesNegativesAcrossZero() {
        check(new int[]{-3, -2, -1, 0, 1}, 5);
    }

    @Test
    void ignoresDuplicatesInsideARun() {
        check(new int[]{1, 2, 2, 3, 3, 4}, 4);
    }

    @Test
    void findsRunsRegardlessOfOrder() {
        check(new int[]{9, 3, 7, 5, 4, 8, 6, 100}, 7);
    }

    @Test
    void picksTheLongerOfTwoRuns() {
        check(new int[]{1, 2, 3, 10, 11, 12, 13, 50}, 4);
    }

    @Test
    void handlesNoConsecutiveValues() {
        check(new int[]{10, 30, 50, 70}, 1);
    }

    @Test
    void treatsIntegerExtremesAsNotConsecutive() {
        check(new int[]{Integer.MIN_VALUE, Integer.MAX_VALUE}, 1);
    }

    @Test
    void findsRunsAtIntegerBoundaries() {
        check(new int[]{Integer.MAX_VALUE, Integer.MIN_VALUE, Integer.MAX_VALUE - 1}, 2);
        check(new int[]{Integer.MIN_VALUE + 1, Integer.MIN_VALUE, Integer.MAX_VALUE}, 2);
    }

    @Test
    void handlesLongShuffledRun() {
        int[] numbers = new int[1000];
        for (int i = 0; i < numbers.length; i++) {
            numbers[i] = (i * 389) % 1000 - 500;
        }
        check(numbers, 1000);
    }

    @Test
    void doesNotMutateTheInput() {
        int[] numbers = {4, 2, 3, 9};
        int[] before = numbers.clone();
        Solution.longestRun(numbers);
        assertThat(numbers).as("longestRun must not mutate %s", Arrays.toString(before)).containsExactly(before);
    }

    private static void check(int[] numbers, int expected) {
        String call = "longestRun(%s)".formatted(Arrays.toString(numbers));
        assertThat(Solution.longestRun(numbers)).as(call).isEqualTo(expected);
    }
}
