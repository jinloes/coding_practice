package com.jinloes.practice;

public class Solution {
    public static int[] findPair(int[] numbers, int target) {
        throw new UnsupportedOperationException("Implement findPair");
    }

    public static void main(String[] args) {
        assertPair(findPair(new int[]{2, 7, 11, 15}, 9), new int[]{2, 7, 11, 15}, 9);
        assertPair(findPair(new int[]{3, 3}, 6), new int[]{3, 3}, 6);
        assertPair(findPair(new int[]{1, 2, 3}, 7), new int[]{1, 2, 3}, 7);
        System.out.println("Examples passed: 3");
    }

    private static void assertPair(int[] pair, int[] numbers, int target) {
        if (pair.length == 0 && target == 7) {
            return;
        }
        if (pair.length != 2 || pair[0] == pair[1] || pair[0] < 0 || pair[1] < 0
                || pair[0] >= numbers.length || pair[1] >= numbers.length
                || (long) numbers[pair[0]] + numbers[pair[1]] != target) {
            throw new AssertionError("Expected a valid pair for target " + target);
        }
    }
}
