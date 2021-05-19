/**
 * Write a program that takes the head of a singly linked list and returns null if there does not exist a cycle,
 * and the node at the start of the cycle, if a cycle is present. (You do not know the length of the list in advance.)
 */
public class CheckCycle {

    public static ListNode<?> check(ListNode<?> head) {
        ListNode<?> current = head;
        ListNode<?> faster = head;

        while (faster != null && faster.next != null && faster.next.next != null) {
            current = current.next;
            faster = faster.next.next;

            if (current == faster) {
                current = head;

                while (current != faster) {
                    current = current.next;
                    faster = faster.next;
                }
                return current;
            }

        }

        return null;
    }
}
