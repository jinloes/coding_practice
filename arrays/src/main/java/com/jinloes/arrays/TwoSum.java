package com.jinloes.arrays;


import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * You have an unsorted array, and you are given a value S. Find all pairs of elements in the array
 * that add up to value S.
 */
public class TwoSum {
    public record Pair(int v1, int v2) {
        public static Pair of(int v1, int v2) {
            return new Pair(v1, v2);
        }
    }
    public static Set<Pair> find2Sum(int[] arr, int sum) {
        if (arr == null || arr.length < 2) {
            return new HashSet<>();
        }

        Map<Integer, Integer> counts = new HashMap<>();
        for (int value : arr) {
            counts.merge(value, 1, Integer::sum);
        }

        Set<Pair> pairs = new HashSet<>();
        for (int value : counts.keySet()) {
            int complement = sum - value;
            if (!counts.containsKey(complement)) {
                continue;
            }

            if (value < complement) {
                pairs.add(Pair.of(value, complement));
            } else if (value == complement && counts.get(value) > 1) {
                pairs.add(Pair.of(value, value));
            }
        }
        return pairs;
    }
}
