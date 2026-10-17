package com.jinloes.practice;

public class Solution {
    public static int stringToInt(String text) {
        int value = 0;
        for (int i = 0; i < text.length(); i++) if (Character.isDigit(text.charAt(i))) value = value * 10 + text.charAt(i) - '0';
        return value;
    }

    public static String intToString(int value) {
        return value == 0 ? "" : Integer.toString(value);
    }
}
