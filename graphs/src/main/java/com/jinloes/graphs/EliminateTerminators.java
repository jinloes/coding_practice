package com.jinloes.graphs;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

/**
 * Utilities for topological operations.
 */
public class EliminateTerminators {

    /**
     * Performs a topological sort over nodes 0..n-1 given dependency pairs.
     * Each pair deps[i] should have length >= 2 and represent (a -> b) meaning a must come before b.
     *
     * @param n    total number of nodes (must be > 0)
     * @param deps dependency pairs (may be null)
     * @return a topologically sorted list of all nodes, or an empty list if a cycle is detected
     * @throws IllegalArgumentException if n <= 0 or any pair references a node outside [0, n-1]
     */
    public static List<Integer> topologicalSort(int n, int[][] deps) {
        if (n <= 0) {
            throw new IllegalArgumentException("n must be > 0");
        }

        List<Integer> order = new ArrayList<>(n);
        List<List<Integer>> adj = new ArrayList<>(n);
        int[] indegree = new int[n];

        for (int i = 0; i < n; i++) {
            adj.add(new ArrayList<>());
        }

        if (deps != null) {
            for (int[] pair : deps) {
                if (pair == null || pair.length < 2) {
                    continue;
                }
                int a = pair[0];
                int b = pair[1];
                if (a < 0 || a >= n || b < 0 || b >= n) {
                    throw new IllegalArgumentException("dependency pair contains node outside range 0.." + (n - 1));
                }
                adj.get(a).add(b);
                indegree[b]++;
            }
        }

        Deque<Integer> q = new ArrayDeque<>();
        for (int i = 0; i < n; i++) {
            if (indegree[i] == 0) {
                q.addLast(i);
            }
        }

        while (!q.isEmpty()) {
            int u = q.removeFirst();
            order.add(u);
            for (int v : adj.get(u)) {
                indegree[v]--;
                if (indegree[v] == 0) {
                    q.addLast(v);
                }
            }
        }

        if (order.size() != n) {
            return new ArrayList<>(); // cycle detected
        }
        return order;
    }
}
