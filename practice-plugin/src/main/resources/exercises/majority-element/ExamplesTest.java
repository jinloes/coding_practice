package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ExamplesTest {
    @Test
    void findsTheValueThatAppearsTwiceInThree() {
        assertThat(Solution.majorityElement(new int[]{3, 2, 3})).as("majorityElement([3, 2, 3])").isEqualTo(3);
    }

    @Test
    void findsTheMajorityAcrossInterleavedRuns() {
        assertThat(Solution.majorityElement(new int[]{2, 2, 1, 1, 1, 2, 2}))
                .as("majorityElement([2, 2, 1, 1, 1, 2, 2])")
                .isEqualTo(2);
    }

    @Test
    void returnsTheOnlyValue() {
        assertThat(Solution.majorityElement(new int[]{7})).as("majorityElement([7])").isEqualTo(7);
    }
}
