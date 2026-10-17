package com.jinloes.practice;

import java.util.HashMap;
import java.util.Map;

public class Solution {
    public static int longestAtMostKDistinct(String text, int k) {
        if (k <= 0 || text.isEmpty()) return 0;
        Map<Character, Integer> counts = new HashMap<>(); int left = 0, best = 0;
        for (int right = 0; right < text.length(); right++) {
            counts.merge(text.charAt(right), 1, Integer::sum);
            while (counts.size() > k) { char drop = text.charAt(left++); int next = counts.get(drop) - 1; if (next == 0) counts.remove(drop); else counts.put(drop, next); }
            best = Math.max(best, right - left + 1);
        }
        return best;
    }
}
