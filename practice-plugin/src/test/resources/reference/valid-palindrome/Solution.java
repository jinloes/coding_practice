package com.jinloes.practice;

public class Solution {
    public static boolean isPalindrome(String text) {
        int left = 0;
        int right = text.length() - 1;
        while (left < right) {
            char first = text.charAt(left);
            if (!Character.isLetterOrDigit(first)) {
                left++;
                continue;
            }
            char last = text.charAt(right);
            if (!Character.isLetterOrDigit(last)) {
                right--;
                continue;
            }
            if (Character.toLowerCase(first) != Character.toLowerCase(last)) {
                return false;
            }
            left++;
            right--;
        }
        return true;
    }
}
