package com.jinloes.practice;

import java.util.Arrays;

public class Solution {
    public static boolean canGroup(int[] cards, int groupSize) {
        if (groupSize < 1) {
            throw new IllegalArgumentException("groupSize must be positive");
        }
        if (cards.length % groupSize != 0) {
            return false;
        }
        int[] sorted = cards.clone();
        Arrays.sort(sorted);
        for (int i = 1; i < sorted.length; i++) {
            if (sorted[i] - sorted[i - 1] > 1) {
                return false;
            }
        }
        return true;
    }
}
