package com.jinloes.practice;

import java.util.HashMap;
import java.util.Map;

public class Solution {
    private final Map<Integer, Integer> byTimestamp = new HashMap<>();
    private int latestTimestamp;
    // Bug: tracks running min/max monotonically, ignoring timestamp overwrites.
    private int minPrice = Integer.MAX_VALUE;
    private int maxPrice = Integer.MIN_VALUE;

    public Solution() {
    }

    public void update(int timestamp, int price) {
        byTimestamp.put(timestamp, price);
        if (timestamp > latestTimestamp) {
            latestTimestamp = timestamp;
        }
        if (price < minPrice) {
            minPrice = price;
        }
        if (price > maxPrice) {
            maxPrice = price;
        }
    }

    public int latest() {
        return byTimestamp.get(latestTimestamp);
    }

    public int maxPrice() {
        return maxPrice;
    }

    public int minPrice() {
        return minPrice;
    }
}
