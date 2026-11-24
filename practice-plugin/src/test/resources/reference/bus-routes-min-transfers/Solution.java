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
        Map<Integer, List<Integer>> stopToRoutes = new HashMap<>();
        for (int routeIndex = 0; routeIndex < routes.length; routeIndex++) {
            for (int stop : routes[routeIndex]) {
                stopToRoutes.computeIfAbsent(stop, ignored -> new ArrayList<>()).add(routeIndex);
            }
        }
        if (!stopToRoutes.containsKey(source) || !stopToRoutes.containsKey(target)) {
            return -1;
        }

        boolean[] visitedRoute = new boolean[routes.length];
        Set<Integer> visitedStops = new HashSet<>();
        Queue<Integer> queue = new ArrayDeque<>();
        visitedStops.add(source);
        queue.add(source);

        int buses = 0;
        while (!queue.isEmpty()) {
            buses++;
            int levelSize = queue.size();
            for (int i = 0; i < levelSize; i++) {
                int stop = queue.poll();
                List<Integer> routeIds = stopToRoutes.get(stop);
                if (routeIds == null) {
                    continue;
                }
                for (int routeId : routeIds) {
                    if (visitedRoute[routeId]) {
                        continue;
                    }
                    visitedRoute[routeId] = true;
                    for (int nextStop : routes[routeId]) {
                        if (nextStop == target) {
                            return buses;
                        }
                        if (visitedStops.add(nextStop)) {
                            queue.add(nextStop);
                        }
                    }
                }
            }
        }
        return -1;
    }
}
