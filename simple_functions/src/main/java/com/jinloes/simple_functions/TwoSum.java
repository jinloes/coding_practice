package com.jinloes.simple_functions;

import javafx.util.Pair;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * You have an unsorted array, and you are given a value S. Find all pairs of elements in the array
 * that add up to value S.
 */
public class TwoSum {
    public static Set<Pair<Integer, Integer>> find2Sum(int[] arr, int sum) {
        if (arr == null || arr.length < 2) {
            return new HashSet<>();
        }
        Set<Pair<Integer, Integer>> pairs = new HashSet<>();
        Map<Integer, Integer> sumCount = new HashMap<>();
        for (Integer val : arr) {
            if (!sumCount.containsKey(val)) {
                sumCount.put(val, 1);
            } else {
                sumCount.put(val, sumCount.get(val) + 1);
            }
        }
        for (Integer val : arr) {
            int difference = sum - val;
            if (sumCount.containsKey(difference) && difference != val) {
                int min = Math.min(val, difference);
                int max = Math.max(val, difference);
                pairs.add(new Pair<>(min, max));
            } else if (sumCount.containsKey(difference)
                    && difference == val
                    && sumCount.get(difference) > 1) {
                int min = Math.min(val, difference);
                int max = Math.max(val, difference);
                pairs.add(new Pair<>(min, max));
            }
        }
        return pairs;
    }
}
