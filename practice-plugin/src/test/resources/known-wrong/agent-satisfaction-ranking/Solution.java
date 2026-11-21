package com.jinloes.practice;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Solution {
    private final Map<String, int[]> byAgent = new HashMap<>();

    public Solution() {
    }

    public void recordScore(String agentId, int score) {
        int[] stats = byAgent.computeIfAbsent(agentId, ignored -> new int[2]);
        stats[0] += score;
        stats[1]++;
    }

    public List<String> qualifiedAgents(int minRatings) {
        return byAgent.entrySet().stream()
                .filter(entry -> entry.getValue()[1] >= minRatings)
                .sorted(Comparator.<Map.Entry<String, int[]>>comparingInt(
                        entry -> -(entry.getValue()[0] / entry.getValue()[1]))
                        .thenComparing(Map.Entry::getKey))
                .map(Map.Entry::getKey)
                .toList();
    }
}
