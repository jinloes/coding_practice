package com.jinloes.practice;

import java.util.NoSuchElementException;

public final class ExampleRunner {
    private ExampleRunner() {
    }

    public static void main(String[] args) {
        Solution stack = new Solution();
        stack.push(4);
        stack.push(9);
        assertEquals(9, stack.pop());
        assertEquals(4, stack.pop());

        Solution peek = new Solution();
        peek.push(12);
        assertEquals(12, peek.peek());
        assertEquals(1, peek.size());

        Solution empty = new Solution();
        if (!empty.isEmpty()) {
            throw new AssertionError("Example 3: a new stack must be empty");
        }
        expectNoSuchElement(empty::pop);
        expectNoSuchElement(empty::peek);
        System.out.println("Examples passed: 3");
    }

    private static void assertEquals(int expected, int actual) {
        if (expected != actual) {
            throw new AssertionError("Expected " + expected + " but was " + actual);
        }
    }

    private static void expectNoSuchElement(Runnable operation) {
        try {
            operation.run();
            throw new AssertionError("Expected NoSuchElementException");
        } catch (NoSuchElementException expected) {
            // Expected.
        }
    }
}
