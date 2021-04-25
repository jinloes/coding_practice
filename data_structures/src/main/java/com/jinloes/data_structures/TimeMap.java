package com.jinloes.data_structures;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * Create a timebased key-value store class TimeMap, that supports two operations.
 * <p>
 * 1. set(string key, string value, int timestamp)
 * <p>
 * Stores the key and value, along with the given timestamp.
 * 2. get(string key, int timestamp)
 * <p>
 * Returns a value such that set(key, value, timestamp_prev) was called previously, with timestamp_prev <= timestamp.
 * If there are multiple such values, it returns the one with the largest timestamp_prev.
 * If there are no values, it returns the empty string ("").
 */
public class TimeMap {
    private final Map<String, TreeMap<Integer, String>> delegate;

    /**
     * Initialize your data structure here.
     */
    public TimeMap() {
        delegate = new HashMap<>();
    }

    public void set(String key, String value, int timestamp) {
        delegate.putIfAbsent(key, new TreeMap<>());
        delegate.get(key).put(timestamp, value);
    }

    public String get(String key, int timestamp) {
        if (delegate.containsKey(key)) {
            Map.Entry<Integer, String> entry = delegate.get(key).floorEntry(timestamp);
            if (entry == null) {
                return null;
            } else {
                return entry.getValue();
            }
        } else {
            return null;
        }
    }
}
