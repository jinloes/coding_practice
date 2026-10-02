package com.jinloes.practice;

public class Solution {
    public static int longestUniqueLength(String text) {
        int[] nextAllowedStart = new int[Character.MAX_VALUE + 1];
        int start = 0;
        int best = 0;
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            start = Math.max(start, nextAllowedStart[c]);
            nextAllowedStart[c] = i + 1;
            best = Math.max(best, i - start + 1);
        }
        return best;
    }
}
