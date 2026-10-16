package com.jinloes.practice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Solution {
    private static final class History {
        final List<Integer> times = new ArrayList<>();
        final List<String> values = new ArrayList<>();
    }

    private final Map<String, History> histories = new HashMap<>();

    public void set(String key, String value, int time) {
        History history = histories.computeIfAbsent(key, k -> new History());
        history.times.add(time);
        history.values.add(value);
    }

    public String get(String key, int time) {
        History history = histories.get(key);
        if (history == null) {
            return "";
        }
        int low = 0;
        int high = history.times.size();
        while (low < high) {
            int mid = (low + high) >>> 1;
            if (history.times.get(mid) <= time) {
                low = mid + 1;
            } else {
                high = mid;
            }
        }
        return low == 0 ? "" : history.values.get(low - 1);
    }
}
