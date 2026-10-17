package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CorrectnessTest {
    @Test
    void handlesEmptyInput() { assertThat(Solution.longestZigZag(new int[0])).isZero(); }
    @Test
    void monotoneValuesAllowTwoEndpoints() { assertThat(Solution.longestZigZag(new int[]{1, 2, 3, 4, 5})).isEqualTo(2); }
    @Test
    void skipsEqualValuesBetweenDirections() { assertThat(Solution.longestZigZag(new int[]{1, 7, 7, 4, 4, 9})).isEqualTo(4); }
    @Test
    void handlesNegativeValues() { assertThat(Solution.longestZigZag(new int[]{0, -2, 3, -4, -4, 5})).isEqualTo(5); }
}
