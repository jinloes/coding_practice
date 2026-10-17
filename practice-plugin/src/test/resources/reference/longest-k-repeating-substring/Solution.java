package com.jinloes.practice;

public class Solution {
    public static int longestSubstring(String text, int k) { if (k <= 1) return text.length(); return solve(text, 0, text.length(), k); }
    private static int solve(String text, int start, int end, int k) {
        if (end - start < k) return 0;
        int[] counts = new int[Character.MAX_VALUE + 1];
        for (int i = start; i < end; i++) counts[text.charAt(i)]++;
        for (int split = start; split < end; split++) {
            if (counts[text.charAt(split)] > 0 && counts[text.charAt(split)] < k) {
                int best = 0, next = start;
                for (int i = start; i <= end; i++) if (i == end || text.charAt(i) == text.charAt(split)) { best = Math.max(best, solve(text, next, i, k)); next = i + 1; }
                return best;
            }
        }
        return end - start;
    }
}
