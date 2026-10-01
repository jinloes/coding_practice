package com.jinloes.practice;

import java.util.HashMap;
import java.util.Map;

public class Solution {
    public static int countSubarrays(int[] numbers, int target) {
        Map<Long, Integer> seen = new HashMap<>();
        seen.put(0L, 1);
        long prefix = 0;
        int count = 0;
        for (int value : numbers) {
            prefix += value;
            count += seen.getOrDefault(prefix - target, 0);
            seen.merge(prefix, 1, Integer::sum);
        }
        return count;
    }
}
