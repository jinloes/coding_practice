package com.jinloes.practice;

import java.util.Map;
import java.util.TreeMap;

public class Solution {
    private final TreeMap<Integer, Integer> ranges = new TreeMap<>();

    public Solution() {
    }

    public void addRange(int left, int right) {
        Map.Entry<Integer, Integer> entry = ranges.floorEntry(left);
        if (entry != null && entry.getValue() >= left) {
            left = entry.getKey();
            right = Math.max(right, entry.getValue());
            ranges.remove(entry.getKey());
        }
        Map.Entry<Integer, Integer> next = ranges.ceilingEntry(left);
        while (next != null && next.getKey() <= right) {
            right = Math.max(right, next.getValue());
            ranges.remove(next.getKey());
            next = ranges.ceilingEntry(left);
        }
        ranges.put(left, right);
    }

    public boolean queryRange(int left, int right) {
        Map.Entry<Integer, Integer> entry = ranges.floorEntry(left);
        return entry != null && entry.getValue() >= right;
    }

    public void removeRange(int left, int right) {
        Map.Entry<Integer, Integer> entry = ranges.floorEntry(left);
        if (entry != null && entry.getValue() > left) {
            int start = entry.getKey();
            int end = entry.getValue();
            ranges.remove(start);
            if (start < left) {
                ranges.put(start, left);
            }
            if (end > right) {
                ranges.put(right, end);
                return;
            }
        }
        Map.Entry<Integer, Integer> next = ranges.ceilingEntry(left);
        while (next != null && next.getKey() < right) {
            int start = next.getKey();
            int end = next.getValue();
            ranges.remove(start);
            if (end > right) {
                ranges.put(right, end);
                break;
            }
            next = ranges.ceilingEntry(left);
        }
    }
}
