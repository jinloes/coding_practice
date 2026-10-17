package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ExamplesTest {
    @Test
    void entireClassicSequenceAlternates() { assertThat(Solution.longestZigZag(new int[]{1, 7, 4, 9, 2, 5})).isEqualTo(6); }
    @Test
    void allEqualValuesContributeOneValue() { assertThat(Solution.longestZigZag(new int[]{1, 1, 1})).isEqualTo(1); }
}
