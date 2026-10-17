package com.jinloes.practice;

public class Solution {
    public static boolean isPalindrome(int value) {
        if (value < 0 || (value % 10 == 0 && value != 0)) return false;
        int reversed = 0, remaining = value;
        while (remaining > reversed) { reversed = reversed * 10 + remaining % 10; remaining /= 10; }
        return remaining == reversed || remaining == reversed / 10;
    }
}
