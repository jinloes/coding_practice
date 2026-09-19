package com.jinloes.practice;

public class Solution {
    public static boolean isBalanced(String input) {
        throw new UnsupportedOperationException("Implement isBalanced");
    }

    public static void main(String[] args) {
        assertEquals(true, isBalanced("{[()]}"));
        assertEquals(true, isBalanced("([]{})"));
        assertEquals(false, isBalanced("([)]"));
        System.out.println("Examples passed: 3");
    }

    private static void assertEquals(boolean expected, boolean actual) {
        if (expected != actual) {
            throw new AssertionError("Expected " + expected + " but was " + actual);
        }
    }
}
