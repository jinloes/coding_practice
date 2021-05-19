/**
 * Write a program that takes two singly linked lists, and determines if there exists a node that is
 * common to both lists. The lists can contain a cycle.
 */
public class OverlappingListsWithCycle {
    public static ListNode<?> check(ListNode<?> n1, ListNode<?> n2) {
        ListNode<?> root1 = CheckCycle.check(n1);
        ListNode<?> root2 = CheckCycle.check(n2);

        if (root1 == null && root2 == null) {
            // no cycles fall back to simpler case
            return OverlappingLists.check(root1, root2);
        } else if ((root1 != null && root2 == null)
                || (root1 == null && root2 != null)) {
            // cycle is before lists overlap so no overlap
            return null;
        }

        ListNode<?> tmp = root1.next;

        while (tmp != root1 && tmp != root2) {
            tmp = tmp.next;
        }

        if (tmp != root2) {
            // lists never overlapped
            return null;
        }

        int dist1 = distance(n1, root1);
        int dist2 = distance(n2, root2);

        ListNode<?> shorter;
        ListNode<?> longer;
        if (dist1 > dist2) {
            longer = n1;
            shorter = n2;
        } else {
            longer = n2;
            shorter = n1;
        }

        int diff = Math.abs(dist1 - dist2);

        for (int i = 0; i < diff; i++) {
            longer = longer.next;
        }

        while (longer != shorter && longer != root1 && shorter != root2) {
            longer = longer.next;
            shorter = shorter.next;
        }

        return longer == shorter ? longer : root1;
    }

    private static int distance(ListNode<?> start, ListNode<?> end) {
        int dist = 0;

        while (start != end) {
            start = start.next;
            dist++;
        }

        return dist;
    }

}
