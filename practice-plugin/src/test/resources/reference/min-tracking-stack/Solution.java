package com.jinloes.practice;

import java.util.Arrays;
import java.util.NoSuchElementException;

public class Solution {
    private int[] values = new int[16];
    private int[] minimums = new int[16];
    private int size;

    public void push(int value) {
        if (size == values.length) {
            values = Arrays.copyOf(values, size * 2);
            minimums = Arrays.copyOf(minimums, size * 2);
        }
        values[size] = value;
        minimums[size] = size == 0 ? value : Math.min(value, minimums[size - 1]);
        size++;
    }

    public int pop() {
        int value = top();
        size--;
        return value;
    }

    public int top() {
        if (size == 0) {
            throw new NoSuchElementException("stack is empty");
        }
        return values[size - 1];
    }

    public int min() {
        if (size == 0) {
            throw new NoSuchElementException("stack is empty");
        }
        return minimums[size - 1];
    }

    public int size() {
        return size;
    }
}
