package com.jinloes.practice;
import java.util.ArrayDeque;
public class Solution {
    private final ArrayDeque<Integer> values = new ArrayDeque<>();
    public void add(int value) { values.addLast(value); }
    public int removeMin() { return values.removeFirst(); }
    public int peek() { return values.getFirst(); }
    public int size() { return values.size(); }
    public boolean isEmpty() { return values.isEmpty(); }
}
