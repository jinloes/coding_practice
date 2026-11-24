package com.jinloes.practice;

import java.util.Map;
import java.util.TreeMap;

public class Solution {
    private final TreeMap<Integer, Integer> ranges = new TreeMap<>();

    public Solution() {
    }

    public void addRange(int left, int right) {
        ranges.put(left, right);
    }

    public boolean queryRange(int left, int right) {
        Map.Entry<Integer, Integer> entry = ranges.floorEntry(left);
        return entry != null && entry.getValue() >= right;
    }

    public void removeRange(int left, int right) {
        // Bug: deletes overlapping intervals without preserving untouched left/right remainders.
        Map.Entry<Integer, Integer> entry = ranges.floorEntry(left);
        if (entry != null && entry.getValue() > left) {
            ranges.remove(entry.getKey());
        }
    }
}
