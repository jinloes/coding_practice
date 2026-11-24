package com.jinloes.practice;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class Solution {
    private final Map<Integer, Integer> byTimestamp = new HashMap<>();
    private final TreeMap<Integer, Integer> priceCounts = new TreeMap<>();
    private int latestTimestamp;

    public Solution() {
    }

    public void update(int timestamp, int price) {
        Integer previous = byTimestamp.put(timestamp, price);
        if (previous != null) {
            int remaining = priceCounts.get(previous) - 1;
            if (remaining == 0) {
                priceCounts.remove(previous);
            } else {
                priceCounts.put(previous, remaining);
            }
        }
        priceCounts.merge(price, 1, Integer::sum);
        if (timestamp > latestTimestamp) {
            latestTimestamp = timestamp;
        }
    }

    public int latest() {
        return byTimestamp.get(latestTimestamp);
    }

    public int maxPrice() {
        return priceCounts.lastKey();
    }

    public int minPrice() {
        return priceCounts.firstKey();
    }
}
