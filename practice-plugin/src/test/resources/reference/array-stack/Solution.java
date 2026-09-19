package com.jinloes.practice;

import java.util.NoSuchElementException;

public class Solution {
    private int[] values = new int[8];
    private int size;

    public void push(int value) {
        if (size == values.length) {
            int[] larger = new int[values.length * 2];
            System.arraycopy(values, 0, larger, 0, values.length);
            values = larger;
        }
        values[size++] = value;
    }

    public int pop() {
        ensureNotEmpty();
        int value = values[--size];
        values[size] = 0;
        return value;
    }

    public int peek() {
        ensureNotEmpty();
        return values[size - 1];
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    private void ensureNotEmpty() {
        if (size == 0) {
            throw new NoSuchElementException("Stack is empty");
        }
    }
}
