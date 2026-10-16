package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CorrectnessTest {
    @Test
    void emptyArrayIsSplittable() {
        assertThat(Solution.canSplitEvenly(new int[]{})).as("canSplitEvenly([])").isEqualTo(true);
    }

    @Test
    void singleValueCannotSplit() {
        assertThat(Solution.canSplitEvenly(new int[]{7})).as("canSplitEvenly([7])").isEqualTo(false);
    }

    @Test
    void oddTotalCannotSplit() {
        assertThat(Solution.canSplitEvenly(new int[]{1, 2, 4})).as("canSplitEvenly([1, 2, 4])").isEqualTo(false);
    }

    @Test
    void evenTotalButNoSplit() {
        assertThat(Solution.canSplitEvenly(new int[]{1, 1, 8})).as("canSplitEvenly([1, 1, 8])").isEqualTo(false);
    }

    @Test
    void needsSeveralValuesOnOneSide() {
        assertThat(Solution.canSplitEvenly(new int[]{9, 4, 3, 2})).as("canSplitEvenly([9, 4, 3, 2])").isEqualTo(true);
    }

    @Test
    void manyEqualValues() {
        assertThat(Solution.canSplitEvenly(new int[]{5, 5, 5, 5, 5, 5, 5, 5, 5, 5})).as("canSplitEvenly([5, 5, 5, 5, 5, 5, 5, 5, 5, 5])").isEqualTo(true);
    }

    @Test
    void mixedLargerInput() {
        assertThat(Solution.canSplitEvenly(new int[]{8, 12, 13, 5, 7, 2, 3, 5, 8, 17, 7, 13, 1, 15})).as("canSplitEvenly([8, 12, 13, 5, 7, 2, 3, 5, 8, 17, 7, 13, 1, 15])").isEqualTo(true);
    }

    @Test
    void largeValueDominates() {
        assertThat(Solution.canSplitEvenly(new int[]{100, 1, 2, 3, 4, 5, 6, 79})).as("canSplitEvenly([100, 1, 2, 3, 4, 5, 6, 79])").isEqualTo(true);
    }

    @Test
    void twoEqualOnly() {
        assertThat(Solution.canSplitEvenly(new int[]{4, 4})).as("canSplitEvenly([4, 4])").isEqualTo(true);
    }
}
