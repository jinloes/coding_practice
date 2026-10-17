package com.jinloes.practice;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Solution {
    public static List<Integer> sunsetViews(List<Integer> heights) {
        List<Integer> result = new ArrayList<>();
        int tallestWest = Integer.MIN_VALUE;
        for (int i = heights.size() - 1; i >= 0; i--) {
            int height = heights.get(i);
            if (height > tallestWest) {
                result.add(i);
                tallestWest = height;
            }
        }
        Collections.reverse(result);
        return result;
    }
}
