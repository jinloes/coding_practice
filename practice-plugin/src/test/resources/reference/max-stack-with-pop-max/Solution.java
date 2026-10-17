package com.jinloes.practice;

import java.util.Comparator;
import java.util.TreeSet;

public class Solution {
    private final TreeSet<Entry> byIndex = new TreeSet<>(Comparator.comparingInt(e -> e.index));
    private final TreeSet<Entry> byValue = new TreeSet<>(Comparator.<Entry>comparingInt(e -> e.value).thenComparingInt(e -> e.index));
    private int nextIndex;

    public Solution() {}

    public void push(int value) {
        Entry entry = new Entry(nextIndex++, value);
        byIndex.add(entry);
        byValue.add(entry);
    }

    public int pop() {
        Entry entry = byIndex.pollLast();
        byValue.remove(entry);
        return entry.value;
    }

    public int top() {
        return byIndex.last().value;
    }

    public int peekMax() {
        return byValue.last().value;
    }

    public int popMax() {
        Entry entry = byValue.pollLast();
        byIndex.remove(entry);
        return entry.value;
    }

    private record Entry(int index, int value) {}
}
