package com.jinloes.practice;

import java.util.ArrayList;
import java.util.List;

public class Solution {
    public static List<Integer> sunsetViews(List<Integer> heights) {
        List<Integer> result = new ArrayList<>();
        for (int i = 0; i < heights.size(); i++) result.add(i);
        return result;
    }
}
