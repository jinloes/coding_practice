package com.jinloes.practice;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;

public class Solution {
    private static final Object TOMBSTONE = new Object();
    private final Map<String, String> base = new HashMap<>();
    private final Deque<Map<String, Object>> transactions = new ArrayDeque<>();

    public Solution() {
    }

    public void set(String key, String value) {
        if (transactions.isEmpty()) {
            base.put(key, value);
        } else {
            transactions.peek().put(key, value);
        }
    }

    public String get(String key) {
        for (Map<String, Object> frame : transactions) {
            if (frame.containsKey(key)) {
                Object value = frame.get(key);
                return value == TOMBSTONE ? null : (String) value;
            }
        }
        return base.get(key);
    }

    public void delete(String key) {
        if (transactions.isEmpty()) {
            base.remove(key);
        } else {
            transactions.peek().put(key, TOMBSTONE);
        }
    }

    public void begin() {
        transactions.push(new HashMap<>());
    }

    public boolean commit() {
        if (transactions.isEmpty()) {
            return false;
        }
        Map<String, Object> top = transactions.pop();
        if (transactions.isEmpty()) {
            for (Map.Entry<String, Object> entry : top.entrySet()) {
                if (entry.getValue() == TOMBSTONE) {
                    base.remove(entry.getKey());
                } else {
                    base.put(entry.getKey(), (String) entry.getValue());
                }
            }
        } else {
            transactions.peek().putAll(top);
        }
        return true;
    }

    public boolean rollback() {
        if (transactions.isEmpty()) {
            return false;
        }
        transactions.pop();
        return true;
    }
}
