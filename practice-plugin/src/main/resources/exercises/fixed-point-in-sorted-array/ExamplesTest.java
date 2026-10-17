package com.jinloes.practice;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class ExamplesTest {
    @Test
    void findsInteriorMatch() { assertThat(Solution.findFixedPoint(new int[]{-3, -1, 2, 5})).isEqualTo(2); }
    @Test
    void reportsNoMatch() { assertThat(Solution.findFixedPoint(new int[]{1, 2, 3})).isEqualTo(-1); }
    @Test
    void findsZeroAtStart() { assertThat(Solution.findFixedPoint(new int[]{0, 4, 8})).isZero(); }
}

