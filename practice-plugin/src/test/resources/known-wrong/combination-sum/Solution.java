package com.jinloes.practice;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Solution {
    public static List<List<Integer>> combinations(int[] candidates, int target) {
        int[] sorted = candidates.clone();
        Arrays.sort(sorted);
        List<List<Integer>> result = new ArrayList<>();
        search(sorted, target, new ArrayList<>(), result);
        return result;
    }

    private static void search(int[] sorted, int remaining, List<Integer> path,
                               List<List<Integer>> result) {
        if (remaining == 0) {
            result.add(new ArrayList<>(path));
            return;
        }
        for (int i = 0; i < sorted.length && sorted[i] <= remaining; i++) {
            path.add(sorted[i]);
            search(sorted, remaining - sorted[i], path, result);
            path.remove(path.size() - 1);
        }
    }
}
