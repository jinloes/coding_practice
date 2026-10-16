package com.jinloes.practice;

import java.util.ArrayList;
import java.util.List;

public class Solution {
    public static List<Integer> partitionSizes(String text) {
        int[] last = new int[26];
        for (int i = 0; i < text.length(); i++) {
            last[text.charAt(i) - 'a'] = i;
        }
        List<Integer> sizes = new ArrayList<>();
        int start = 0;
        while (start < text.length()) {
            int end = last[text.charAt(start) - 'a'];
            sizes.add(end - start + 1);
            start = end + 1;
        }
        return sizes;
    }
}
