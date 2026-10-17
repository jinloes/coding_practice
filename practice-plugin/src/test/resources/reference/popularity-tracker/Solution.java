package com.jinloes.practice;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Solution {
    private final Map<Integer, Integer> counts = new HashMap<>();
    private final Map<Integer, Set<Integer>> idsByCount = new HashMap<>();
    private int maxCount;
    public Solution() {}
    public void increasePopularity(int id) { move(id, counts.getOrDefault(id, 0), counts.getOrDefault(id, 0) + 1); }
    public void decreasePopularity(int id) { Integer count = counts.get(id); if (count != null) move(id, count, count - 1); }
    public int mostPopular() { return maxCount == 0 ? -1 : idsByCount.get(maxCount).iterator().next(); }
    private void move(int id, int oldCount, int newCount) {
        if (oldCount > 0) {
            Set<Integer> old = idsByCount.get(oldCount); old.remove(id); if (old.isEmpty()) idsByCount.remove(oldCount);
        }
        if (newCount == 0) counts.remove(id); else { counts.put(id, newCount); idsByCount.computeIfAbsent(newCount, ignored -> new HashSet<>()).add(id); }
        if (newCount > maxCount) maxCount = newCount;
        while (maxCount > 0 && !idsByCount.containsKey(maxCount)) maxCount--;
    }
}
