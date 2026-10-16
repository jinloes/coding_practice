package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ExamplesTest {
    @Test
    void splitsIntoTwoEqualGroups() {
        assertThat(Solution.canSplitEvenly(new int[]{3, 1, 1, 2, 1})).as("canSplitEvenly([3, 1, 1, 2, 1])").isEqualTo(true);
    }

    @Test
    void oddTotalCannotSplit() {
        assertThat(Solution.canSplitEvenly(new int[]{2, 5, 4})).as("canSplitEvenly([2, 5, 4])").isEqualTo(false);
    }

    @Test
    void twoEqualValuesSplit() {
        assertThat(Solution.canSplitEvenly(new int[]{6, 6})).as("canSplitEvenly([6, 6])").isEqualTo(true);
    }
}
