package com.jinloes.practice;

public class Solution {
    public static int countComponents(int nodeCount, int[][] edges) {
        return nodeCount - edges.length;
    }
}
