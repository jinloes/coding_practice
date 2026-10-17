package com.jinloes.practice;

public class Solution {
    public static int[] smallestCover(String[] words, String[] keywords) {
        if (keywords.length == 0) return new int[]{0, -1};
        int first = -1, last = -1;
        for (int i = 0; i < words.length; i++) for (String k : keywords) if (words[i].equals(k)) { if (first < 0) first = i; last = i; }
        return first < 0 ? new int[]{-1, -1} : new int[]{first, last};
    }
}
