package com.jinloes.practice;

public class Solution {
    public static int countPalindromes(String text) {
        int count = 0;
        for (int center = 0; center < text.length(); center++) {
            int left = center;
            int right = center;
            while (left >= 0 && right < text.length() && text.charAt(left) == text.charAt(right)) {
                count++;
                left--;
                right++;
            }
        }
        return count;
    }
}
