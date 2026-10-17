package com.jinloes.practice;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.NoSuchElementException;

public class Solution {
    private final Deque<Integer> values = new ArrayDeque<>();
    private final Deque<Integer> maximums = new ArrayDeque<>();
    public void enqueue(int value) { values.addLast(value); while (!maximums.isEmpty() && maximums.peekLast() < value) maximums.removeLast(); maximums.addLast(value); }
    public int dequeue() { if (values.isEmpty()) throw new NoSuchElementException("queue is empty"); int value = values.removeFirst(); if (value == maximums.peekFirst()) maximums.removeFirst(); return value; }
    public int max() { if (maximums.isEmpty()) throw new NoSuchElementException("queue is empty"); return maximums.peekFirst(); }
}
