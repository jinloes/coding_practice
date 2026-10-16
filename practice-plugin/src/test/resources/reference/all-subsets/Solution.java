package com.jinloes.practice;

import java.util.ArrayList;
import java.util.List;

public class Solution {
    public static List<List<Integer>> subsets(int[] values) {
        List<List<Integer>> result = new ArrayList<>();
        result.add(new ArrayList<>());
        for (int value : values) {
            int existing = result.size();
            for (int i = 0; i < existing; i++) {
                List<Integer> copy = new ArrayList<>(result.get(i));
                copy.add(value);
                result.add(copy);
            }
        }
        return result;
    }
}
