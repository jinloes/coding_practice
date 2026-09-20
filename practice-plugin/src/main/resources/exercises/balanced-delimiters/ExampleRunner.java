package com.jinloes.practice;

public final class ExampleRunner {
    private ExampleRunner() {
    }

    public static void main(String[] args) {
        assertEquals(true, Solution.isBalanced("{[()]}"));
        assertEquals(true, Solution.isBalanced("([]{})"));
        assertEquals(false, Solution.isBalanced("([)]"));
        System.out.println("Examples passed: 3");
    }

    private static void assertEquals(boolean expected, boolean actual) {
        if (expected != actual) {
            throw new AssertionError("Expected " + expected + " but was " + actual);
        }
    }
}
