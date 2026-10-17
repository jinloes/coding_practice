package com.jinloes.practice;

import java.util.HashMap;
import java.util.Map;

public class Solution {
    private final Map<String, Integer> counts = new HashMap<>();

    public void inc(String key) { counts.merge(key, 1, Integer::sum); }
    public void dec(String key) { counts.computeIfPresent(key, (k, v) -> v - 1); }
    public String getMaxKey() { return counts.keySet().stream().findFirst().orElse(""); }
    public String getMinKey() { return counts.keySet().stream().findFirst().orElse(""); }
}
