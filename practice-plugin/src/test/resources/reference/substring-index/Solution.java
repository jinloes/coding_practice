package com.jinloes.practice;

public class Solution {
    public static int substringIndex(String text, String pattern) {
        if (text == null || pattern == null) {
            return -1;
        }
        if (pattern.isEmpty()) {
            return 0;
        }
        int[] lps = longestPrefixSuffix(pattern);
        int matched = 0;
        for (int i = 0; i < text.length(); i++) {
            while (matched > 0 && text.charAt(i) != pattern.charAt(matched)) {
                matched = lps[matched - 1];
            }
            if (text.charAt(i) == pattern.charAt(matched)) {
                matched++;
                if (matched == pattern.length()) {
                    return i - pattern.length() + 1;
                }
            }
        }
        return -1;
    }

    private static int[] longestPrefixSuffix(String pattern) {
        int[] lps = new int[pattern.length()];
        int length = 0;
        for (int i = 1; i < pattern.length(); i++) {
            while (length > 0 && pattern.charAt(i) != pattern.charAt(length)) {
                length = lps[length - 1];
            }
            if (pattern.charAt(i) == pattern.charAt(length)) {
                length++;
                lps[i] = length;
            }
        }
        return lps;
    }
}
