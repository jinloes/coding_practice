package com.jinloes.practice;

public class Solution {
    public static int reverseBits(int value) {
        int result = 0;
        for (int i = 0; i < 31; i++) {
            result = (result << 1) | (value & 1);
            value >>>= 1;
        }
        return result;
    }
}
