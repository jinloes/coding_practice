package com.jinloes.coding_practice.arrays;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class BuyAndSellStock2Test {

    private static Stream<Arguments> arraysForMaxProfit() {
        return Stream.of(
                // Edge cases
                Arguments.of(null, 0),                                          // null array
                Arguments.of(new int[]{}, 0),                                    // empty array
                Arguments.of(new int[]{5}, 0),                                   // single day

                // No profit possible (always decreasing)
                Arguments.of(new int[]{10, 9, 8, 7, 6}, 0),                      // strictly decreasing
                Arguments.of(new int[]{5, 5, 5, 5}, 0),                         // all same price

                // Single transaction is best (no second profitable transaction)
                Arguments.of(new int[]{1, 2, 3, 4, 5}, 4),                      // strictly increasing
                Arguments.of(new int[]{2, 10, 1, 3}, 10),                       // buy@2 sell@10 (+8), buy@1 sell@3 (+2) = 10

                // Two transactions are better than one
                Arguments.of(new int[]{12, 11, 13, 9, 12, 8, 14, 13, 15}, 10),  // original test case

                // Multiple possible two-transaction combinations
                Arguments.of(new int[]{3, 3, 5, 0, 0, 3, 1, 4}, 6),             // peak at 5 and 4
                Arguments.of(new int[]{1, 2, 4, 2, 5, 7, 2, 4, 9, 0}, 13),     // multiple peaks

                // Three transactions possible but only two allowed
                Arguments.of(new int[]{1, 3, 2, 5, 0, 3, 7, 2, 8}, 13),       // buy@0 sell@7 (+7), buy@2 sell@8 (+6) = 13

                // Very small profits
                Arguments.of(new int[]{1, 2, 1, 2}, 2),                         // alternating small profits

                // Large numbers
                Arguments.of(new int[]{1000, 500, 1000, 500, 1000}, 1000)     // high volatility
        );
    }

    @ParameterizedTest
    @MethodSource("arraysForMaxProfit")
    void maxProfit(int[] prices, int expectedMaxProfit) {
        assertThat(BuyAndSellStock2.maxProfit(prices)).isEqualTo(expectedMaxProfit);
    }
}