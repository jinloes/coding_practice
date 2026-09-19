package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ExamplesTest {
    @Test
    void findsTypicalPair() {
        assertValid(new int[]{2, 7, 11, 15}, 9, Solution.findPair(new int[]{2, 7, 11, 15}, 9));
    }

    @Test
    void findsPairOfDuplicates() {
        assertValid(new int[]{3, 3}, 6, Solution.findPair(new int[]{3, 3}, 6));
    }

    @Test
    void reportsMissingPair() {
        assertThat(Solution.findPair(new int[]{1, 2, 3}, 7)).isEmpty();
    }

    private static void assertValid(int[] numbers, int target, int[] result) {
        assertThat(result).hasSize(2);
        assertThat(result[0]).isBetween(0, numbers.length - 1);
        assertThat(result[1]).isBetween(0, numbers.length - 1);
        assertThat(result[0]).isNotEqualTo(result[1]);
        assertThat((long) numbers[result[0]] + numbers[result[1]]).isEqualTo(target);
    }
}
