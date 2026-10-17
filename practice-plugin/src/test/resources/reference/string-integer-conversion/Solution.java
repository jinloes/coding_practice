package com.jinloes.practice;

public class Solution {
    public static int stringToInt(String text) {
        boolean negative = text.charAt(0) == '-';
        int value = 0;
        for (int i = negative ? 1 : 0; i < text.length(); i++) value = value * 10 - (text.charAt(i) - '0');
        return negative ? value : -value;
    }

    public static String intToString(int value) {
        if (value == 0) return "0";
        boolean negative = value < 0;
        int remaining = negative ? value : -value;
        StringBuilder digits = new StringBuilder();
        while (remaining != 0) {
            int digit = -(remaining % 10);
            digits.append((char) ('0' + digit));
            remaining /= 10;
        }
        if (negative) digits.append('-');
        return digits.reverse().toString();
    }
}
