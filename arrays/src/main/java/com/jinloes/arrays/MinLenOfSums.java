package com.jinloes.arrays;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Given an array of integers arr and an integer target.
 * <p>
 * You have to find two non-overlapping sub-arrays of arr each with a sum equal target. There can be multiple answers so you have to find an answer where the sum of the lengths of the two sub-arrays is minimum.
 * <p>
 * Return the minimum sum of the lengths of the two required sub-arrays, or return -1 if you cannot find such two sub-arrays.
 */
public class MinLenOfSums {

    public int minSumOfLengths(int[] arr, int target) {
        if (arr == null || arr.length == 0) {
            return -1;
        }

        int[] best = new int[arr.length];
        Arrays.fill(best, Integer.MAX_VALUE);

        Map<Integer, Integer> prefixToIndex = new HashMap<>();
        prefixToIndex.put(0, -1);

        int prefix = 0;
        int answer = Integer.MAX_VALUE;

        for (int i = 0; i < arr.length; i++) {
            prefix += arr[i];
            best[i] = i > 0 ? best[i - 1] : Integer.MAX_VALUE;

            Integer start = prefixToIndex.get(prefix - target);
            if (start != null) {
                int currentLength = i - start;
                best[i] = Math.min(best[i], currentLength);
                if (start >= 0 && best[start] != Integer.MAX_VALUE) {
                    answer = Math.min(answer, currentLength + best[start]);
                }
            }

            prefixToIndex.put(prefix, i);
        }

        return answer == Integer.MAX_VALUE ? -1 : answer;
    }
}
