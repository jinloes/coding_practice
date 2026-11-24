package com.jinloes.practice;

import java.util.List;

public class Solution {
    public record Trade(int buyId, int sellId, int price, int quantity) {
    }

    public List<Trade> submitOrder(int orderId, boolean isBuy, int price, int quantity) {
        throw new UnsupportedOperationException("Implement submitOrder");
    }
}
