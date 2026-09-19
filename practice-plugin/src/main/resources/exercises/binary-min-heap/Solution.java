package com.jinloes.practice;

public class Solution {
    public void add(int value) {
        throw new UnsupportedOperationException("Implement add");
    }

    public int removeMin() {
        throw new UnsupportedOperationException("Implement removeMin");
    }

    public int peek() {
        throw new UnsupportedOperationException("Implement peek");
    }

    public int size() {
        throw new UnsupportedOperationException("Implement size");
    }

    public boolean isEmpty() {
        throw new UnsupportedOperationException("Implement isEmpty");
    }

    public static void main(String[] args) {
        Solution first = new Solution();
        first.add(7);
        first.add(2);
        first.add(5);
        assertEquals(2, first.removeMin());
        assertEquals(5, first.removeMin());
        assertEquals(7, first.removeMin());
        Solution second = new Solution();
        second.add(4);
        second.add(4);
        assertEquals(4, second.removeMin());
        assertEquals(4, second.removeMin());
        assertEmptyOperations(new Solution());
        System.out.println("Examples passed: 3");
    }

    private static void assertEquals(int expected, int actual) {
        if (expected != actual) {
            throw new AssertionError("Expected " + expected + " but was " + actual);
        }
    }

    private static void assertEmptyOperations(Solution heap) {
        if (!heap.isEmpty()) {
            throw new AssertionError("A new heap must be empty");
        }
        try {
            heap.removeMin();
            throw new AssertionError("removeMin must fail when empty");
        } catch (java.util.NoSuchElementException expected) {
            // Expected.
        }
        try {
            heap.peek();
            throw new AssertionError("peek must fail when empty");
        } catch (java.util.NoSuchElementException expected) {
            // Expected.
        }
    }
}
