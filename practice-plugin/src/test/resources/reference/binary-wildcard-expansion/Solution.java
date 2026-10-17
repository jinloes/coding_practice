package com.jinloes.practice;

import java.util.ArrayList;
import java.util.List;

public class Solution {
    public static List<String> expand(String pattern) { List<String> result = new ArrayList<>(); char[] chars = pattern.toCharArray(); backtrack(chars, 0, result); return result; }
    private static void backtrack(char[] chars, int index, List<String> result) {
        if (index == chars.length) { result.add(new String(chars)); return; }
        if (chars[index] != '?') { backtrack(chars, index + 1, result); return; }
        chars[index] = '0'; backtrack(chars, index + 1, result); chars[index] = '1'; backtrack(chars, index + 1, result); chars[index] = '?';
    }
}
