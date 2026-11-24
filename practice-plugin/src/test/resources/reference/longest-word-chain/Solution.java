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

        Map<String, Integer> dp = new HashMap<>(sorted.length * 2);
        int best = 0;
        for (String word : sorted) {
            int chain = 1;
            int len = word.length();
            for (int i = 0; i < len; i++) {
                String predecessor = word.substring(0, i) + word.substring(i + 1);
                Integer prev = dp.get(predecessor);
                if (prev != null && prev + 1 > chain) {
                    chain = prev + 1;
                }
            }
            dp.merge(word, chain, Math::max);
            if (chain > best) {
                best = chain;
            }
        }
        return best;
    }
}
