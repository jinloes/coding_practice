package com.jinloes.simple_functions;

import java.util.Stack;

class RemoveNthFromEnd {
    public static ListNode removeNthFromEnd(ListNode head, int n) {
        Stack<ListNode> stack = new Stack<>();

        ListNode current = head;

        while (current != null) {
            stack.add(current);
            current = current.next;
        }

        ListNode newHead = null;
        for (int i = 1; !stack.isEmpty(); i++) {
            ListNode cur = stack.pop();

            if (i != n) {
                cur.next = newHead;
                newHead = cur;
            }
        }

        return newHead;
    }
}