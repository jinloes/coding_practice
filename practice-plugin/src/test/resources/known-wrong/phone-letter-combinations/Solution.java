package com.jinloes.practice;

import java.util.ArrayList;
import java.util.List;

public class Solution {
    private static final String[] LETTERS = {"abc", "def", "ghi", "jkl", "mno", "pqr", "tuv", "wxy"};

    public static List<String> combinations(String digits) {
        for (int i = 0; i < digits.length(); i++) {
            char digit = digits.charAt(i);
            if (digit < '2' || digit > '9') {
                throw new IllegalArgumentException("Unsupported digit: " + digit);
            }
        }
        List<String> result = new ArrayList<>();
        if (digits.isEmpty()) {
            return result;
        }
        build(digits, 0, new char[digits.length()], result);
        return result;
    }

    private static void build(String digits, int position, char[] buffer, List<String> result) {
        if (position == digits.length()) {
            result.add(new String(buffer));
            return;
        }
        for (char letter : LETTERS[digits.charAt(position) - '2'].toCharArray()) {
            buffer[position] = letter;
            build(digits, position + 1, buffer, result);
        }
    }
}
