package com.jinloes.practice;

public class Solution {
    public static int countComponents(int nodeCount, int[][] edges) {
        int[] parent = new int[nodeCount];
        int[] size = new int[nodeCount];
        for (int i = 0; i < nodeCount; i++) {
            parent[i] = i;
            size[i] = 1;
        }
        int components = nodeCount;
        for (int[] edge : edges) {
            int a = find(parent, edge[0]);
            int b = find(parent, edge[1]);
            if (a == b) {
                continue;
            }
            if (size[a] < size[b]) {
                int swap = a;
                a = b;
                b = swap;
            }
            parent[b] = a;
            size[a] += size[b];
            components--;
        }
        return components;
    }

    private static int find(int[] parent, int node) {
        while (parent[node] != node) {
            parent[node] = parent[parent[node]];
            node = parent[node];
        }
        return node;
    }
}
