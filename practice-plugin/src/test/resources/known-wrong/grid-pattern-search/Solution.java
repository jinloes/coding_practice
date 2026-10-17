package com.jinloes.practice;

public class Solution {
    public static boolean containsPattern(String[] grid, String[] pattern) {
        if (grid.length == 0 || pattern.length == 0) return false;
        for (String row : grid) if (row.contains(pattern[0])) return true;
        return false;
    }
}
