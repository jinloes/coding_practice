package com.jinloes.practice;

public class Solution {
    public static int[] processingOrder(int[][] tasks) {
        int[] order = new int[tasks.length];
        for (int i = 0; i < order.length; i++) order[i] = i;
        return order;
    }
}
