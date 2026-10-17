package com.jinloes.practice;

public class Solution {
    public static int substringIndex(String text, String pattern) {
        if (text == null || pattern == null) {
            return -1;
        }
        if (pattern.isEmpty()) {
            return 0;
        }
        int matched = 0;
        for (int i = 0; i < text.length(); i++) {
            if (text.charAt(i) == pattern.charAt(matched)) {
                matched++;
                if (matched == pattern.length()) {
                    return i - matched + 1;
                }
            } else {
                matched = 0;
            }
        }
        return -1;
    }
}
