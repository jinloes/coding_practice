/**
 * Write a program which deletes a node in a singly linked list. The input node is guaranteed not to be the tail node.
 */
public class DeleteNode {
    public static <T> ListNode<T> delete(ListNode<T> head, ListNode<T> toDelete) {
        ListNode<T> previous = null;
        ListNode<T> current = head;

        while (current != null && current != toDelete) {
            previous = current;
            current = current.next;
        }

        if (current == null) {
            return head;
        }

        if (previous == null) {
            return current.next;
        }

        previous.next = current.next;

        return head;
    }
}
