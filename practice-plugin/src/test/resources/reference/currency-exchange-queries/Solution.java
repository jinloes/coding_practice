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
            String from = pairs[i][0];
            String to = pairs[i][1];
            double rate = rates[i];
            graph.computeIfAbsent(from, ignored -> new ArrayList<>()).add(new Edge(to, rate));
            graph.computeIfAbsent(to, ignored -> new ArrayList<>()).add(new Edge(from, 1.0 / rate));
        }

        double[] results = new double[queries.length];
        for (int i = 0; i < queries.length; i++) {
            results[i] = findRate(graph, queries[i][0], queries[i][1]);
        }
        return results;
    }

    private static double findRate(Map<String, List<Edge>> graph, String source, String target) {
        if (!graph.containsKey(source) || !graph.containsKey(target)) {
            return -1.0;
        }
        if (source.equals(target)) {
            return 1.0;
        }
        Set<String> visited = new HashSet<>();
        Queue<Step> queue = new ArrayDeque<>();
        visited.add(source);
        queue.add(new Step(source, 1.0));
        while (!queue.isEmpty()) {
            Step current = queue.poll();
            for (Edge edge : graph.get(current.currency())) {
                double nextRate = current.product() * edge.rate();
                if (edge.to().equals(target)) {
                    return nextRate;
                }
                if (visited.add(edge.to())) {
                    queue.add(new Step(edge.to(), nextRate));
                }
            }
        }
        return -1.0;
    }
}
