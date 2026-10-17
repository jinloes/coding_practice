package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ExamplesTest {
    @Test
    void combinesTwoProfitableTrades() { assertThat(Solution.maxProfit(new int[]{3, 3, 5, 0, 0, 3, 1, 4})).isEqualTo(6); }
    @Test
    void descendingPricesHaveNoProfit() { assertThat(Solution.maxProfit(new int[]{7, 6, 4, 3, 1})).isZero(); }
}
