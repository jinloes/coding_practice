package com.jinloes.practice;

public class Solution {
    public static boolean canPermutePalindrome(String text) {
        boolean[] odd = new boolean[Character.MAX_VALUE + 1];
        int oddCount = 0;
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            odd[c] = !odd[c];
            oddCount += odd[c] ? 1 : -1;
        }
        return oddCount <= 1;
    }
}
