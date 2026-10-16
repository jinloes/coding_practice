package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Random;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class ExamplesTest {
    @Test
    void addsWithCarriesInTheMiddle() {
        assertSum(new int[]{2, 4, 3}, new int[]{5, 6, 4}, 7, 0, 8);
    }

    @Test
    void finalCarryCreatesANewNode() {
        assertSum(new int[]{9, 9, 9, 9}, new int[]{1}, 0, 0, 0, 0, 1);
    }

    @Test
    void zeroPlusZeroIsZero() {
        assertSum(new int[]{0}, new int[]{0}, 0);
    }

    private static Solution.ListNode list(int... digits) {
        Solution.ListNode dummy = new Solution.ListNode(0);
        Solution.ListNode tail = dummy;
        for (int digit : digits) {
            tail.next = new Solution.ListNode(digit);
            tail = tail.next;
        }
        return dummy.next;
    }

    private static List<Integer> digits(Solution.ListNode head) {
        List<Integer> out = new ArrayList<>();
        for (Solution.ListNode n = head; n != null; n = n.next) {
            out.add(n.val);
        }
        return out;
    }

    private static void assertSum(int[] a, int[] b, Integer... expected) {
        Solution.ListNode left = list(a);
        Solution.ListNode right = list(b);
        Set<Solution.ListNode> inputs = Collections.newSetFromMap(new IdentityHashMap<>());
        for (Solution.ListNode n = left; n != null; n = n.next) {
            inputs.add(n);
        }
        for (Solution.ListNode n = right; n != null; n = n.next) {
            inputs.add(n);
        }
        Solution.ListNode result = Solution.addDigitLists(left, right);
        assertThat(digits(result)).as("addDigitLists(%s, %s)", digits(left), digits(right))
                .containsExactly(expected);
        for (Solution.ListNode n = result; n != null; n = n.next) {
            assertThat(inputs.contains(n)).as("result must use new nodes").isFalse();
        }
        assertThat(digits(left)).as("first input must stay unchanged").hasSize(a.length);
        assertThat(digits(right)).as("second input must stay unchanged").hasSize(b.length);
    }
}
