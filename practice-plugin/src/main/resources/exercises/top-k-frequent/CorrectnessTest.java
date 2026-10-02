package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

class CorrectnessTest {
    @Test
    void returnsTheOnlyValueWhenAllValuesAreEqual() {
        assertThat(top(new int[]{3, 3, 3, 3}, 1)).as("topKFrequent([3, 3, 3, 3], 1)").containsExactly(3);
    }

    @Test
    void returnsEveryDistinctValueWhenKEqualsTheDistinctCount() {
        assertThat(top(new int[]{9, 8, 7}, 3)).as("topKFrequent([9, 8, 7], 3)").containsExactlyInAnyOrder(7, 8, 9);
        assertThat(top(new int[]{2, 2, 6, 6, 6, 1}, 3)).as("topKFrequent([2, 2, 6, 6, 6, 1], 3)")
                .containsExactlyInAnyOrder(1, 2, 6);
    }

    @Test
    void prefersFrequencyOverValue() {
        assertThat(top(new int[]{100, 1, 1, 1, 100, 2, 3, 4}, 2)).as("topKFrequent([100, 1, 1, 1, 100, 2, 3, 4], 2)")
                .containsExactlyInAnyOrder(1, 100);
        assertThat(top(new int[]{-5, -5, -5, 9, 9, 10, 11}, 1)).as("topKFrequent([-5, -5, -5, 9, 9, 10, 11], 1)")
                .containsExactly(-5);
    }

    @Test
    void worksWhenTheMostFrequentValueAppearsLast() {
        assertThat(top(new int[]{1, 2, 3, 4, 4, 4, 4}, 1)).as("topKFrequent([1, 2, 3, 4, 4, 4, 4], 1)")
                .containsExactly(4);
    }

    @Test
    void handlesExtremeValues() {
        int[] numbers = {Integer.MIN_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, 0, Integer.MIN_VALUE, Integer.MAX_VALUE};
        assertThat(top(numbers, 2)).as("topKFrequent with MIN_VALUE twice, MAX_VALUE three times and 0 once")
                .containsExactlyInAnyOrder(Integer.MAX_VALUE, Integer.MIN_VALUE);
    }

    @Test
    void returnsExactlyKDistinctValues() {
        int[] result = top(new int[]{1, 1, 1, 1, 2, 2, 2, 3, 3, 4}, 3);
        assertThat(result).as("topKFrequent([1, 1, 1, 1, 2, 2, 2, 3, 3, 4], 3)")
                .hasSize(3).doesNotHaveDuplicates().containsExactlyInAnyOrder(1, 2, 3);
    }

    @Test
    void doesNotChangeTheInputArray() {
        int[] numbers = {5, 3, 5, 3, 5, 8};
        int[] copy = numbers.clone();
        top(numbers, 2);
        assertThat(numbers).as("input array after topKFrequent").containsExactly(copy);
    }

    @Test
    void handlesManyDistinctValuesWithOneClearWinner() {
        int[] numbers = new int[50000];
        for (int i = 0; i < numbers.length; i++) {
            numbers[i] = i % 2 == 0 ? -7 : i;
        }
        assertThat(top(numbers, 1)).as("topKFrequent where -7 fills every even index of 50000 values")
                .containsExactly(-7);
    }

    @Test
    void ranksLargeCounts() {
        int[] numbers = new int[60000];
        for (int i = 0; i < numbers.length; i++) {
            numbers[i] = i % 6 == 0 ? 10 : (i % 6 < 3 ? 20 : i);
        }
        assertThat(top(numbers, 2)).as("topKFrequent of 60000 values with 10 and 20 repeated most")
                .containsExactlyInAnyOrder(10, 20);
    }

    private static int[] top(int[] numbers, int k) {
        try {
            return Solution.topKFrequent(numbers, k);
        } catch (Throwable thrown) {
            return fail("topKFrequent(%s, %d) threw %s".formatted(
                    numbers.length > 20 ? "an array of " + numbers.length + " values" : Arrays.toString(numbers),
                    k, thrown), thrown);
        }
    }
}
