package com.jinloes.practice;

import java.util.LinkedHashMap;
import java.util.Map;

public class Solution {
    private final int capacity;
    // Bug: evicts by LRU only, ignoring access frequency.
    private final LinkedHashMap<Integer, Integer> cache;

    public Solution(int capacity) {
        this.capacity = capacity;
        this.cache = new LinkedHashMap<>(16, 0.75f, true);
    }

    public int get(int key) {
        return cache.getOrDefault(key, -1);
    }

    public void put(int key, int value) {
        if (capacity <= 0) {
            return;
        }
        if (!cache.containsKey(key) && cache.size() == capacity) {
            Integer oldest = cache.keySet().iterator().next();
            cache.remove(oldest);
        }
        cache.put(key, value);
    }
}
