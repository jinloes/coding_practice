package com.jinloes.practice;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Solution {
    public static List<List<Integer>> combinations(int[] candidates, int target) {
        int[] sorted = candidates.clone();
        Arrays.sort(sorted);
        int[] values = new int[sorted.length];
        int[] counts = new int[sorted.length];
        int distinct = 0;
        for (int value : sorted) {
            if (distinct > 0 && values[distinct - 1] == value) {
                counts[distinct - 1]++;
            } else {
                values[distinct] = value;
                counts[distinct++] = 1;
            }
        }
        List<List<Integer>> result = new ArrayList<>();
        search(values, counts, distinct, 0, target, new ArrayList<>(), result);
        return result;
    }

    private static void search(int[] values, int[] counts, int distinct, int start, int remaining,
                               List<Integer> path, List<List<Integer>> result) {
        if (remaining == 0) {
            result.add(new ArrayList<>(path));
            return;
        }
        for (int i = start; i < distinct && values[i] <= remaining; i++) {
            int taken = 0;
            while (taken < counts[i] && (taken + 1) * values[i] <= remaining) {
                taken++;
                path.add(values[i]);
                search(values, counts, distinct, i + 1, remaining - taken * values[i], path, result);
            }
            for (int k = 0; k < taken; k++) {
                path.remove(path.size() - 1);
            }
        }
    }
}
