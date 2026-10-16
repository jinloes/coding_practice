package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CorrectnessTest {
    @Test
    void emptyValuesZeroTarget() {
        assertThat(Solution.countAssignments(new int[]{}, 0)).as("countAssignments([], 0)").isEqualTo(1);
    }

    @Test
    void emptyValuesOtherTarget() {
        assertThat(Solution.countAssignments(new int[]{}, 4)).as("countAssignments([], 4)").isEqualTo(0);
    }

    @Test
    void negativeTargetMirrorsPositive() {
        assertThat(Solution.countAssignments(new int[]{1, 2, 3, 4}, -2)).as("countAssignments([1, 2, 3, 4], -2)").isEqualTo(2);
    }

    @Test
    void zerosDoubleTheCount() {
        assertThat(Solution.countAssignments(new int[]{0, 0, 0, 5}, 5)).as("countAssignments([0, 0, 0, 5], 5)").isEqualTo(8);
    }

    @Test
    void parityMismatchGivesZero() {
        assertThat(Solution.countAssignments(new int[]{2, 4, 6}, 3)).as("countAssignments([2, 4, 6], 3)").isEqualTo(0);
    }

    @Test
    void targetBeyondTotal() {
        assertThat(Solution.countAssignments(new int[]{1, 2, 3}, 10)).as("countAssignments([1, 2, 3], 10)").isEqualTo(0);
    }

    @Test
    void allValuesNeeded() {
        assertThat(Solution.countAssignments(new int[]{4, 6, 10}, 0)).as("countAssignments([4, 6, 10], 0)").isEqualTo(2);
    }

    @Test
    void randomValues() {
        assertThat(Solution.countAssignments(new int[]{1, 4, 1, 1, 6, 9, 6, 1, 0, 5, 3, 1}, 5)).as("countAssignments([1, 4, 1, 1, 6, 9, 6, 1, 0, 5, 3, 1], 5)").isEqualTo(0);
    }

    @Test
    void singleValueBothSigns() {
        assertThat(Solution.countAssignments(new int[]{5}, -5)).as("countAssignments([5], -5)").isEqualTo(1);
    }

    @Test
    void singleZeroTargetZero() {
        assertThat(Solution.countAssignments(new int[]{0}, 0)).as("countAssignments([0], 0)").isEqualTo(2);
    }
}
