package com.jinloes.coding_practice.arrays;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class BuyAndSellStockTest {

    private static Stream<Arguments> arraysForMaxProfit() {
        return Stream.of(
                // Edge cases
                Arguments.of(null, 0),                                          // null array
                Arguments.of(new int[]{}, 0),                                    // empty array
                Arguments.of(new int[]{5}, 0),                                   // single day

                // No profit possible
                Arguments.of(new int[]{10, 9, 8, 7, 6}, 0),                      // strictly decreasing
                Arguments.of(new int[]{5, 5, 5, 5}, 0),                         // all same price

                // Single transaction scenarios
                Arguments.of(new int[]{1, 2, 3, 4, 5}, 4),                      // strictly increasing
                Arguments.of(new int[]{310, 315, 275, 295, 260, 270, 290, 230, 255, 250}, 30), // original test case
                Arguments.of(new int[]{7, 1, 5, 3, 6, 4}, 5),                    // buy at 1, sell at 6
                Arguments.of(new int[]{7, 6, 4, 3, 1}, 0),                      // no profit possible
                Arguments.of(new int[]{2, 10, 1, 3}, 8),                        // big profit in the middle

                // Small price differences
                Arguments.of(new int[]{1, 2}, 1),                               // minimal profit
                Arguments.of(new int[]{2, 1}, 0),                               // loss scenario

                // Large numbers
                Arguments.of(new int[]{1000, 500, 1000}, 500),                 // large swing
                Arguments.of(new int[]{Integer.MAX_VALUE, Integer.MIN_VALUE}, 0) // extreme values
        );
    }

    @ParameterizedTest
    @MethodSource("arraysForMaxProfit")
    void maxProfit(int[] prices, int expectedMaxProfit) {
        assertThat(BuyAndSellStock.maxProfit(prices)).isEqualTo(expectedMaxProfit);
    }
}