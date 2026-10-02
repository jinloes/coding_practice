package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class CorrectnessTest {
    @Test
    void sumsEverythingWhenAllPositive() {
        check(new int[]{1, 2, 3, 4}, 10);
    }

    @Test
    void prefersAnAllZeroSliceOverNegatives() {
        check(new int[]{-1, 0, -2}, 0);
    }

    @Test
    void bridgesASmallNegative() {
        check(new int[]{4, -1, 4}, 7);
        check(new int[]{2, -3, 2}, 2);
    }

    @Test
    void findsBestSliceAtTheStart() {
        check(new int[]{5, 4, -20, 3}, 9);
    }

    @Test
    void findsBestSliceAtTheEnd() {
        check(new int[]{3, -20, 4, 5}, 9);
    }

    @Test
    void accumulatesBeyondIntRange() {
        check(new int[]{Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE}, 6442450941L);
    }

    @Test
    void handlesMinimumValues() {
        check(new int[]{Integer.MIN_VALUE, Integer.MIN_VALUE}, Integer.MIN_VALUE);
    }

    @Test
    void restartsAfterAHugeLoss() {
        check(new int[]{10, Integer.MIN_VALUE, 3, 4}, 10);
    }

    @Test
    void doesNotMutateTheInput() {
        int[] numbers = {2, -1, 3};
        int[] before = numbers.clone();
        Solution.maxSubarraySum(numbers);
        assertThat(numbers).as("maxSubarraySum must not mutate %s", Arrays.toString(before)).containsExactly(before);
    }

    private static void check(int[] numbers, long expected) {
        String call = "maxSubarraySum(%s)".formatted(Arrays.toString(numbers));
        assertThat(Solution.maxSubarraySum(numbers)).as(call).isEqualTo(expected);
    }
}
