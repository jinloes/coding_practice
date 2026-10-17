package com.jinloes.practice;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.NoSuchElementException;

public class Solution {
    private final Deque<Integer> values = new ArrayDeque<>();
    private int last;
    public void enqueue(int value) { values.addLast(value); last = value; }
    public int dequeue() { if (values.isEmpty()) throw new NoSuchElementException(); return values.removeFirst(); }
    public int max() { if (values.isEmpty()) throw new NoSuchElementException(); return last; }
}
