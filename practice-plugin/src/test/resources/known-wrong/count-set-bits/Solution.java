package com.jinloes.practice;

public class Solution {
    public static int countSetBits(int value) {
        int count = 0;
        while (value > 0) {
            value &= value - 1;
            count++;
        }
        return count;
    }
}
