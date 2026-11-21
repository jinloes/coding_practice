package com.jinloes.practice;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Solution {
    private static final class Stats {
        long sum;
        int count;
    }

    private final Map<String, Stats> byAgent = new HashMap<>();

    public Solution() {
    }

    public void recordScore(String agentId, int score) {
        if (agentId == null || agentId.isBlank() || score < 1 || score > 5) {
            throw new IllegalArgumentException("Invalid agent score.");
        }
        Stats stats = byAgent.computeIfAbsent(agentId, ignored -> new Stats());
        stats.sum += score;
        stats.count++;
    }

    public List<String> qualifiedAgents(int minRatings) {
        if (minRatings < 1) {
            throw new IllegalArgumentException("minRatings must be at least 1.");
        }
        return byAgent.entrySet().stream()
                .filter(entry -> entry.getValue().count >= minRatings)
                .sorted((left, right) -> {
                    Stats a = left.getValue();
                    Stats b = right.getValue();
                    int byAverage = Long.compare(b.sum * a.count, a.sum * b.count);
                    if (byAverage != 0) {
                        return byAverage;
                    }
                    int byCount = Integer.compare(b.count, a.count);
                    if (byCount != 0) {
                        return byCount;
                    }
                    return left.getKey().compareTo(right.getKey());
                })
                .map(Map.Entry::getKey)
                .toList();
    }
}
