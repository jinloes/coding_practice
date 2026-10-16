package com.jinloes.practice;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public final class ExampleRunner {
    private static final String USAGE =
            "Input: list values, then | and the group size, for example: 1 2 3 4 5 | 2";

    private ExampleRunner() {
    }

    public static void main(String[] args) {
        if (args.length > 0) {
            runInput(String.join(" ", args).trim());
            return;
        }
        assertThat(reverse(new int[]{1, 2, 3, 4, 5}, 2)).as("Example 1").containsExactly(2, 1, 4, 3, 5);
        assertThat(reverse(new int[]{1, 2, 3, 4, 5, 6}, 3)).as("Example 2").containsExactly(3, 2, 1, 6, 5, 4);
        assertThat(reverse(new int[]{1, 2, 3}, 1)).as("Example 3").containsExactly(1, 2, 3);
        System.out.println("Examples passed: 3");
    }

    private static void runInput(String input) {
        try {
            String[] sides = input.split("\\|", -1);
            if (sides.length != 2) {
                System.out.println("Could not parse input. " + USAGE);
                return;
            }
            String valueText = sides[0].trim();
            int[] values = valueText.isEmpty() ? new int[0]
                    : java.util.Arrays.stream(valueText.split("\\s+")).mapToInt(Integer::parseInt).toArray();
            int k = Integer.parseInt(sides[1].trim());
            if (k < 1) {
                System.out.println("The group size must be at least 1. " + USAGE);
                return;
            }
            System.out.println("reverseInGroups = " + reverse(values, k));
        } catch (NumberFormatException exception) {
            System.out.println("Could not parse input. " + USAGE);
        }
    }

    private static List<Integer> reverse(int[] values, int k) {
        Solution.ListNode dummy = new Solution.ListNode(0);
        Solution.ListNode tail = dummy;
        for (int value : values) {
            tail.next = new Solution.ListNode(value);
            tail = tail.next;
        }
        List<Integer> out = new ArrayList<>();
        for (Solution.ListNode n = Solution.reverseInGroups(dummy.next, k); n != null; n = n.next) {
            out.add(n.val);
        }
        return out;
    }
}
