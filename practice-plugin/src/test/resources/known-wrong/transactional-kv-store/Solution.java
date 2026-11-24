package com.jinloes.practice;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;

public class Solution {
    private final Map<String, String> base = new HashMap<>();
    private final Deque<Map<String, String>> transactions = new ArrayDeque<>();

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
        for (Map<String, String> frame : transactions) {
            if (frame.containsKey(key)) {
                return frame.get(key);
            }
        }
        return base.get(key);
    }

    public void delete(String key) {
        // Bug: removes from top frame instead of writing a tombstone, so outer values leak through.
        if (transactions.isEmpty()) {
            base.remove(key);
        } else {
            transactions.peek().remove(key);
        }
    }

    public void begin() {
        transactions.push(new HashMap<>());
    }

    public boolean commit() {
        if (transactions.isEmpty()) {
            return false;
        }
        Map<String, String> top = transactions.pop();
        if (transactions.isEmpty()) {
            base.putAll(top);
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
