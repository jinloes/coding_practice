package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

class ExamplesTest {
    @Test
    void movesZeroesBehindTheValuesThatKeepTheirOrder() {
        int[] numbers = {0, 1, 0, 3, 12};
        int[] before = numbers.clone();
        Solution.moveZeroes(numbers);
        assertThat(numbers).as("moveZeroes(%s)", Arrays.toString(before))
                .containsExactly(1, 3, 12, 0, 0);
    }

    @Test
    void leavesAnAllZeroArrayAlone() {
        int[] numbers = {0, 0};
        Solution.moveZeroes(numbers);
        assertThat(numbers).as("moveZeroes([0, 0])").containsExactly(0, 0);
    }

    @Test
    void leavesAnArrayWithoutZeroesAlone() {
        int[] numbers = {1, 2};
        Solution.moveZeroes(numbers);
        assertThat(numbers).as("moveZeroes([1, 2])").containsExactly(1, 2);
    }
}
