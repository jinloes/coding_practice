package com.jinloes.practice;

public class Solution {
    public static int[] increment(int[] digits) {
        int[] result = digits.clone();
        int last = result.length - 1;
        if (result[last] < 9) {
            result[last]++;
            return result;
        }
        result[last] = 0;
        if (last == 0) {
            return new int[]{1, 0};
        }
        result[last - 1]++;
        return result;
    }
}
