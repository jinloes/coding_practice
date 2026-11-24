package com.jinloes.practice;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class Solution {
    public int longestChain(String[] words) {
        if (words.length == 0) {
            return 0;
        }
        String[] sorted = words.clone();
        Arrays.sort(sorted, Comparator.comparingInt(String::length));

        Map<String, Integer> dp = new HashMap<>();
        int best = 0;
        for (String word : sorted) {
            // Bug: only checks deleting the first or last character, missing middle insertions.
            int chain = 1;
            if (word.length() > 1) {
                chain = Math.max(chain, dp.getOrDefault(word.substring(1), 0) + 1);
                chain = Math.max(chain, dp.getOrDefault(word.substring(0, word.length() - 1), 0) + 1);
            }
            dp.merge(word, chain, Math::max);
            best = Math.max(best, chain);
        }
        return best;
    }
}
