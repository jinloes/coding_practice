/**
 * Implement a routine which sorts lists efficiently. It should be a stable sort, i.e., the relative positions of
 * equal elements must remain unchanged.
 */
public class FastSortingForList {
    public static ListNode<Integer> sort(ListNode<Integer> node) {
        if (node == null || node.next == null) {
            return node;
        }
        ListNode<Integer> slower = node;
        ListNode<Integer> faster = node;
        ListNode<Integer> preSlow = null;

        while (faster != null && faster.next != null) {
            preSlow = slower;
            slower = slower.next;
            faster = faster.next.next;
        }

        // Split lists
        preSlow.next = null;

        return merge(sort(node), sort(slower));
    }

    private static ListNode<Integer> merge(ListNode<Integer> l1, ListNode<Integer> l2) {
        ListNode<Integer> dummy = new ListNode<>(0, null);
        ListNode<Integer> current = dummy;

        while (l1 != null && l2 != null) {
            if (l1.data < l2.data) {
                current.next = l1;
                l1 = l1.next;
            } else {
                current.next = l2;
                l2 = l2.next;
            }
            current = current.next;
        }

        while (l1 != null) {
            current.next = l1;
            current = current.next;
            l1 = l1.next;

        }

        while (l2 != null) {
            current.next = l2;
            current = current.next;
            l2 = l2.next;

        }

        return dummy.next;
    }
}
