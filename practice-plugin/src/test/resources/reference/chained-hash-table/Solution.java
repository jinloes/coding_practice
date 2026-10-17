package com.jinloes.practice;

public class Solution {
    private static final class Entry {
        final String key;
        String value;
        Entry next;
        Entry(String key, String value, Entry next) { this.key = key; this.value = value; this.next = next; }
    }
    private final Entry[] buckets;
    private int size;
    public Solution(int capacity) {
        if (capacity < 1) throw new IllegalArgumentException("capacity must be positive");
        buckets = new Entry[capacity];
    }
    public void put(String key, String value) {
        int index = index(key);
        for (Entry entry = buckets[index]; entry != null; entry = entry.next) {
            if (entry.key.equals(key)) { entry.value = value; return; }
        }
        buckets[index] = new Entry(key, value, buckets[index]);
        size++;
    }
    public String get(String key) {
        for (Entry entry = buckets[index(key)]; entry != null; entry = entry.next) if (entry.key.equals(key)) return entry.value;
        return null;
    }
    public boolean containsKey(String key) {
        for (Entry entry = buckets[index(key)]; entry != null; entry = entry.next) if (entry.key.equals(key)) return true;
        return false;
    }
    public boolean remove(String key) {
        int index = index(key);
        Entry previous = null;
        for (Entry entry = buckets[index]; entry != null; entry = entry.next) {
            if (entry.key.equals(key)) {
                if (previous == null) buckets[index] = entry.next; else previous.next = entry.next;
                size--;
                return true;
            }
            previous = entry;
        }
        return false;
    }
    public int size() { return size; }
    private int index(String key) {
        if (key == null) throw new IllegalArgumentException("key must not be null");
        return (key.hashCode() & 0x7fffffff) % buckets.length;
    }
}
