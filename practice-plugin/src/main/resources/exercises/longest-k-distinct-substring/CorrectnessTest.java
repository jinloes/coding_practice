package com.jinloes.practice;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class CorrectnessTest {
    @Test
    void handlesEmptyAndLargeK() { assertThat(Solution.longestAtMostKDistinct("", 2)).isZero(); assertThat(Solution.longestAtMostKDistinct("abc", 5)).isEqualTo(3); }
    @Test
    void shrinksAfterDistinctOverflow() { assertThat(Solution.longestAtMostKDistinct("aabacbebebe", 3)).isEqualTo(7); }
}
