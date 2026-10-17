package com.jinloes.practice;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class CorrectnessTest {
    @Test
    void acceptsLettersInInput() { assertThat(Solution.convert("ff", 16, 2)).isEqualTo("11111111"); }
    @Test
    void handlesDecimalAndNegativeZero() { assertThat(Solution.convert("255", 10, 16)).isEqualTo("FF"); assertThat(Solution.convert("-0", 10, 2)).isEqualTo("0"); }
}
