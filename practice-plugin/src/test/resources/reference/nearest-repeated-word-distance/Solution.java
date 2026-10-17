package com.jinloes.practice;

import java.util.HashMap;
import java.util.Map;

public class Solution {
    public static int nearestDistance(String[] words) {
        Map<String, Integer> lastSeen = new HashMap<>();
        int best = Integer.MAX_VALUE;
        for (int i = 0; i < words.length; i++) {
            Integer previous = lastSeen.put(words[i], i);
            if (previous != null) best = Math.min(best, i - previous);
        }
        return best == Integer.MAX_VALUE ? -1 : best;
    }
}
