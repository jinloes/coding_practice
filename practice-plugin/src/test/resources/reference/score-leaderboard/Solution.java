package com.jinloes.practice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Solution {
    private final Map<Integer, Integer> scores = new HashMap<>();
    public void addScore(int playerId, int score) { scores.merge(playerId, score, Integer::sum); }
    public int top(int k) { if (k <= 0) return 0; List<Integer> values = new ArrayList<>(scores.values()); values.sort((a, b) -> Integer.compare(b, a)); int sum = 0; for (int i = 0; i < Math.min(k, values.size()); i++) sum += values.get(i); return sum; }
    public void reset(int playerId) { scores.remove(playerId); }
}
