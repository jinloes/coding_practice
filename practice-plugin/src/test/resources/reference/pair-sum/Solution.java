package com.jinloes.practice;

import java.util.HashMap;
import java.util.Map;

public class Solution {
    public static int[] findPair(int[] numbers, int target) {
        Map<Integer, Integer> seen = new HashMap<>();
        for (int index = 0; index < numbers.length; index++) {
            long complement = (long) target - numbers[index];
            if (complement >= Integer.MIN_VALUE && complement <= Integer.MAX_VALUE) {
                Integer previous = seen.get((int) complement);
                if (previous != null) {
                    return new int[]{previous, index};
                }
            }
            seen.putIfAbsent(numbers[index], index);
        }
        return new int[0];
    }
}
