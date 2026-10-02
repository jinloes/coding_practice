package com.jinloes.practice;

public class Solution {
    public static String longestPalindrome(String text) {
        int bestStart = 0;
        int bestLength = 0;
        for (int center = 0; center < text.length(); center++) {
            for (int parity = 0; parity < 2; parity++) {
                int left = center;
                int right = center + parity;
                while (left >= 0 && right < text.length() && text.charAt(left) == text.charAt(right)) {
                    left--;
                    right++;
                }
                int length = right - left - 1;
                if (length > bestLength) {
                    bestLength = length;
                    bestStart = left + 1;
                }
            }
        }
        return text.substring(bestStart, bestStart + bestLength);
    }
}
