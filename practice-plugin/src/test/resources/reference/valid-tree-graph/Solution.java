package com.jinloes.practice;

public class Solution {
    public static boolean isTree(int nodeCount, int[][] edges) {
        if (edges.length != nodeCount - 1) {
            return false;
        }
        int[] parent = new int[nodeCount];
        int[] size = new int[nodeCount];
        for (int i = 0; i < nodeCount; i++) {
            parent[i] = i;
            size[i] = 1;
        }
        for (int[] edge : edges) {
            int a = find(parent, edge[0]);
            int b = find(parent, edge[1]);
            if (a == b) {
                return false;
            }
            if (size[a] < size[b]) {
                int swap = a;
                a = b;
                b = swap;
            }
            parent[b] = a;
            size[a] += size[b];
        }
        return true;
    }

    private static int find(int[] parent, int node) {
        while (parent[node] != node) {
            parent[node] = parent[parent[node]];
            node = parent[node];
        }
        return node;
    }
}
