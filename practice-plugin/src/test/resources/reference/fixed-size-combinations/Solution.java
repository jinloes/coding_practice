package com.jinloes.practice;

import java.util.ArrayList;
import java.util.List;

public class Solution {
    public static List<List<Integer>> combinations(int n, int k) {
        List<List<Integer>> result = new ArrayList<>();
        if (k < 0 || k > n) return result;
        backtrack(1, n, k, new ArrayList<>(), result);
        return result;
    }
    private static void backtrack(int next, int n, int k, List<Integer> current, List<List<Integer>> result) {
        if (current.size() == k) { result.add(new ArrayList<>(current)); return; }
        int needed = k - current.size();
        for (int value = next; value <= n - needed + 1; value++) {
            current.add(value); backtrack(value + 1, n, k, current, result); current.remove(current.size() - 1);
        }
    }
}
