package com.jinloes.linked_list;

import java.util.Stack;

/**
 * Definition for singly-linked list.
 * public class ListNode {
 * int val;
 * ListNode next;
 * ListNode() {}
 * ListNode(int val) { this.val = val; }
 * ListNode(int val, ListNode next) { this.val = val; this.next = next; }
 * }
 */

public class ReverseKGroup {

    public ListNode reverseKGroup(ListNode head, int k) {
        ListNode dummy = new ListNode();
        Stack<ListNode> stack = new Stack<>();
        dummy.next = head;
        ListNode current = head;
        ListNode previous = dummy;

        while (current != null) {
            ListNode innerCurrent = current;
            while (innerCurrent != null && stack.size() < k) {
                stack.add(innerCurrent);
                innerCurrent = innerCurrent.next;
            }
            ListNode next = innerCurrent;
            if (stack.size() == k) {
                ListNode innerDummy = new ListNode();
                ListNode innerPrevious = innerDummy;
                while (!stack.isEmpty()) {
                    innerPrevious.next = stack.pop();
                    innerPrevious = innerPrevious.next;
                }
                innerPrevious.next = next;
                previous.next = innerDummy.next;
                previous = innerPrevious;
            } else {
                previous = current;
            }
            current = next;
        }

        return dummy.next;
    }
}

