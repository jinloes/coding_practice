package com.jinloes.practice;

import java.util.HashMap;
import java.util.Map;

public class Solution {
    private static final class Entry {
        final int key;
        int value;
        Entry prev;
        Entry next;

        Entry(int key, int value) {
            this.key = key;
            this.value = value;
        }
    }

    private final int capacity;
    private final Map<Integer, Entry> entries = new HashMap<>();
    private final Entry head = new Entry(0, 0);
    private final Entry tail = new Entry(0, 0);

    public Solution(int capacity) {
        if (capacity < 1) {
            throw new IllegalArgumentException("capacity must be at least 1");
        }
        this.capacity = capacity;
        head.next = tail;
        tail.prev = head;
    }

    public int get(int key) {
        Entry entry = entries.get(key);
        if (entry == null) {
            return -1;
        }
        unlink(entry);
        addFirst(entry);
        return entry.value;
    }

    public void put(int key, int value) {
        Entry entry = entries.get(key);
        if (entry != null) {
            entry.value = value;
            unlink(entry);
            addFirst(entry);
            return;
        }
        if (entries.size() == capacity) {
            Entry eldest = tail.prev;
            unlink(eldest);
            entries.remove(eldest.key);
        }
        entry = new Entry(key, value);
        entries.put(key, entry);
        addFirst(entry);
    }

    private void unlink(Entry entry) {
        entry.prev.next = entry.next;
        entry.next.prev = entry.prev;
    }

    private void addFirst(Entry entry) {
        entry.next = head.next;
        entry.prev = head;
        head.next.prev = entry;
        head.next = entry;
    }
}
