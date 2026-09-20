package com.jinloes.practice;

import java.util.NoSuchElementException;

public final class ExampleRunner {
    private ExampleRunner() {
    }

    public static void main(String[] args) {
        Solution heap = new Solution();
        heap.add(7);
        heap.add(2);
        heap.add(5);
        assertEquals(2, heap.peek());
        assertEquals(2, heap.removeMin());
        assertEquals(5, heap.removeMin());
        assertEquals(7, heap.removeMin());

        Solution duplicates = new Solution();
        duplicates.add(4);
        duplicates.add(1);
        duplicates.add(1);
        assertEquals(1, duplicates.removeMin());
        assertEquals(1, duplicates.removeMin());

        Solution empty = new Solution();
        if (!empty.isEmpty()) {
            throw new AssertionError("Example 3: a new heap must be empty");
        }
        expectNoSuchElement(empty::removeMin);
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
