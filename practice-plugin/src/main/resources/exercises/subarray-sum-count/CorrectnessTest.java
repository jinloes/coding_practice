package com.jinloes.practice;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class CorrectnessTest {
    @Test
    void handlesEmptyAndSingleElementInputs() {
        assertThat(Solution.countSubarrays(new int[0], 0)).isZero();
        assertThat(Solution.countSubarrays(new int[]{5}, 5)).isEqualTo(1);
        assertThat(Solution.countSubarrays(new int[]{5}, 2)).isZero();
    }
    @Test
    void countsOverlappingAndAllZeroSubarrays() {
        assertThat(Solution.countSubarrays(new int[]{0, 0, 0, 0}, 0)).isEqualTo(10);
        assertThat(Solution.countSubarrays(new int[]{2, -2, 2, -2}, 0)).isEqualTo(4);
    }
    @Test
    void handlesLargeMagnitudePrefixSums() {
        assertThat(Solution.countSubarrays(new int[]{Integer.MAX_VALUE, 1, -1, Integer.MIN_VALUE}, 0)).isEqualTo(1);
    }
    @Test
    void doesNotMutateInput() {
        int[] numbers = {2, -1, 2, 1};
        int[] before = numbers.clone();
        Solution.countSubarrays(numbers, 3);
        assertThat(numbers).containsExactly(before);
    }
    @Test
    void agreesWithBruteForceOnMixedValues() {
        int[] numbers = {-2, 3, 0, -1, 2, -2, 1};
        int expected = 0;
        for (int start = 0; start < numbers.length; start++) {
            int sum = 0;
            for (int end = start; end < numbers.length; end++) {
                sum += numbers[end];
                if (sum == 1) expected++;
            }
        }
        assertThat(Solution.countSubarrays(numbers, 1)).isEqualTo(expected);
    }
}
