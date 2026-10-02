package com.jinloes.practice;

import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

public class Solution {
    public static int[] topKFrequent(int[] numbers, int k) {
        Map<Integer, Integer> counts = new HashMap<>();
        for (int number : numbers) {
            counts.merge(number, 1, Integer::sum);
        }
        PriorityQueue<Map.Entry<Integer, Integer>> heap =
                new PriorityQueue<>(k + 1, (a, b) -> Integer.compare(a.getValue(), b.getValue()));
        for (Map.Entry<Integer, Integer> entry : counts.entrySet()) {
            heap.add(entry);
            if (heap.size() > k) {
                heap.poll();
            }
        }
        int[] result = new int[heap.size()];
        for (int i = 0; i < result.length; i++) {
            result[i] = heap.poll().getKey();
        }
        return result;
    }
}
