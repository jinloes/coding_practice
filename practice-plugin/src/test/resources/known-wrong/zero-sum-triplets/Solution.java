package com.jinloes.practice;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Solution {
    public static List<List<Integer>> findTriplets(int[] numbers) {
        int[] sorted = numbers.clone();
        Arrays.sort(sorted);
        List<List<Integer>> triplets = new ArrayList<>();
        for (int i = 0; i + 2 < sorted.length; i++) {
            int low = i + 1;
            int high = sorted.length - 1;
            while (low < high) {
                long sum = (long) sorted[i] + sorted[low] + sorted[high];
                if (sum < 0) {
                    low++;
                } else if (sum > 0) {
                    high--;
                } else {
                    triplets.add(List.of(sorted[i], sorted[low], sorted[high]));
                    low++;
                    high--;
                }
            }
        }
        return triplets;
    }
}
