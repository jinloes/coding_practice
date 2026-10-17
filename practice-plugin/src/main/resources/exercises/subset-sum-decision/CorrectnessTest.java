package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CorrectnessTest {
    @Test
    void coversEdgeCases() {
        assertThat(Solution.hasSubsetSum(new int[]{}, 0)).isTrue();
        assertThat(Solution.hasSubsetSum(new int[]{}, 5)).isFalse();
        assertThat(Solution.hasSubsetSum(new int[]{1, 2, 3}, -1)).isFalse();
        assertThat(Solution.hasSubsetSum(new int[]{4}, 8)).isFalse();
    }
    @Test
    void solvesLargeDeterministicCase() {
        int[] numbers = new int[80];
        for (int i=0;i<numbers.length;i++) numbers[i] = i % 7 + 1;
        assertThat(Solution.hasSubsetSum(numbers, 217)).isTrue();
        assertThat(Solution.hasSubsetSum(numbers, 1000)).isFalse();
    }
}
