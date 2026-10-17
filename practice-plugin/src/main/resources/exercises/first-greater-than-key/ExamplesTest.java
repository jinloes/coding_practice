package com.jinloes.practice;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class ExamplesTest {
    @Test
    void skipsEqualValues() { assertThat(Solution.firstGreaterThan(new int[]{1, 2, 2, 4}, 2)).isEqualTo(3); }
    @Test
    void canReturnFirstIndex() { assertThat(Solution.firstGreaterThan(new int[]{1, 3, 5}, 0)).isZero(); }
    @Test
    void reportsNoGreaterValue() { assertThat(Solution.firstGreaterThan(new int[]{1, 3, 5}, 9)).isEqualTo(-1); }
}

