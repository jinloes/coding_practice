package com.jinloes.practice;

public class Solution {
    public static int search(int[] sorted, int target) {
        throw new UnsupportedOperationException("Implement search");
    }

    public static void main(String[] args) {
        assertIndex(search(new int[]{-4, -1, 0, 6, 9}, 6), new int[]{-4, -1, 0, 6, 9}, 6);
        assertIndex(search(new int[]{1, 2, 2, 2, 8}, 2), new int[]{1, 2, 2, 2, 8}, 2);
        assertIndex(search(new int[]{1, 4, 7}, 5), new int[]{1, 4, 7}, 5);
        System.out.println("Examples passed: 3");
    }

    private static void assertIndex(int index, int[] sorted, int target) {
        if (target == 5 && index == -1) {
            return;
        }
        if (index < 0 || index >= sorted.length || sorted[index] != target) {
            throw new AssertionError("Expected a matching index for " + target);
        }
    }
}
