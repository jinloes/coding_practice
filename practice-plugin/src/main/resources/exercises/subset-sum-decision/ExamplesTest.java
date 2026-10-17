package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ExamplesTest {
    @Test
    void findsReachableSum() { assertThat(Solution.hasSubsetSum(new int[]{2, 4, 8}, 6)).isTrue(); }
    @Test
    void rejectsUnreachableSum() { assertThat(Solution.hasSubsetSum(new int[]{5, 10, 12}, 1)).isFalse(); }
    @Test
    void emptySubsetReachesZero() { assertThat(Solution.hasSubsetSum(new int[]{7, 3}, 0)).isTrue(); }
}
