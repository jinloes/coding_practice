package com.jinloes.practice;

public class Solution {
    public static int guessNumber(int upperBound, java.util.function.IntUnaryOperator feedback) {
        if (upperBound <= 0) return -1;
        int lo = 1, hi = upperBound;
        while (lo <= hi) {
            int mid = lo + (hi - lo) / 2;
            int result = feedback.applyAsInt(mid);
            if (result == 0) return mid;
            if (result > 0) lo = mid + 1; else hi = mid - 1;
        }
        return -1;
    }
}
