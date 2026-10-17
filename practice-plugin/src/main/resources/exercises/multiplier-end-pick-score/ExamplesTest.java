package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ExamplesTest {
    @Test
    void scoresIncreasingPositiveValues() { assertThat(Solution.maximumScore(new int[]{1,2,3}, new int[]{3,2,1})).isEqualTo(14); }
    @Test
    void handlesMixedSigns() { assertThat(Solution.maximumScore(new int[]{-5,-3,-3,-2,7,1}, new int[]{-10,-5,3,4,6})).isEqualTo(102); }
    @Test
    void handlesSingleOperation() { assertThat(Solution.maximumScore(new int[]{4}, new int[]{-2})).isEqualTo(-8); }
}
