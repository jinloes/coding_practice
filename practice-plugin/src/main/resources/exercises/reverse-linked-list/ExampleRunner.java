package com.jinloes.practice;

public final class ExampleRunner {
    private ExampleRunner() {
    }

    public static void main(String[] args) {
        assertValues(Solution.reverse(
                new Solution.Node(1, new Solution.Node(2, new Solution.Node(3, null)))), 3, 2, 1);
        Solution.Node single = new Solution.Node(8, null);
        if (Solution.reverse(single) != single) {
            throw new AssertionError("Example 2: expected the same node");
        }
        if (Solution.reverse(null) != null) {
            throw new AssertionError("Example 3: expected null");
        }
        System.out.println("Examples passed: 3");
    }

    private static void assertValues(Solution.Node head, int... expected) {
        for (int value : expected) {
            if (head == null || head.value != value) {
                throw new AssertionError("Unexpected reversed list");
            }
            head = head.next;
        }
        if (head != null) {
            throw new AssertionError("Unexpected reversed list length");
        }
    }
}
