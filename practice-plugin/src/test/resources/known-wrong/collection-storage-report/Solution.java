package com.jinloes.practice;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Solution {
    private final Map<String, Long> totals = new HashMap<>();

    public Solution() {
    }

    public void putFile(String fileName, long sizeBytes, List<String> collections) {
        for (String name : collections) {
            totals.merge(name, sizeBytes, Long::sum);
        }
    }

    public long totalBytes() {
        return totals.values().stream().mapToLong(Long::longValue).sum();
    }

    public List<String> topCollections(int limit) {
        return totals.entrySet().stream()
                .sorted(Comparator.<Map.Entry<String, Long>>comparingLong(Map.Entry::getValue).reversed())
                .limit(limit)
                .map(Map.Entry::getKey)
                .toList();
    }
}
