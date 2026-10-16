package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;

class CorrectnessTest {
    @Test
    void smallestPossibleArray() {
        assertDuplicate(1, 1, 1);
    }

    @Test
    void duplicateOfTheLargestValue() {
        assertDuplicate(2, 1, 2, 2);
        assertDuplicate(5, 5, 3, 5, 1, 2, 4);
    }

    @Test
    void valueRepeatedManyTimes() {
        assertDuplicate(4, 4, 3, 4, 4, 1, 4);
    }

    @Test
    void duplicateAdjacentAtTheEnd() {
        assertDuplicate(6, 1, 2, 3, 4, 5, 6, 6);
    }

    @Test
    void longChainWithLateDuplicate() {
        int n = 5000;
        int[] nums = new int[n + 1];
        for (int i = 0; i < n; i++) {
            nums[i] = i + 1;
        }
        nums[n] = n / 2;
        assertDuplicate(n / 2, nums);
    }

    @Test
    void shuffledValuesWithOneExtraCopy() {
        Random random = new Random(5);
        for (int round = 0; round < 30; round++) {
            int n = 2 + random.nextInt(300);
            int[] nums = new int[n + 1];
            for (int i = 0; i < n; i++) {
                nums[i] = i + 1;
            }
            for (int i = n - 1; i > 0; i--) {
                int j = random.nextInt(i + 1);
                int tmp = nums[i];
                nums[i] = nums[j];
                nums[j] = tmp;
            }
            int duplicate = 1 + random.nextInt(n);
            nums[n] = duplicate;
            int slot = random.nextInt(n + 1);
            int swap = nums[slot];
            nums[slot] = nums[n];
            nums[n] = swap;
            assertDuplicate(duplicate, nums);
        }
    }

    @Test
    void severalValuesMissingWhenOneIsRepeatedOften() {
        assertDuplicate(7, 7, 7, 7, 7, 7, 7, 7, 7);
    }

    private static void assertDuplicate(int expected, int... nums) {
        int[] before = nums.clone();
        assertThat(Solution.findDuplicate(nums)).as("findDuplicate(%s)", Arrays.toString(before))
                .isEqualTo(expected);
        assertThat(nums).as("the input array must not be modified").containsExactly(before);
    }
}
