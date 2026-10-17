package com.jinloes.practice;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

public class Solution {
    public record Score(String student, int score) {
    }

    public static String bestThreeAverage(List<Score> records) {
        Map<String, PriorityQueue<Integer>> topScores = new HashMap<>();
        for (Score record : records) {
            PriorityQueue<Integer> heap = topScores.computeIfAbsent(record.student(), ignored -> new PriorityQueue<>());
            heap.offer(record.score());
            if (heap.size() > 3) {
                heap.poll();
            }
        }
        String best = "";
        long bestTotal = Long.MIN_VALUE;
        for (Map.Entry<String, PriorityQueue<Integer>> entry : topScores.entrySet()) {
            if (entry.getValue().size() < 3) {
                continue;
            }
            long total = 0;
            for (int score : entry.getValue()) {
                total += score;
            }
            if (total > bestTotal || (total == bestTotal && (best.isEmpty() || entry.getKey().compareTo(best) < 0))) {
                bestTotal = total;
                best = entry.getKey();
            }
        }
        return best;
    }
}
