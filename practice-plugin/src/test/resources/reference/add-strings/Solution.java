package com.jinloes.practice;

public class Solution {
    public static String addStrings(String first, String second) {
        StringBuilder sum = new StringBuilder(Math.max(first.length(), second.length()) + 1);
        int i = first.length() - 1;
        int j = second.length() - 1;
        int carry = 0;
        while (i >= 0 || j >= 0 || carry > 0) {
            int column = carry;
            if (i >= 0) {
                column += first.charAt(i--) - '0';
            }
            if (j >= 0) {
                column += second.charAt(j--) - '0';
            }
            sum.append((char) ('0' + column % 10));
            carry = column / 10;
        }
        return sum.reverse().toString();
    }
}
