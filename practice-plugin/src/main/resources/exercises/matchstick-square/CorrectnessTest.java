package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CorrectnessTest {
    @Test
    void coversBacktrackingAndSmallInputs() {
        assertThat(Solution.canFormSquare(new int[]{5, 5, 5, 5, 1})).isFalse();
        assertThat(Solution.canFormSquare(new int[]{4, 4, 4, 4})).isTrue();
        assertThat(Solution.canFormSquare(new int[]{1, 1, 1})).isFalse();
        assertThat(Solution.canFormSquare(new int[]{5, 5, 5, 5, 4, 4, 4, 4, 3, 3, 3, 3})).isTrue();
    }
    @Test
    void doesNotMutateInput() {
        int[] sticks = {1, 1, 2, 2, 2};
        int[] before = sticks.clone();
        Solution.canFormSquare(sticks);
        assertThat(sticks).containsExactly(before);
    }
}
