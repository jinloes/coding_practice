package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Random;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class CorrectnessTest {
    @Test
    void listsOfDifferentLengths() {
        assertSum(new int[]{5}, new int[]{5, 1, 2}, 0, 2, 2);
        assertSum(new int[]{1, 1, 1, 1}, new int[]{2}, 3, 1, 1, 1);
    }

    @Test
    void carryRipplesThroughTheLongerList() {
        assertSum(new int[]{1}, new int[]{9, 9, 9}, 0, 0, 0, 1);
    }

    @Test
    void finalCarryWithEqualLengths() {
        assertSum(new int[]{8, 8}, new int[]{7, 9}, 5, 8, 1);
    }

    @Test
    void nullListsCountAsZero() {
        assertThat(digits(Solution.addDigitLists(null, list(4, 2)))).containsExactly(4, 2);
        assertThat(digits(Solution.addDigitLists(list(4, 2), null))).containsExactly(4, 2);
        assertThat(digits(Solution.addDigitLists(null, null))).containsExactly(0);
    }

    @Test
    void leadingZerosAreKept() {
        assertSum(new int[]{0, 0}, new int[]{0}, 0, 0);
        assertSum(new int[]{1, 0, 0}, new int[]{1}, 2, 0, 0);
    }

    @Test
    void matchesBigIntegerArithmetic() {
        Random random = new Random(11);
        for (int round = 0; round < 50; round++) {
            int[] a = new int[1 + random.nextInt(40)];
            int[] b = new int[1 + random.nextInt(40)];
            for (int i = 0; i < a.length; i++) {
                a[i] = random.nextInt(10);
            }
            for (int i = 0; i < b.length; i++) {
                b[i] = random.nextInt(10);
            }
            java.math.BigInteger sum = value(a).add(value(b));
            List<Integer> result = digits(Solution.addDigitLists(list(a), list(b)));
            assertThat(value(result)).as("round %d", round).isEqualTo(sum);
        }
    }

    private static java.math.BigInteger value(int[] digits) {
        StringBuilder text = new StringBuilder();
        for (int i = digits.length - 1; i >= 0; i--) {
            text.append(digits[i]);
        }
        return new java.math.BigInteger(text.toString());
    }

    private static java.math.BigInteger value(List<Integer> digits) {
        return value(digits.stream().mapToInt(Integer::intValue).toArray());
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
