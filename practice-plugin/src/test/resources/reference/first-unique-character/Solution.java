package com.jinloes.practice;

public class Solution {
    public static int firstUniqueIndex(String word) {
        int[] counts = new int[26];
        for (int index = 0; index < word.length(); index++) {
            counts[word.charAt(index) - 'a']++;
        }
        for (int index = 0; index < word.length(); index++) {
            if (counts[word.charAt(index) - 'a'] == 1) {
                return index;
            }
        }
        return -1;
    }
}
