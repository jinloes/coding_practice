package com.jinloes.practice;

import java.util.Arrays;

public class Solution {
    public static int minBoats(int[] people, int limit) {
        int[] weights = people.clone();
        Arrays.sort(weights);
        int left = 0, right = weights.length - 1, boats = 0;
        while (left <= right) {
            if (weights[left] + weights[right] <= limit) left++;
            right--;
            boats++;
        }
        return boats;
    }
}
