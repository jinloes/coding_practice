package com.jinloes.practice;

import java.util.ArrayDeque;
import java.util.NoSuchElementException;

public class Solution {
    private final ArrayDeque<Integer> values = new ArrayDeque<>();
    private int min = Integer.MAX_VALUE;

    public void push(int value) {
        values.push(value);
        min = Math.min(min, value);
    }

    public int pop() {
        if (values.isEmpty()) {
            throw new NoSuchElementException();
        }
        return values.pop();
    }

    public int top() {
        if (values.isEmpty()) {
            throw new NoSuchElementException();
        }
        return values.peek();
    }

    public int min() {
        if (values.isEmpty()) {
            throw new NoSuchElementException();
        }
        return min;
    }

    public int size() {
        return values.size();
    }
}
