package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

class ExamplesTest {
    @Test
    void buysTheDipAndSellsThePeakAfterIt() {
        int[] prices = {7, 1, 5, 3, 6, 4};
        assertThat(Solution.maxProfit(prices))
                .as("maxProfit(%s) must buy at 1 and sell at 6", Arrays.toString(prices))
                .isEqualTo(5);
    }

    @Test
    void reportsNoProfitWhenPricesOnlyFall() {
        int[] prices = {7, 6, 4, 3, 1};
        assertThat(Solution.maxProfit(prices))
                .as("maxProfit(%s) must return 0 because no sale beats its purchase",
                        Arrays.toString(prices))
                .isEqualTo(0);
    }

    @Test
    void handlesNoDaysAtAll() {
        assertThat(Solution.maxProfit(new int[0]))
                .as("maxProfit([]) must return 0 because there is nothing to trade")
                .isEqualTo(0);
    }
}
