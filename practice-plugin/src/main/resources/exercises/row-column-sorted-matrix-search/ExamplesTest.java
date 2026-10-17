package com.jinloes.practice;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class ExamplesTest {
    @Test
    void findsInteriorValue() { assertThat(Solution.contains(new int[][]{{1,4,7},{2,5,8},{3,6,9}}, 5)).isTrue(); }
    @Test
    void reportsMissingValue() { assertThat(Solution.contains(new int[][]{{1,4,7},{2,5,8},{3,6,9}}, 10)).isFalse(); }
    @Test
    void handlesNegativeValues() { assertThat(Solution.contains(new int[][]{{-5,-1},{-3,4}}, -3)).isTrue(); }
}

