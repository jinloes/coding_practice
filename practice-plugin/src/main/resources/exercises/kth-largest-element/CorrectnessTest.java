package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CorrectnessTest {
    @Test
    void kOneIsTheMaximum() {
        assertThat(Solution.findKthLargest(new int[]{3, 17, -4, 12}, 1)).as("findKthLargest of [3, 17, -4, 12] with k=1").isEqualTo(17);
    }

    @Test
    void kEqualsLengthIsTheMinimum() {
        assertThat(Solution.findKthLargest(new int[]{3, 17, -4, 12}, 4)).as("findKthLargest of [3, 17, -4, 12] with k=4").isEqualTo(-4);
    }

    @Test
    void sortedAscendingInput() {
        assertThat(Solution.findKthLargest(new int[]{1, 2, 3, 4, 5, 6}, 2)).as("findKthLargest of ascending [1..6] with k=2").isEqualTo(5);
    }

    @Test
    void sortedDescendingInput() {
        assertThat(Solution.findKthLargest(new int[]{6, 5, 4, 3, 2, 1}, 5)).as("findKthLargest of descending [6..1] with k=5").isEqualTo(2);
    }

    @Test
    void duplicatesAtTheTop() {
        assertThat(Solution.findKthLargest(new int[]{9, 9, 8, 8}, 2)).as("findKthLargest of [9, 9, 8, 8] with k=2").isEqualTo(9);
    }

    @Test
    void extremeValues() {
        assertThat(Solution.findKthLargest(new int[]{2147483647, -2147483648, 0}, 2)).as("findKthLargest with Integer extremes and k=2").isEqualTo(0);
    }

    @Test
    void leavesTheInputUntouched() {
        int[] nums = {4, 1, 9, 6};
        Solution.findKthLargest(nums, 2);
        assertThat(nums).as("input after findKthLargest").containsExactly(4, 1, 9, 6);
    }

    @Test
    void rejectsKOutsideTheArray() {
        assertThatThrownBy(() -> Solution.findKthLargest(new int[]{1, 2}, 0)).as("k = 0").isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> Solution.findKthLargest(new int[]{1, 2}, 3)).as("k above length").isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> Solution.findKthLargest(new int[0], 1)).as("empty array").isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void matchesSortingOnPseudoRandomArrays() {
        java.util.Random random = new java.util.Random(3);
        for (int round = 0; round < 100; round++) {
            int[] nums = random.ints(1 + random.nextInt(30), -15, 15).toArray();
            int k = 1 + random.nextInt(nums.length);
            int[] sorted = nums.clone();
            java.util.Arrays.sort(sorted);
            assertThat(Solution.findKthLargest(nums, k)).as("k=" + k + " of " + java.util.Arrays.toString(nums)).isEqualTo(sorted[sorted.length - k]);
        }
    }
}
