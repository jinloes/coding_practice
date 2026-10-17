package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ExamplesTest {
    @Test
    void combinesTwoSingleElementSubarrays() { assertThat(Solution.minSumOfLengths(new int[]{3, 2, 2, 4, 3}, 3)).isEqualTo(2); }
    @Test
    void findsSubarraysAtBothEnds() { assertThat(Solution.minSumOfLengths(new int[]{7, 3, 4, 7}, 7)).isEqualTo(2); }
}
