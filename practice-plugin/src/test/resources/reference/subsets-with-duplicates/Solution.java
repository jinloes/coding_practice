package com.jinloes.practice;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Solution {
    public static List<List<Integer>> subsets(int[] values) {
        int[] sorted = values.clone();
        Arrays.sort(sorted);
        List<List<Integer>> result = new ArrayList<>();
        build(sorted, 0, new ArrayList<>(), result);
        return result;
    }

    private static void build(int[] sorted, int start, List<Integer> path, List<List<Integer>> result) {
        result.add(new ArrayList<>(path));
        for (int i = start; i < sorted.length; i++) {
            if (i > start && sorted[i] == sorted[i - 1]) {
                continue;
            }
            path.add(sorted[i]);
            build(sorted, i + 1, path, result);
            path.remove(path.size() - 1);
        }
    }
}
