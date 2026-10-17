package com.jinloes.practice;

public class Solution {
    public static boolean canFormSquare(int[] matchsticks) {
        int total=0; for (int s: matchsticks) total += s;
        return matchsticks.length >= 4 && total % 4 == 0;
    }
}
