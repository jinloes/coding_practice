package com.jinloes.practice;

import java.util.ArrayList;
import java.util.List;

public class Solution {
    public static List<Integer> findStarts(String text, String[] words) {
        List<Integer> result = new ArrayList<>();
        if (words.length == 0) return result;
        String joined = String.join("", words);
        for (int i = 0; i <= text.length() - joined.length(); i++) if (text.startsWith(joined, i)) result.add(i);
        return result;
    }
}
