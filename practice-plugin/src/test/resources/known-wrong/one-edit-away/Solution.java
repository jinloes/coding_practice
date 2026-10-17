package com.jinloes.practice;

public class Solution {
    public static boolean isOneEditAway(String first, String second) {
        return Math.abs(first.length() - second.length()) <= 1;
    }
}
