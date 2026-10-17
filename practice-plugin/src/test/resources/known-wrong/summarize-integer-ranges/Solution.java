package com.jinloes.practice;

import java.util.ArrayList;
import java.util.List;

public class Solution {
    public static List<String> summarize(int[] numbers) {
        List<String> result = new ArrayList<>();
        for (int number : numbers) {
            result.add(String.valueOf(number));
        }
        return result;
    }
}
