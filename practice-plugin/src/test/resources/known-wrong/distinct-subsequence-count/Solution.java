package com.jinloes.practice;

public class Solution {
    public static long countSubsequences(String source, String pattern) {
        int m = pattern.length();
        long[] ways = new long[m + 1];
        ways[0] = 1;
        for (int i = 0; i < source.length(); i++) {
            char c = source.charAt(i);
            for (int j = 1; j <= m; j++) {
                if (pattern.charAt(j - 1) == c) {
                    ways[j] += ways[j - 1];
                }
            }
        }
        return ways[m];
    }
}
