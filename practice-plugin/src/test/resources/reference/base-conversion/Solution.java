package com.jinloes.practice;

public class Solution {
    public static String convert(String number, int fromBase, int toBase) {
        boolean negative = number.startsWith("-"); int start = negative ? 1 : 0; long value = 0;
        for (int i = start; i < number.length(); i++) value = value * fromBase + Character.digit(number.charAt(i), fromBase);
        if (value == 0) return "0";
        String digits = "0123456789ABCDEF"; StringBuilder out = new StringBuilder();
        while (value > 0) { out.append(digits.charAt((int)(value % toBase))); value /= toBase; }
        if (negative) out.append('-');
        return out.reverse().toString();
    }
}
