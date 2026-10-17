package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ExamplesTest {
    @Test
    void parsesZero() { assertThat(Solution.stringToInt("0")).isZero(); }
    @Test
    void parsesNegativeValue() { assertThat(Solution.stringToInt("-2048")).isEqualTo(-2048); }
    @Test
    void formatsPositiveValue() { assertThat(Solution.intToString(12345)).isEqualTo("12345"); }
}
