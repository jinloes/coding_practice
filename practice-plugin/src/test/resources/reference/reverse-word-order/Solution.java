package com.jinloes.practice;

public class Solution {
    public static String reverseWords(String text) {
        String trimmed = text.trim();
        if (trimmed.isEmpty()) return "";
        String[] words = trimmed.split(" +");
        StringBuilder result = new StringBuilder(trimmed.length());
        for (int i = words.length - 1; i >= 0; i--) {
            if (result.length() > 0) result.append(' ');
            result.append(words[i]);
        }
        return result.toString();
    }
}
