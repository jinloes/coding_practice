package com.jinloes.practice;

public class Solution {
    public static boolean isOneEditAway(String first, String second) {
        if (Math.abs(first.length() - second.length()) > 1) return false;
        String shorter = first.length() <= second.length() ? first : second;
        String longer = first.length() <= second.length() ? second : first;
        int i = 0, j = 0;
        boolean edited = false;
        while (i < shorter.length() && j < longer.length()) {
            if (shorter.charAt(i) == longer.charAt(j)) { i++; j++; continue; }
            if (edited) return false;
            edited = true;
            if (shorter.length() == longer.length()) i++;
            j++;
        }
        return true;
    }
}
