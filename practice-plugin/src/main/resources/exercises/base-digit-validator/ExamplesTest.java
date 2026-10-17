package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ExamplesTest {
    @Test
    void acceptsBinaryDigits() { assertThat(Solution.isValidForBase("10110", 2)).isTrue(); }
    @Test
    void acceptsUppercaseHexPrefix() { assertThat(Solution.isValidForBase("0Xff", 16)).isTrue(); }
    @Test
    void rejectsDigitOutsideBase() { assertThat(Solution.isValidForBase("128", 8)).isFalse(); }
}
