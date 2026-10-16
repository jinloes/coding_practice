package com.jinloes.practice;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public final class ExampleRunner {
    private static final String USAGE =
            "Input: two digit lists separated by |, least significant digit first, for example: 2 4 3 | 5 6 4";

    private ExampleRunner() {
    }

    public static void main(String[] args) {
        if (args.length > 0) {
            runInput(String.join(" ", args).trim());
            return;
        }
        assertThat(add(new int[]{2, 4, 3}, new int[]{5, 6, 4})).as("Example 1: 342 + 465").containsExactly(7, 0, 8);
        assertThat(add(new int[]{9, 9, 9, 9}, new int[]{1})).as("Example 2: 9999 + 1").containsExactly(0, 0, 0, 0, 1);
        assertThat(add(new int[]{0}, new int[]{0})).as("Example 3: 0 + 0").containsExactly(0);
        System.out.println("Examples passed: 3");
    }

    private static void runInput(String input) {
        try {
            String[] sides = input.split("\\|", -1);
            if (sides.length != 2) {
                System.out.println("Could not parse input. " + USAGE);
                return;
            }
            int[] a = parse(sides[0]);
            int[] b = parse(sides[1]);
            System.out.println("addDigitLists = " + add(a, b));
        } catch (NumberFormatException exception) {
            System.out.println("Could not parse input. " + USAGE);
        }
    }

    private static int[] parse(String side) {
        String[] tokens = side.trim().split("\\s+");
        int[] digits = new int[tokens.length];
        for (int i = 0; i < tokens.length; i++) {
            digits[i] = Integer.parseInt(tokens[i]);
            if (digits[i] < 0 || digits[i] > 9) {
                throw new NumberFormatException("digit out of range");
            }
        }
        return digits;
    }

    private static List<Integer> add(int[] a, int[] b) {
        List<Integer> out = new ArrayList<>();
        for (Solution.ListNode n = Solution.addDigitLists(build(a), build(b)); n != null; n = n.next) {
            out.add(n.val);
        }
        return out;
    }

    private static Solution.ListNode build(int[] digits) {
        Solution.ListNode dummy = new Solution.ListNode(0);
        Solution.ListNode tail = dummy;
        for (int digit : digits) {
            tail.next = new Solution.ListNode(digit);
            tail = tail.next;
        }
        return dummy.next;
    }
}
