package com.jinloes.practice;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class Solution {
    private Solution() {
    }

    public static List<List<String>> suggest(String[] products, String query) {
        String[] sorted = products.clone();
        Arrays.sort(sorted);
        List<List<String>> result = new ArrayList<>(query.length());
        int left = 0;
        int right = sorted.length - 1;
        for (int i = 0; i < query.length(); i++) {
            char ch = query.charAt(i);
            while (left <= right && (sorted[left].length() <= i || sorted[left].charAt(i) != ch)) {
                left++;
            }
            while (left <= right && (sorted[right].length() <= i || sorted[right].charAt(i) != ch)) {
                right--;
            }
            int limit = Math.min(3, right - left + 1);
            List<String> matches = new ArrayList<>(Math.max(0, limit));
            for (int k = 0; k < limit; k++) {
                matches.add(sorted[left + k]);
            }
            result.add(matches);
        }
        return result;
    }
}
