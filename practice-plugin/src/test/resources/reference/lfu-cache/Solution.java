package com.jinloes.practice;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;

public class Solution {
    private final int capacity;
    private final Map<Integer, Integer> values = new HashMap<>();
    private final Map<Integer, Integer> freqs = new HashMap<>();
    private final Map<Integer, LinkedHashSet<Integer>> buckets = new HashMap<>();
    private int minFreq;

    public Solution(int capacity) {
        this.capacity = capacity;
    }

    public int get(int key) {
        Integer value = values.get(key);
        if (value == null) {
            return -1;
        }
        touch(key);
        return value;
    }

    public void put(int key, int value) {
        if (capacity <= 0) {
            return;
        }
        if (values.containsKey(key)) {
            values.put(key, value);
            touch(key);
            return;
        }
        if (values.size() == capacity) {
            LinkedHashSet<Integer> lowest = buckets.get(minFreq);
            Integer evicted = lowest.iterator().next();
            lowest.remove(evicted);
            if (lowest.isEmpty()) {
                buckets.remove(minFreq);
            }
            values.remove(evicted);
            freqs.remove(evicted);
        }
        values.put(key, value);
        freqs.put(key, 1);
        buckets.computeIfAbsent(1, ignored -> new LinkedHashSet<>()).add(key);
        minFreq = 1;
    }

    private void touch(int key) {
        int freq = freqs.get(key);
        LinkedHashSet<Integer> current = buckets.get(freq);
        current.remove(key);
        if (current.isEmpty()) {
            buckets.remove(freq);
            if (freq == minFreq) {
                minFreq++;
            }
        }
        int nextFreq = freq + 1;
        freqs.put(key, nextFreq);
        buckets.computeIfAbsent(nextFreq, ignored -> new LinkedHashSet<>()).add(key);
    }
}
