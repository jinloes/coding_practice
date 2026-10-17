package com.jinloes.practice;

public class Solution {
    public static int nearestDistance(String[] words) {
        for (int i = 1; i < words.length; i++) if (words[i].equals(words[i - 1])) return 1;
        return -1;
    }
}
