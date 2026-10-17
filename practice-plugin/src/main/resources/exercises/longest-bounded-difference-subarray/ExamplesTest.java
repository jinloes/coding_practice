package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ExamplesTest {
    @Test
    void findsShortWindowInMixedArray() {
        assertThat(Solution.longestSubarray(new int[]{8, 2, 4, 7}, 4)).isEqualTo(2);
    }

    @Test
    void keepsFourValuesWithinLimit() {
        assertThat(Solution.longestSubarray(new int[]{10, 1, 2, 4, 7, 2}, 5)).isEqualTo(4);
    }
}
