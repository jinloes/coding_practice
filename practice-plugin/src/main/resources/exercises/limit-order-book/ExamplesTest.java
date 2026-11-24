package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ExamplesTest {
    @Test
    void matchesLowestSellPricesFirstWithPartialFill() {
        Solution book = new Solution();
        assertThat(book.submitOrder(1, false, 100, 10)).isEmpty();
        assertThat(book.submitOrder(2, false, 102, 5)).isEmpty();
        assertThat(book.submitOrder(3, true, 102, 12)).containsExactly(
                new Solution.Trade(3, 1, 100, 10),
                new Solution.Trade(3, 2, 102, 2));
        // Remaining 3 shares of sell #2 at 102 stay on the book
        assertThat(book.submitOrder(4, true, 102, 5)).containsExactly(
                new Solution.Trade(4, 2, 102, 3));
    }

    @Test
    void breaksSamePriceTiesByArrivalOrderFifo() {
        Solution book = new Solution();
        assertThat(book.submitOrder(1, true, 100, 5)).isEmpty();
        assertThat(book.submitOrder(2, true, 100, 5)).isEmpty();
        assertThat(book.submitOrder(3, false, 99, 7)).containsExactly(
                new Solution.Trade(1, 3, 100, 5),
                new Solution.Trade(2, 3, 100, 2));
    }

    @Test
    void nonCrossingOrdersRestWithoutTrading() {
        Solution book = new Solution();
        assertThat(book.submitOrder(1, true, 95, 4)).isEmpty();
        assertThat(book.submitOrder(2, false, 105, 4)).isEmpty();
    }
}
