package com.jinloes.practice;

import java.util.HashMap;
import java.util.Map;

public class Solution {
    private final Map<Integer,Integer> counts = new HashMap<>();
    public Solution() {}
    public void increasePopularity(int id) { counts.put(id, counts.getOrDefault(id,0)+1); }
    public void decreasePopularity(int id) { counts.put(id, counts.getOrDefault(id,0)-1); }
    public int mostPopular() { return counts.keySet().stream().findFirst().orElse(-1); }
}
