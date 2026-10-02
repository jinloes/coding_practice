package com.jinloes.practice;

public class Solution {
    public static int longestRepeat(String text, int k) {
        int[] counts = new int[26];
        int mostFrequent = 0;
        for (int i = 0; i < text.length(); i++) {
            mostFrequent = Math.max(mostFrequent, ++counts[text.charAt(i) - 'A']);
        }
        return (int) Math.min(text.length(), (long) mostFrequent + k);
    }
}
