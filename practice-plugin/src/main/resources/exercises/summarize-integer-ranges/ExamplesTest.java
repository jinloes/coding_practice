package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ExamplesTest {
    @Test
    void compressesAdjacentPositiveRuns() {
        assertThat(Solution.summarize(new int[]{0, 1, 2, 4, 5, 7}))
                .containsExactly("0-2", "4-5", "7");
    }

    @Test
    void formatsNegativeRanges() {
        assertThat(Solution.summarize(new int[]{-3, -2, 0, 2, 3, 4}))
                .containsExactly("-3--2", "0", "2-4");
    }
}
