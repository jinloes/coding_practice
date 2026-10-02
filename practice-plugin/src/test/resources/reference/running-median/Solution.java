package com.jinloes.practice;

import java.util.Collections;
import java.util.PriorityQueue;

public class Solution {
    private final PriorityQueue<Integer> lower = new PriorityQueue<>(Collections.reverseOrder());
    private final PriorityQueue<Integer> upper = new PriorityQueue<>();

    public void add(int value) {
        if (lower.isEmpty() || value <= lower.peek()) {
            lower.add(value);
        } else {
            upper.add(value);
        }
        if (lower.size() > upper.size() + 1) {
            upper.add(lower.poll());
        } else if (upper.size() > lower.size()) {
            lower.add(upper.poll());
        }
    }

    public double median() {
        if (lower.isEmpty()) {
            throw new IllegalStateException("No values have been added");
        }
        if (lower.size() > upper.size()) {
            return lower.peek();
        }
        return ((long) lower.peek() + upper.peek()) / 2.0;
    }
}
