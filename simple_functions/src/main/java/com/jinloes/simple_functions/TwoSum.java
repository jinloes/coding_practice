package com.jinloes.simple_functions;


import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * You have an unsorted array, and you are given a value S. Find all pairs of elements in the array
 * that add up to value S.
 */
public class TwoSum {
    private record Pair(int v1, int v2){
        public static Pair of(int v1, int v2) {
            return new Pair(v1, v2);
        }
    }
    public static Set<Pair> find2Sum(int[] arr, int sum) {
        if (arr == null || arr.length < 2) {
            return new HashSet<>();
        }
        Set<Pair> pairs = new HashSet<>();
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
                pairs.add(Pair.of(min, max));
            } else if (sumCount.containsKey(difference)
                    && difference == val
                    && sumCount.get(difference) > 1) {
                int min = Math.min(val, difference);
                int max = Math.max(val, difference);
                pairs.add(Pair.of(min, max));
            }
        }
        return pairs;
    }
}
