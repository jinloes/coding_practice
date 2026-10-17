package com.jinloes.practice;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class ExamplesTest {
    @Test
    void convertsBetweenNonDecimalBases() { assertThat(Solution.convert("615", 7, 13)).isEqualTo("1A7"); }
    @Test
    void preservesNegativeSign() { assertThat(Solution.convert("-1010", 2, 16)).isEqualTo("-A"); }
    @Test
    void convertsZeroToSingleZero() { assertThat(Solution.convert("0", 10, 2)).isEqualTo("0"); }
}
