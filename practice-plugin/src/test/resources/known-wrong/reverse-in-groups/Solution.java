package com.jinloes.practice;

public class Solution {
    public static class ListNode {
        public int val;
        public ListNode next;

        public ListNode(int val) {
            this.val = val;
        }
    }

    public static ListNode reverseInGroups(ListNode head, int k) {
        if (k < 1) {
            throw new IllegalArgumentException("k must be at least 1");
        }
        ListNode dummy = new ListNode(0);
        dummy.next = head;
        ListNode groupPrev = dummy;
        while (true) {
            ListNode after = groupPrev.next;
            int count = 0;
            while (after != null && count < k) {
                after = after.next;
                count++;
            }
            if (count == 0) {
                break;
            }

            ListNode groupStart = groupPrev.next;
            ListNode prev = after;
            ListNode cursor = groupStart;
            for (int i = 0; i < count; i++) {
                ListNode next = cursor.next;
                cursor.next = prev;
                prev = cursor;
                cursor = next;
            }
            groupPrev.next = prev;
            groupPrev = groupStart;
        }
        return dummy.next;
    }
}
