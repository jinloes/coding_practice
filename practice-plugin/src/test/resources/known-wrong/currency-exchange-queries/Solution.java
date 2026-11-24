package com.jinloes.practice;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

public class Solution {
    private record Edge(String to, double rate) {
    }

    private record Step(String currency, double product) {
    }

    public double[] evaluateRates(String[][] pairs, double[] rates, String[][] queries) {
        Map<String, List<Edge>> graph = new HashMap<>();
        for (int i = 0; i < pairs.length; i++) {
            // Bug: only adds forward edges, omitting reverse 1.0 / rate edges.
            graph.computeIfAbsent(pairs[i][0], ignored -> new ArrayList<>())
                    .add(new Edge(pairs[i][1], rates[i]));
            graph.computeIfAbsent(pairs[i][1], ignored -> new ArrayList<>());
        }
        double[] results = new double[queries.length];
        for (int i = 0; i < queries.length; i++) {
            String src = queries[i][0];
            String dst = queries[i][1];
            if (!graph.containsKey(src) || !graph.containsKey(dst)) {
                results[i] = -1.0;
                continue;
            }
            if (src.equals(dst)) {
                results[i] = 1.0;
                continue;
            }
            results[i] = bfs(graph, src, dst);
        }
        return results;
    }

    private static double bfs(Map<String, List<Edge>> graph, String src, String dst) {
        Set<String> visited = new HashSet<>();
        Queue<Step> queue = new ArrayDeque<>();
        visited.add(src);
        queue.add(new Step(src, 1.0));
        while (!queue.isEmpty()) {
            Step current = queue.poll();
            for (Edge edge : graph.get(current.currency())) {
                double next = current.product() * edge.rate();
                if (edge.to().equals(dst)) {
                    return next;
                }
                if (visited.add(edge.to())) {
                    queue.add(new Step(edge.to(), next));
                }
            }
        }
        return -1.0;
    }
}
