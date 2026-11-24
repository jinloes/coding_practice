package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CorrectnessTest {
    @Test
    void executesAtRestingMakerPriceNotIncomingPrice() {
        Solution book = new Solution();
        book.submitOrder(1, true, 105, 4);
        assertThat(book.submitOrder(2, false, 100, 4)).containsExactly(
                new Solution.Trade(1, 2, 105, 4));
    }

    @Test
    void incomingRemainderRestsAndMatchesLaterOrders() {
        Solution book = new Solution();
        book.submitOrder(1, false, 100, 3);
        assertThat(book.submitOrder(2, true, 101, 10)).containsExactly(
                new Solution.Trade(2, 1, 100, 3));
        // 7 shares of buy #2 now rest at 101
        assertThat(book.submitOrder(3, false, 101, 5)).containsExactly(
                new Solution.Trade(2, 3, 101, 5));
        assertThat(book.submitOrder(4, false, 100, 4)).containsExactly(
                new Solution.Trade(2, 4, 101, 2));
    }

    @Test
    void respectsFifoEvenWhenOrderIdsAreNotMonotonic() {
        Solution book = new Solution();
        book.submitOrder(50, false, 100, 3);
        book.submitOrder(10, false, 100, 3);
        assertThat(book.submitOrder(99, true, 100, 4)).containsExactly(
                new Solution.Trade(99, 50, 100, 3),
                new Solution.Trade(99, 10, 100, 1));
    }
}
