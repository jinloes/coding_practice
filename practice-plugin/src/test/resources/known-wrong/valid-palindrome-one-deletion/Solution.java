package com.jinloes.practice;

public final class Solution {
    private Solution() {
    }

    public static boolean canBePalindrome(String text) {
        int left = 0;
        int right = text.length() - 1;
        boolean skipped = false;
        while (left < right) {
            if (text.charAt(left) != text.charAt(right)) {
                if (skipped) {
                    return false;
                }
                skipped = true;
                // Greedy single-branch bug: always skips left without trying right.
                left++;
                continue;
            }
            left++;
            right--;
        }
        return true;
    }
}
