package com.jinloes.practice;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class ExamplesTest {
    @Test
    void countsRepeatedPositiveSums() { assertThat(Solution.countSubarrays(new int[]{1, 1, 1}, 2)).isEqualTo(2); }
    @Test
    void includesNegativeAndZeroValues() { assertThat(Solution.countSubarrays(new int[]{1, -1, 0}, 0)).isEqualTo(3); }
    @Test
    void countsSeveralMatches() { assertThat(Solution.countSubarrays(new int[]{3, 4, 7, 2, -3, 1, 4, 2}, 7)).isEqualTo(4); }
}
