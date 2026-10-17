package com.jinloes.practice;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Solution {
    public static List<List<Integer>> uniquePermutations(int[] values) {
        int[] sorted = values.clone();
        Arrays.sort(sorted);
        List<List<Integer>> result = new ArrayList<>();
        backtrack(sorted, new boolean[sorted.length], new ArrayList<>(), result);
        return result;
    }
    private static void backtrack(int[] values, boolean[] used, List<Integer> current, List<List<Integer>> result) {
        if (current.size() == values.length) { result.add(new ArrayList<>(current)); return; }
        for (int i = 0; i < values.length; i++) {
            if (used[i] || (i > 0 && values[i] == values[i - 1] && !used[i - 1])) continue;
            used[i] = true; current.add(values[i]);
            backtrack(values, used, current, result);
            current.remove(current.size() - 1); used[i] = false;
        }
    }
}
