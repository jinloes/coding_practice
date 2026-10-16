package com.jinloes.practice;

public class Solution {
    public static int minCostToTop(int[] cost) {
        int twoBack = Integer.MAX_VALUE / 2;
        int oneBack = 0;
        for (int toll : cost) {
            int here = toll + Math.min(oneBack, twoBack);
            twoBack = oneBack;
            oneBack = here;
        }
        return oneBack;
    }
}
