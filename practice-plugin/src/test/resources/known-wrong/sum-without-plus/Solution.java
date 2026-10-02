package com.jinloes.practice;

public class Solution {
    public static int add(int a, int b) {
        while (b > 0) {
            int carry = (a & b) << 1;
            a ^= b;
            b = carry;
        }
        return a;
    }
}
