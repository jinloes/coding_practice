package com.jinloes.practice;

import java.util.HashSet;
import java.util.Set;

public class Solution {
    public static boolean hasDuplicate(int[] numbers) {
        Set<Integer> seen = new HashSet<>();
        for (int value : numbers) {
            if (!seen.add(value)) {
                return true;
            }
        }
        return false;
    }
}
