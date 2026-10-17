package com.jinloes.practice;

import java.util.List;

public class Solution {
    public static int[] sortItems(int itemCount, int groupCount, int[] groups, List<List<Integer>> beforeItems) {
        int[] result = new int[itemCount];
        for (int i = 0; i < itemCount; i++) result[i] = i;
        return result;
    }
}
