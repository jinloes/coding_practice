package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ExamplesTest {
    @Test
    void countsEqualValuesInsideARun() {
        assertThat(Solution.longestRun(new int[]{5, 6, 6, 2, 3})).isEqualTo(3);
    }

    @Test
    void descendingArrayHasOnlySingletonRuns() {
        assertThat(Solution.longestRun(new int[]{4, 3, 2})).isEqualTo(1);
    }
}
