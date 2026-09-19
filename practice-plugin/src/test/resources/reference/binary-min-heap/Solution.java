package com.jinloes.practice;

import java.util.NoSuchElementException;

public class Solution {
    private int[] values = new int[8];
    private int size;

    public void add(int value) {
        ensureCapacity();
        int index = size++;
        values[index] = value;
        while (index > 0) {
            int parent = (index - 1) / 2;
            if (values[parent] <= values[index]) {
                break;
            }
            swap(parent, index);
            index = parent;
        }
    }

    public int removeMin() {
        ensureNotEmpty();
        int result = values[0];
        values[0] = values[--size];
        values[size] = 0;
        int index = 0;
        while (true) {
            int left = index * 2 + 1;
            if (left >= size) {
                break;
            }
            int right = left + 1;
            int smallerChild = right < size && values[right] < values[left] ? right : left;
            if (values[index] <= values[smallerChild]) {
                break;
            }
            swap(index, smallerChild);
            index = smallerChild;
        }
        return result;
    }

    public int peek() {
        ensureNotEmpty();
        return values[0];
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    private void ensureCapacity() {
        if (size == values.length) {
            int[] larger = new int[values.length * 2];
            System.arraycopy(values, 0, larger, 0, values.length);
            values = larger;
        }
    }

    private void ensureNotEmpty() {
        if (size == 0) {
            throw new NoSuchElementException("Heap is empty");
        }
    }

    private void swap(int first, int second) {
        int temporary = values[first];
        values[first] = values[second];
        values[second] = temporary;
    }
}
