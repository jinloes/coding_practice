package com.jinloes.practice;

public class Solution {
    public static int longestRepeat(String text, int k) {
        int[] counts = new int[26];
        int start = 0;
        int mostFrequent = 0;
        int best = 0;
        for (int end = 0; end < text.length(); end++) {
            mostFrequent = Math.max(mostFrequent, ++counts[text.charAt(end) - 'A']);
            while ((long) (end - start + 1) - mostFrequent > k) {
                counts[text.charAt(start) - 'A']--;
                start++;
            }
            best = Math.max(best, end - start + 1);
        }
        return best;
    }
}
