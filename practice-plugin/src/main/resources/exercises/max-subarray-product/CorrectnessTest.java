package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class CorrectnessTest {
    @Test
    void findsBestSliceAfterDroppingAnEdge() {
        check(new int[]{2, -5, -2, -4, 3}, 24);
    }

    @Test
    void handlesAllZeros() {
        check(new int[]{0, 0}, 0);
    }

    @Test
    void handlesAllNegativesWithOddCount() {
        check(new int[]{-2, -3, -4}, 12);
    }

    @Test
    void handlesAllPositive() {
        check(new int[]{2, 3, 4}, 24);
    }

    @Test
    void splitsAtAZero() {
        check(new int[]{3, 0, 4}, 4);
    }

    @Test
    void prefersZeroOverANegativeSingleton() {
        check(new int[]{-2, 0, -1}, 0);
    }

    @Test
    void handlesOnesAndNegativeOnes() {
        check(new int[]{-1, -1, -1}, 1);
    }

    @Test
    void exceedsIntRange() {
        check(new int[]{Integer.MAX_VALUE, Integer.MAX_VALUE}, 4611686014132420609L);
        check(new int[]{Integer.MIN_VALUE, Integer.MIN_VALUE}, 4611686018427387904L);
    }

    @Test
    void doesNotMutateTheInput() {
        int[] numbers = {2, -1, 3};
        int[] before = numbers.clone();
        Solution.maxSubarrayProduct(numbers);
        assertThat(numbers).as("maxSubarrayProduct must not mutate %s", Arrays.toString(before)).containsExactly(before);
    }

    private static void check(int[] numbers, long expected) {
        String call = "maxSubarrayProduct(%s)".formatted(Arrays.toString(numbers));
        assertThat(Solution.maxSubarrayProduct(numbers)).as(call).isEqualTo(expected);
    }
}
