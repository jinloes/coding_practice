package com.jinloes.linked_list;

class ReorderList {
    public void reorderList(ListNode head) {
        ListNode slow = head;
        ListNode fast = head.next;

        while (fast != null && fast.next != null) {
            slow = slow.next;
            fast = fast.next.next;
        }


        ListNode current = slow.next;
        slow.next = null;
        ListNode previous = null;
        while (current != null) {
            ListNode next = current.next;
            current.next = previous;
            previous = current;
            current = next;
        }

        ListNode mid = previous;

        ListNode dummy = new ListNode();
        current = dummy;
        ListNode list1 = head;
        ListNode list2 = mid;
        int idx = 0;

        while (list1 != null && list2 != null) {
            if (idx % 2 == 0) {
                current.next = list1;
                list1 = list1.next;
            } else {
                current.next = list2;
                list2 = list2.next;
            }
            current = current.next;
            idx++;
        }

        while (list1 != null) {
            current.next = list1;
            list1 = list1.next;
            current = current.next;
        }

        while (list2 != null) {
            current.next = list2;
            list2 = list2.next;
            current = current.next;
        }
    }
}

