import java.util.Stack;

/**
 * Write a program which takes a singly linked list L and two integers s and / as arguments, and reverses the order
 * of the nodes from the sth node to /th node, inclusive. The numbering begins at 1, i.e., the head node is the
 * first node. Do not allocate additional nodes.
 */
public class ReverseSublist {
    public static ListNode<Integer> reverse(ListNode<Integer> head, int start, int end) {
        ListNode<Integer> dummy = new ListNode<>(0, head);
        ListNode<Integer> current = dummy;


        // find the first node before where we want to reverse
        for (int i = 1; i < start; i++) {
            current = current.next;
        }

        ListNode<Integer> toReverse = current.next;

        // remove items from list to reverse and add to stack
        // at the end toReverse will be the list we didn't want to reverse
        Stack<ListNode<Integer>> stack = new Stack<>();
        for (int i = start; i <= end; i++) {
            stack.add(toReverse);
            toReverse = toReverse.next;
        }

        while (!stack.isEmpty()) {
            current.next = stack.pop();
            current = current.next;
        }

        current.next = toReverse;

        return dummy.next;
    }
}
