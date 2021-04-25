package com.jinloes.simple_functions.dynamic_programming;

import java.util.HashMap;
import java.util.Map;

/**
 * A robot is located at the top-left corner of a m x n grid (marked 'Start' in the diagram below).
 * <p>
 * The robot can only move either down or right at any point in time.
 * The robot is trying to reach the bottom-right corner of the grid (marked 'Finish' in the diagram below).
 * <p>
 * How many possible unique paths are there?
 */
public class UniquePaths {

    public int uniquePaths(int m, int n) {
        Map<Integer, Map<Integer, Integer>> memo = new HashMap<>();

        return uniquePaths(m, n, memo);
    }

    private int uniquePaths(int m, int n, Map<Integer, Map<Integer, Integer>> memo) {
        if (m == 1 && n == 1) {
            return 1;
        } else if (m == 0 || n == 0) {
            return 0;
        }

        if (memo.get(m) != null && memo.get(m).containsKey(n)) {
            return memo.get(m).get(n);
        }

        int sum = uniquePaths(m - 1, n, memo) + uniquePaths(m, n - 1, memo);
        memo.putIfAbsent(m, new HashMap<>());
        memo.get(m).put(n, sum);

        return sum;
    }
}
