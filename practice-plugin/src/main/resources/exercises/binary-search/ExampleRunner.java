package com.jinloes.practice;

public final class ExampleRunner {
    private ExampleRunner() {
    }

    public static void main(String[] args) {
        assertIndex(new int[]{-4, -1, 0, 6, 9}, 6);
        assertIndex(new int[]{1, 2, 2, 2, 8}, 2);
        if (Solution.search(new int[]{1, 4, 7}, 5) != -1) {
            throw new AssertionError("Example 3: expected -1");
        }
        System.out.println("Examples passed: 3");
    }

    private static void assertIndex(int[] sorted, int target) {
        int index = Solution.search(sorted, target);
        if (index < 0 || index >= sorted.length || sorted[index] != target) {
            throw new AssertionError("Expected a matching index for " + target);
        }
    }
}
