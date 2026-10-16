package com.jinloes.practice;

import java.util.LinkedHashMap;
import java.util.Map;

public class Solution {
    private final int capacity;
    private final LinkedHashMap<Integer, Integer> map = new LinkedHashMap<>();

    public Solution(int capacity) {
        if (capacity < 1) {
            throw new IllegalArgumentException("capacity must be at least 1");
        }
        this.capacity = capacity;
    }

    public int get(int key) {
        return map.getOrDefault(key, -1);
    }

    public void put(int key, int value) {
        if (!map.containsKey(key) && map.size() == capacity) {
            Integer eldest = map.keySet().iterator().next();
            map.remove(eldest);
        }
        map.put(key, value);
    }
}
