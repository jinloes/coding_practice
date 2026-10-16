package com.jinloes.practice;

public class Solution {
    public static boolean containsPermutation(String text, String pattern) {
        boolean[] wanted = new boolean[26];
        for (char c : pattern.toCharArray()) {
            wanted[c - 'a'] = true;
        }
        int length = pattern.length();
        for (int start = 0; start + length <= text.length(); start++) {
            boolean all = true;
            for (int i = start; i < start + length; i++) {
                all &= wanted[text.charAt(i) - 'a'];
            }
            if (all) {
                return true;
            }
        }
        return false;
    }
}
