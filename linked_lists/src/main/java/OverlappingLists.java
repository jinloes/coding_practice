/**
 * Write a program that takes two cycle-free singly linked lists, and determines if there exists a node that is
 * common to both lists.
 */
public class OverlappingLists {

    public static ListNode<?> check(ListNode<?> n1, ListNode<?> n2) {
        int n1Len = length(n1);
        int n2Len = length(n2);

        ListNode<?> longer;
        ListNode<?> shorter;

        if (n1Len > n2Len) {
            longer = n1;
            shorter = n2;
        } else {
            longer = n2;
            shorter = n1;
        }

        int diff = Math.abs(n1Len - n2Len);

        for (int i = 0; i < diff; i++) {
            longer = longer.next;
        }

        while (longer != null && shorter != null) {
            if (longer == shorter) {
                return shorter;
            }
            longer = longer.next;
            shorter = shorter.next;
        }

        return null;

    }

    private static int length(ListNode<?> head) {
        int len = 0;
        while (head != null) {
            len++;
            head = head.next;
        }

        return len;
    }
}
