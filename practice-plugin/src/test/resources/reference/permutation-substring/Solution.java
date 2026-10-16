package com.jinloes.practice;

public class Solution {
    public static boolean containsPermutation(String text, String pattern) {
        int length = pattern.length();
        if (length > text.length()) {
            return false;
        }
        int[] balance = new int[26];
        for (int i = 0; i < length; i++) {
            balance[pattern.charAt(i) - 'a']++;
            balance[text.charAt(i) - 'a']--;
        }
        int off = 0;
        for (int count : balance) {
            if (count != 0) {
                off++;
            }
        }
        if (off == 0) {
            return true;
        }
        for (int i = length; i < text.length(); i++) {
            off += shift(balance, text.charAt(i) - 'a', -1);
            off += shift(balance, text.charAt(i - length) - 'a', 1);
            if (off == 0) {
                return true;
            }
        }
        return false;
    }

    private static int shift(int[] balance, int letter, int delta) {
        int change = balance[letter] == 0 ? 1 : 0;
        balance[letter] += delta;
        if (balance[letter] == 0) {
            change = -1;
        }
        return change;
    }
}
