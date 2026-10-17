package com.jinloes.practice;

public class Solution {
    public static boolean isSorted(String[] words, String order) {
        int[] rank = new int[Character.MAX_VALUE + 1];
        for (int i = 0; i < order.length(); i++) {
            rank[order.charAt(i)] = i;
        }
        for (int i = 1; i < words.length; i++) {
            if (compare(words[i - 1], words[i], rank) > 0) {
                return false;
            }
        }
        return true;
    }

    private static int compare(String left, String right, int[] rank) {
        int limit = Math.min(left.length(), right.length());
        for (int i = 0; i < limit; i++) {
            int difference = rank[left.charAt(i)] - rank[right.charAt(i)];
            if (difference != 0) {
                return difference;
            }
        }
        return left.length() - right.length();
    }
}
