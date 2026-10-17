package com.jinloes.practice;

public class Solution {
    public static boolean canConstruct(String letterText, String magazineText) {
        for (int i = 0; i < letterText.length(); i++) if (magazineText.indexOf(letterText.charAt(i)) < 0) return false;
        return true;
    }
}
