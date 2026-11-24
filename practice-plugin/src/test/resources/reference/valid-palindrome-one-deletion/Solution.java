package com.jinloes.practice;

public final class Solution {
    private Solution() {
    }

    public static boolean canBePalindrome(String text) {
        int left = 0;
        int right = text.length() - 1;
        while (left < right) {
            if (text.charAt(left) != text.charAt(right)) {
                return isStrictPalindrome(text, left + 1, right)
                        || isStrictPalindrome(text, left, right - 1);
            }
            left++;
            right--;
        }
        return true;
    }

    private static boolean isStrictPalindrome(String text, int left, int right) {
        while (left < right) {
            if (text.charAt(left) != text.charAt(right)) {
                return false;
            }
            left++;
            right--;
        }
        return true;
    }
}
