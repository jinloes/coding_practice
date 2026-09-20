package com.jinloes.practice;

public final class ExampleRunner {
    private ExampleRunner() {
    }

    public static void main(String[] args) {
        assertPair(new int[]{2, 7, 11, 15}, 9);
        assertPair(new int[]{3, 3}, 6);
        int[] missing = Solution.findPair(new int[]{1, 2, 3}, 7);
        if (missing == null || missing.length != 0) {
            throw new AssertionError("Example 3: expected []");
        }
        System.out.println("Examples passed: 3");
    }

    private static void assertPair(int[] numbers, int target) {
        int[] pair = Solution.findPair(numbers, target);
        if (pair == null || pair.length != 2 || pair[0] == pair[1]
                || pair[0] < 0 || pair[1] < 0
                || pair[0] >= numbers.length || pair[1] >= numbers.length
                || (long) numbers[pair[0]] + numbers[pair[1]] != target) {
            throw new AssertionError("Expected a valid pair for target " + target);
        }
    }
}
