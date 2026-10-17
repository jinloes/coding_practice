package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CorrectnessTest {
    @Test
    void handlesShortInputs() { assertThat(Solution.maxProfit(new int[0])).isZero(); assertThat(Solution.maxProfit(new int[]{5})).isZero(); }
    @Test
    void oneLongRiseUsesOnlyOneTransaction() { assertThat(Solution.maxProfit(new int[]{1, 2, 3, 4, 5})).isEqualTo(4); }
    @Test
    void separatedValleysMatter() { assertThat(Solution.maxProfit(new int[]{2, 4, 1, 7, 5, 3, 6})).isEqualTo(9); }
    @Test
    void doesNotMutateInput() { int[] prices = {5, 1, 6}; Solution.maxProfit(prices); assertThat(prices).containsExactly(5, 1, 6); }
}
