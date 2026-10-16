package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ExamplesTest {
    @Test
    void countsSingleMinusPlacements() {
        assertThat(Solution.countAssignments(new int[]{1, 1, 1, 1, 1}, 3)).as("countAssignments([1, 1, 1, 1, 1], 3)").isEqualTo(5);
    }

    @Test
    void unreachableTargetGivesZero() {
        assertThat(Solution.countAssignments(new int[]{2, 3}, 7)).as("countAssignments([2, 3], 7)").isEqualTo(0);
    }

    @Test
    void zerosDoubleTheCount() {
        assertThat(Solution.countAssignments(new int[]{0, 0, 1}, 1)).as("countAssignments([0, 0, 1], 1)").isEqualTo(4);
    }
}
