package com.jinloes.data_structures;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class AllOne {
    private static class ValueMeta {
        Set<String> keys;
        ValueMeta next;
        ValueMeta previous;
        int count;

        public ValueMeta(int count) {
            this.keys = new HashSet<>();
            this.count = count;
        }

        public String toString() {
            return count + ":" + keys;
        }
    }

    private final Map<String, Integer> keyIndex;
    private final Map<Integer, ValueMeta> countIndex;
    private ValueMeta min;
    private ValueMeta max;

    public AllOne() {
        this.keyIndex = new HashMap<>();
        this.countIndex = new HashMap<>();
        this.min = new ValueMeta(0);
        countIndex.put(0, min);

        this.max = new ValueMeta(0);
        countIndex.put(Integer.MAX_VALUE, max);
        min.next = max;
        max.previous = min;
    }

    public void inc(String key) {
        int count = keyIndex.getOrDefault(key, 0);
        int newCount = count + 1;

        keyIndex.put(key, newCount);

        ValueMeta meta = countIndex.getOrDefault(newCount, new ValueMeta(newCount));
        countIndex.put(newCount, meta);
        meta.keys.add(key);

        ValueMeta oldMeta = countIndex.get(count);
        oldMeta.keys.remove(key);

        if (meta.keys.size() == 1) {
            meta.next = oldMeta.next;
            meta.previous = oldMeta;

            oldMeta.next.previous = meta;
            oldMeta.next = meta;
        }

        if (oldMeta.keys.isEmpty() && oldMeta != min) {
            oldMeta.previous.next = meta;
            meta.previous = oldMeta.previous;
        }
    }

    public void dec(String key) {
        int count = keyIndex.get(key);
        int newCount = count - 1;

        keyIndex.put(key, newCount);

        ValueMeta meta = countIndex.getOrDefault(newCount, new ValueMeta(newCount));
        countIndex.put(newCount, meta);
        if (newCount > 0) {
            meta.keys.add(key);
        }

        ValueMeta oldMeta = countIndex.get(count);
        oldMeta.keys.remove(key);

        if (meta.keys.size() == 1) {
            meta.next = oldMeta;
            meta.previous = oldMeta.previous;

            oldMeta.previous.next = meta;
            oldMeta.previous = meta;
        }

        if (oldMeta.keys.isEmpty() && oldMeta != max) {
            oldMeta.next.previous = meta;
            meta.next = oldMeta.next;
        }
    }

    public String getMaxKey() {
        if (max.previous != min) {
            return max.previous.keys.iterator().next();
        }
        return "";
    }

    public String getMinKey() {
        System.out.println(countIndex);
        if (min.next != max) {
            return min.next.keys.iterator().next();
        }
        return "";
    }
}
