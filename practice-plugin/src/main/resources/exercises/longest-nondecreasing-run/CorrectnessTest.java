package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CorrectnessTest {
    @Test
    void handlesEmptyInput() {
        assertThat(Solution.longestRun(new int[0])).isZero();
    }

    @Test
    void handlesAllEqualValues() {
        assertThat(Solution.longestRun(new int[]{7, 7, 7, 7})).isEqualTo(4);
    }

    @Test
    void findsRunAtEnd() {
        assertThat(Solution.longestRun(new int[]{9, 1, 2, 2, 3, 0})).isEqualTo(4);
    }

    @Test
    void doesNotMutateInput() {
        int[] numbers = {3, 4, 1};
        Solution.longestRun(numbers);
        assertThat(numbers).containsExactly(3, 4, 1);
    }
}
