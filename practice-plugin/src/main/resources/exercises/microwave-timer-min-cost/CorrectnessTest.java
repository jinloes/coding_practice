package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CorrectnessTest {
    @Test
    void supportsNinetySecondsRepresentation() { assertThat(Solution.minCost(5,3,2,90)).isEqualTo(10); }
    @Test
    void mayPreferLeadingMinuteRepresentation() { assertThat(Solution.minCost(1,10,1,70)).isEqualTo(13); }
    @Test
    void handlesMaximumCookTime() { assertThat(Solution.minCost(9,2,1,6039)).isEqualTo(4); }
    @Test
    void zeroMoveCostCountsOnlyPresses() { assertThat(Solution.minCost(4,0,3,125)).isEqualTo(9); }
    @Test
    void repeatedDigitsAvoidExtraMoves() { assertThat(Solution.minCost(2,7,1,222)).isEqualTo(24); }
}
