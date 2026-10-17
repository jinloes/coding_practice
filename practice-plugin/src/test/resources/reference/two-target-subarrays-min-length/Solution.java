package com.jinloes.practice;

import java.util.HashMap;
import java.util.Map;

public class Solution {
    public static int minSumOfLengths(int[] numbers, int target) {
        int n = numbers.length;
        int[] bestBefore = new int[n];
        int best = Integer.MAX_VALUE / 4, answer = Integer.MAX_VALUE / 4;
        Map<Long, Integer> last = new HashMap<>();
        last.put(0L, -1);
        long prefix = 0;
        for (int i = 0; i < n; i++) {
            prefix += numbers[i];
            Integer start = last.get(prefix - target);
            if (start != null) {
                int length = i - start;
                if (start >= 0 && bestBefore[start] < Integer.MAX_VALUE / 4) answer = Math.min(answer, bestBefore[start] + length);
                best = Math.min(best, length);
            }
            bestBefore[i] = best;
            last.put(prefix, i);
        }
        return answer >= Integer.MAX_VALUE / 4 ? -1 : answer;
    }
}
