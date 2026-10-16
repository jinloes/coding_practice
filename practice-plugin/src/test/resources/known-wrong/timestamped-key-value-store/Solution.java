package com.jinloes.practice;

import java.util.HashMap;
import java.util.Map;

public class Solution {
    private final Map<String, Map<Integer, String>> valuesByKey = new HashMap<>();

    public void set(String key, String value, int time) {
        valuesByKey.computeIfAbsent(key, k -> new HashMap<>()).put(time, value);
    }

    public String get(String key, int time) {
        Map<Integer, String> history = valuesByKey.get(key);
        if (history == null) {
            return "";
        }
        return history.getOrDefault(time, "");
    }
}
