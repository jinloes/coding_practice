package com.jinloes.practice;

public class Solution {
    public static String longestCommonPrefix(String[] words) {
        if (words.length == 0) {
            return "";
        }
        String first = words[0];
        for (int column = 0; column < first.length(); column++) {
            char expected = first.charAt(column);
            for (int row = 1; row < words.length; row++) {
                String word = words[row];
                if (column == word.length() || word.charAt(column) != expected) {
                    return first.substring(0, column);
                }
            }
        }
        return first;
    }
}
