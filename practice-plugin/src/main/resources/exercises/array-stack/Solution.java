package com.jinloes.practice;

public class Solution {
    public void push(int value) {
        throw new UnsupportedOperationException("Implement push");
    }

    public int pop() {
        throw new UnsupportedOperationException("Implement pop");
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
        first.push(4);
        first.push(9);
        assertEquals(9, first.pop());
        assertEquals(4, first.pop());
        Solution second = new Solution();
        second.push(5);
        assertEquals(5, second.peek());
        assertEquals(1, second.size());
        assertEmptyOperations(new Solution());
        System.out.println("Examples passed: 3");
    }

    private static void assertEquals(int expected, int actual) {
        if (expected != actual) {
            throw new AssertionError("Expected " + expected + " but was " + actual);
        }
    }

    private static void assertEmptyOperations(Solution stack) {
        if (!stack.isEmpty()) {
            throw new AssertionError("A new stack must be empty");
        }
        try {
            stack.pop();
            throw new AssertionError("pop must fail when empty");
        } catch (java.util.NoSuchElementException expected) {
            // Expected.
        }
        try {
            stack.peek();
            throw new AssertionError("peek must fail when empty");
        } catch (java.util.NoSuchElementException expected) {
            // Expected.
        }
    }
}
