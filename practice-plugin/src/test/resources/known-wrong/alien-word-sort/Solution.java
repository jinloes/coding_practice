package com.jinloes.practice;

public class Solution {
    public static boolean isSorted(String[] words, String order) {
        for (int i = 1; i < words.length; i++) {
            if (words[i - 1].compareTo(words[i]) > 0) {
                return false;
            }
        }
        return true;
    }
}
