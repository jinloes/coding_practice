package com.jinloes.practice;

public class Solution {
    private static final int MOD = 1_000_000_007;

    public static int count(String digits) {
        int n = digits.length();
        if (n == 0) {
            return 0;
        }
        long beforePrevious = 1;
        long previous = digits.charAt(0) != '0' ? 1 : 0;
        for (int i = 1; i < n; i++) {
            char current = digits.charAt(i);
            long ways = 0;
            if (current != '0') {
                ways += previous;
            }
            int pair = (digits.charAt(i - 1) - '0') * 10 + (current - '0');
            if (pair >= 10 && pair <= 26) {
                ways += beforePrevious;
            }
            beforePrevious = previous;
            previous = ways % MOD;
        }
        return (int) previous;
    }
}
