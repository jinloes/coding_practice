/**
 * Write a function that reverses a singly linked list. The function should use no more than constant storage beyond
 * that needed for the list itself.
 */
public class ReverseLinkedList {
    public static ListNode<Integer> reverse(ListNode<Integer> head) {
        ListNode<Integer> current = head;
        ListNode<Integer> previous = null;

        while (current != null) {
            ListNode<Integer> next = current.next;
            current.next = previous;
            previous = current;
            current = next;
        }

        return previous;
    }
}
