package com.jinloes.practice;

import java.util.ArrayList;
import java.util.List;

public class Solution {
    public static List<String> summarize(int[] numbers) {
        List<String> ranges = new ArrayList<>();
        int index = 0;
        while (index < numbers.length) {
            int start = numbers[index];
            int end = start;
            while (index + 1 < numbers.length && numbers[index + 1] == end + 1) {
                index++;
                end = numbers[index];
            }
            ranges.add(start == end ? String.valueOf(start) : start + "-" + end);
            index++;
        }
        return ranges;
    }
}
