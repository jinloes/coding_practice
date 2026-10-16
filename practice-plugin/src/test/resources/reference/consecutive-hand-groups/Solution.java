package com.jinloes.practice;

import java.util.TreeMap;

public class Solution {
    public static boolean canGroup(int[] cards, int groupSize) {
        if (groupSize < 1) {
            throw new IllegalArgumentException("groupSize must be positive");
        }
        if (cards.length % groupSize != 0) {
            return false;
        }
        TreeMap<Integer, Integer> counts = new TreeMap<>();
        for (int card : cards) {
            counts.merge(card, 1, Integer::sum);
        }
        while (!counts.isEmpty()) {
            int first = counts.firstKey();
            for (int offset = 0; offset < groupSize; offset++) {
                long value = (long) first + offset;
                if (value > Integer.MAX_VALUE) {
                    return false;
                }
                Integer count = counts.get((int) value);
                if (count == null) {
                    return false;
                }
                if (count == 1) {
                    counts.remove((int) value);
                } else {
                    counts.put((int) value, count - 1);
                }
            }
        }
        return true;
    }
}
