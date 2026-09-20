package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

class ExamplesTest {
    @Test
    void findsTypicalPair() {
        assertValid(new int[]{2, 7, 11, 15}, 9);
    }

    @Test
    void findsPairOfDuplicates() {
        assertValid(new int[]{3, 3}, 6);
    }

    @Test
    void reportsMissingPair() {
        int[] numbers = {1, 2, 3};
        assertThat(Solution.findPair(numbers, 7))
                .as("%s must report that no pair exists", call(numbers, 7))
                .isEmpty();
    }

    private static void assertValid(int[] numbers, int target) {
        String call = call(numbers, target);
        int[] result = Solution.findPair(numbers, target);
        assertThat(result).as("%s must return exactly two indices", call).hasSize(2);
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
