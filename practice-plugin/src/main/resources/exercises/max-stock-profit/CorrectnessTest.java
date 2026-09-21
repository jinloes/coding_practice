package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

class CorrectnessTest {
    @Test
    void handlesASingleDay() {
        assertProfit(new int[]{5}, 0);
    }

    @Test
    void handlesTwoDaysInBothDirections() {
        assertProfit(new int[]{1, 9}, 8);
        assertProfit(new int[]{9, 1}, 0);
    }

    @Test
    void refusesToSellBeforeBuying() {
        assertProfit(new int[]{9, 1, 1}, 0);
    }

    @Test
    void returnsZeroForAFlatMarket() {
        assertProfit(new int[]{4, 4, 4, 4}, 0);
    }

    @Test
    void ignoresAHigherPeakThatComesBeforeTheDip() {
        assertProfit(new int[]{20, 25, 1, 4}, 5);
    }

    @Test
    void findsTheBestPairAcrossSeveralDips() {
        assertProfit(new int[]{6, 2, 7, 1, 9, 3}, 8);
    }

    @Test
    void handlesNegativeAndLargePrices() {
        assertProfit(new int[]{-5, -1, -9, 10}, 19);
        assertProfit(new int[]{0, 1_000_000_000}, 1_000_000_000);
    }

    @Test
    void doesNotMutateTheInput() {
        int[] prices = {7, 1, 5, 3, 6, 4};
        int[] before = prices.clone();
        maxProfit(prices);
        assertThat(prices).as("maxProfit must not mutate %s", Arrays.toString(before))
                .containsExactly(before);
    }

    @Test
    void handlesALongRunOfDays() {
        int[] prices = new int[20_000];
        for (int i = 0; i < prices.length; i++) {
            prices[i] = 50_000 - i;
        }
        prices[prices.length - 1] = 100_000;
        assertProfit(prices, 100_000 - (50_000 - (prices.length - 2)));
    }

    private static void assertProfit(int[] prices, int expected) {
        assertThat(maxProfit(prices)).as("%s must return %d", call(prices), expected).isEqualTo(expected);
    }

    private static int maxProfit(int[] prices) {
        try {
            return Solution.maxProfit(prices);
        } catch (Throwable thrown) {
            return fail("%s threw %s".formatted(call(prices), thrown), thrown);
        }
    }

    private static String call(int[] prices) {
        String shown = prices.length <= 12
                ? Arrays.toString(prices)
                : "[" + prices[0] + ", " + prices[1] + ", ... (" + prices.length + " days)]";
        return "maxProfit(%s)".formatted(shown);
    }
}
