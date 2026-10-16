package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CorrectnessTest {
    @Test
    void loneValueFirst() {
        assertThat(Solution.findUnpaired(new int[]{8, 2, 2, 6, 6})).as("findUnpaired of [8, 2, 2, 6, 6]").isEqualTo(8);
    }

    @Test
    void loneValueLast() {
        assertThat(Solution.findUnpaired(new int[]{2, 6, 2, 6, 8})).as("findUnpaired of [2, 6, 2, 6, 8]").isEqualTo(8);
    }

    @Test
    void loneValueSitsBetweenSmallerAndLarger() {
        assertThat(Solution.findUnpaired(new int[]{1, 5, 1, 9, 9})).as("findUnpaired of [1, 5, 1, 9, 9]").isEqualTo(5);
    }

    @Test
    void zeroCanBeTheLoneValue() {
        assertThat(Solution.findUnpaired(new int[]{3, 0, 3})).as("findUnpaired of [3, 0, 3]").isEqualTo(0);
    }

    @Test
    void extremeValuesDoNotOverflow() {
        assertThat(Solution.findUnpaired(new int[]{2147483647, -2147483648, 2147483647})).as("findUnpaired with Integer extremes").isEqualTo(-2147483648);
    }

    @Test
    void pairsAreNotAdjacent() {
        assertThat(Solution.findUnpaired(new int[]{10, 20, 30, 10, 20})).as("findUnpaired of [10, 20, 30, 10, 20]").isEqualTo(30);
    }

    @Test
    void leavesTheInputUntouched() {
        int[] nums = {7, 1, 7};
        Solution.findUnpaired(nums);
        assertThat(nums).as("input after findUnpaired").containsExactly(7, 1, 7);
    }

    @Test
    void findsTheLoneValueInShuffledLargeInput() {
        java.util.Random random = new java.util.Random(8);
        int pairs = 5000;
        int[] nums = new int[pairs * 2 + 1];
        int lone = 123456789;
        for (int i = 0; i < pairs; i++) {
            int value = random.nextInt();
            nums[2 * i] = value;
            nums[2 * i + 1] = value;
        }
        nums[nums.length - 1] = lone;
        for (int i = nums.length - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            int swap = nums[i];
            nums[i] = nums[j];
            nums[j] = swap;
        }
        assertThat(Solution.findUnpaired(nums)).as("findUnpaired of 10001 shuffled values").isEqualTo(lone);
    }
}
