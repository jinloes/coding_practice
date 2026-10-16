package com.jinloes.practice;

import java.util.ArrayList;
import java.util.List;

public class Solution {
    public static List<List<Integer>> permutations(int[] values) {
        List<List<Integer>> result = new ArrayList<>();
        build(values, new boolean[values.length], new ArrayList<>(), result);
        return result;
    }

    private static void build(int[] values, boolean[] used, List<Integer> path, List<List<Integer>> result) {
        if (path.size() == values.length) {
            result.add(new ArrayList<>(path));
            return;
        }
        for (int i = 0; i < values.length; i++) {
            if (used[i]) {
                continue;
            }
            used[i] = true;
            path.add(values[i]);
            build(values, used, path, result);
            path.remove(path.size() - 1);
        }
    }
}
