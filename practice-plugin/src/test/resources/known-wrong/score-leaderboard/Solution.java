package com.jinloes.practice;

import java.util.HashMap;
import java.util.Map;

public class Solution {
    private final Map<Integer, Integer> scores = new HashMap<>();
    public void addScore(int playerId, int score) { scores.put(playerId, score); }
    public int top(int k) { return scores.values().stream().limit(Math.max(0, k)).mapToInt(Integer::intValue).sum(); }
    public void reset(int playerId) { scores.remove(playerId); }
}
