package com.jinloes.practice;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

public class Solution {
    public record Trade(int buyId, int sellId, int price, int quantity) {
    }

    private static final class RestingOrder {
        final int orderId;
        final int price;
        final long sequence;
        int remaining;

        RestingOrder(int orderId, int price, int remaining, long sequence) {
            this.orderId = orderId;
            this.price = price;
            this.remaining = remaining;
            this.sequence = sequence;
        }
    }

    private final PriorityQueue<RestingOrder> bids = new PriorityQueue<>(
            Comparator.comparingInt((RestingOrder o) -> o.price).reversed()
                    .thenComparingLong(o -> o.sequence));
    private final PriorityQueue<RestingOrder> asks = new PriorityQueue<>(
            Comparator.comparingInt((RestingOrder o) -> o.price)
                    .thenComparingLong(o -> o.sequence));
    private long nextSequence;

    public List<Trade> submitOrder(int orderId, boolean isBuy, int price, int quantity) {
        List<Trade> trades = new ArrayList<>();
        int remaining = quantity;
        if (isBuy) {
            while (remaining > 0 && !asks.isEmpty() && asks.peek().price <= price) {
                RestingOrder ask = asks.peek();
                int filled = Math.min(remaining, ask.remaining);
                // Bug: uses incoming order price instead of resting maker price.
                trades.add(new Trade(orderId, ask.orderId, price, filled));
                remaining -= filled;
                ask.remaining -= filled;
                if (ask.remaining == 0) {
                    asks.poll();
                }
            }
            if (remaining > 0) {
                bids.offer(new RestingOrder(orderId, price, remaining, nextSequence++));
            }
        } else {
            while (remaining > 0 && !bids.isEmpty() && bids.peek().price >= price) {
                RestingOrder bid = bids.peek();
                int filled = Math.min(remaining, bid.remaining);
                trades.add(new Trade(bid.orderId, orderId, price, filled));
                remaining -= filled;
                bid.remaining -= filled;
                if (bid.remaining == 0) {
                    bids.poll();
                }
            }
            if (remaining > 0) {
                asks.offer(new RestingOrder(orderId, price, remaining, nextSequence++));
            }
        }
        return trades;
    }
}
