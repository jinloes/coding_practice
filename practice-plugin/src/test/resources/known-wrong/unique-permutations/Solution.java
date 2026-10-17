package com.jinloes.practice;

import java.util.ArrayList;
import java.util.List;

public class Solution {
    public static List<List<Integer>> uniquePermutations(int[] values) {
        List<Integer> one = new ArrayList<>();
        for (int value : values) one.add(value);
        return List.of(one);
    }
}
