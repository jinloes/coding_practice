package com.jinloes.practice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Solution {
    public static int[] topKFrequent(int[] numbers, int k) {
        Map<Integer, Integer> counts = new HashMap<>();
        for (int number : numbers) {
            counts.merge(number, 1, Integer::sum);
        }
        List<Map.Entry<Integer, Integer>> entries = new ArrayList<>(counts.entrySet());
        entries.sort((a, b) -> Integer.compare(a.getValue(), b.getValue()));
        int[] result = new int[k];
        for (int i = 0; i < k; i++) {
            result[i] = entries.get(i).getKey();
        }
        return result;
    }
}
