package com.jinloes.practice;

public class Solution {
    public static boolean isBalanced(String pattern) {
        int low = 0;
        int high = 0;
        for (int i = 0; i < pattern.length(); i++) {
            char c = pattern.charAt(i);
            if (c == '(') {
                low++;
                high++;
            } else if (c == ')') {
                low--;
                high--;
            } else {
                low--;
                high++;
            }
            if (high < 0) {
                return false;
            }
        }
        return low <= 0 && high >= 0;
    }
}
