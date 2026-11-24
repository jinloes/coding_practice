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
    public int minBuses(int[][] routes, int source, int target) {
        if (source == target) {
            return 0;
        }
        // Bug: counts stop-to-stop hops instead of bus boardings.
        Map<Integer, List<Integer>> graph = new HashMap<>();
        for (int[] route : routes) {
            for (int i = 0; i < route.length; i++) {
                int a = route[i];
                int b = route[(i + 1) % route.length];
                graph.computeIfAbsent(a, ignored -> new ArrayList<>()).add(b);
                graph.computeIfAbsent(b, ignored -> new ArrayList<>()).add(a);
            }
        }
        Set<Integer> visited = new HashSet<>();
        Queue<Integer> queue = new ArrayDeque<>();
        visited.add(source);
        queue.add(source);
        int steps = 0;
        while (!queue.isEmpty()) {
            steps++;
            int size = queue.size();
            for (int i = 0; i < size; i++) {
                for (int next : graph.getOrDefault(queue.poll(), List.of())) {
                    if (next == target) {
                        return steps;
                    }
                    if (visited.add(next)) {
                        queue.add(next);
                    }
                }
            }
        }
        return -1;
    }
}
