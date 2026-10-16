package com.jinloes.practice;

public class Solution {
    public static int reverseDigits(int x) {
        int reversed = 0;
        while (x != 0) {
            reversed = reversed * 10 + x % 10;
            x /= 10;
        }
        return reversed;
    }
}
