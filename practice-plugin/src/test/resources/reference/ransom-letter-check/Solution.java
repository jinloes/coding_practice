package com.jinloes.practice;

public class Solution {
    public static boolean canConstruct(String letterText, String magazineText) {
        int[] counts = new int[Character.MAX_VALUE + 1];
        for (int i = 0; i < magazineText.length(); i++) counts[magazineText.charAt(i)]++;
        for (int i = 0; i < letterText.length(); i++) if (--counts[letterText.charAt(i)] < 0) return false;
        return true;
    }
}
