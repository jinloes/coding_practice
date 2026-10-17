package com.jinloes.practice;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Solution {
    public static int[] smallestCover(String[] words, String[] keywords) {
        if (keywords.length == 0) return new int[]{0, -1};
        Set<String> required = new HashSet<>();
        for (String keyword : keywords) required.add(keyword);
        Map<String, Integer> counts = new HashMap<>();
        int covered = 0;
        int bestLeft = -1, bestRight = -1;
        for (int left = 0, right = 0; right < words.length; right++) {
            if (required.contains(words[right])) {
                int count = counts.getOrDefault(words[right], 0);
                counts.put(words[right], count + 1);
                if (count == 0) covered++;
            }
            while (covered == required.size()) {
                if (bestLeft < 0 || right - left < bestRight - bestLeft) { bestLeft = left; bestRight = right; }
                if (required.contains(words[left])) {
                    int count = counts.get(words[left]) - 1;
                    counts.put(words[left], count);
                    if (count == 0) covered--;
                }
                left++;
            }
        }
        return new int[]{bestLeft, bestRight};
    }
}
