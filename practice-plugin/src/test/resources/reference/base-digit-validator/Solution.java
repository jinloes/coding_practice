package com.jinloes.practice;

public class Solution {
    public static boolean isValidForBase(String digits, int base) {
        if (digits == null || digits.isEmpty() || base < 2 || base > 36) return false;
        int start = 0;
        if (digits.startsWith("0x") || digits.startsWith("0X")) {
            if (base != 16 || digits.length() == 2) return false;
            start = 2;
        }
        for (int j = start; j < digits.length(); j++) if (Character.digit(digits.charAt(j), base) < 0) return false;
        return true;
    }
}
