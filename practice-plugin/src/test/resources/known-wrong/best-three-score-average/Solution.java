package com.jinloes.practice;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Solution {
    public record Score(String student, int score) {
    }

    public static String bestThreeAverage(List<Score> records) {
        Map<String, Integer> totals = new HashMap<>();
        for (Score record : records) {
            totals.merge(record.student(), record.score(), Integer::sum);
        }
        return totals.entrySet().stream().max(Map.Entry.comparingByValue()).map(Map.Entry::getKey).orElse("");
    }
}
