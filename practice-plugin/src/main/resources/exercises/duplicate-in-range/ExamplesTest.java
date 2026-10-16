package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;

class ExamplesTest {
    @Test
    void duplicateAppearsTwice() {
        assertDuplicate(2, 1, 3, 4, 2, 2);
    }

    @Test
    void duplicateStartsTheArray() {
        assertDuplicate(3, 3, 1, 3, 4, 2);
    }

    @Test
    void everyEntryIsTheSameValue() {
        assertDuplicate(2, 2, 2, 2, 2, 2);
    }

    private static void assertDuplicate(int expected, int... nums) {
        int[] before = nums.clone();
        assertThat(Solution.findDuplicate(nums)).as("findDuplicate(%s)", Arrays.toString(before))
                .isEqualTo(expected);
        assertThat(nums).as("the input array must not be modified").containsExactly(before);
    }
}
