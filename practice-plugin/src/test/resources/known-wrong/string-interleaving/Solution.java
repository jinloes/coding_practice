package com.jinloes.practice;

public class Solution {
    public static boolean isInterleaving(String first, String second, String merged) {
        if (first.length() + second.length() != merged.length()) {
            return false;
        }
        int i = 0;
        int j = 0;
        for (int k = 0; k < merged.length(); k++) {
            char c = merged.charAt(k);
            if (i < first.length() && first.charAt(i) == c) {
                i++;
            } else if (j < second.length() && second.charAt(j) == c) {
                j++;
            } else {
                return false;
            }
        }
        return true;
    }
}
