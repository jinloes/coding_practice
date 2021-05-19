/**
 * Write a program that takes two lists, assumed to be sorted, and returns their merge.
 * The only field your program can change in a node is its next field.
 */
public class MergeSortedLists {
    public static ListNode<Integer> merge(ListNode<Integer> n1, ListNode<Integer> n2) {
        ListNode<Integer> dummy = new ListNode<>(-1);
        ListNode<Integer> current = dummy;

        while (n1 != null && n2 != null) {
            if (n1.data > n2.data) {
                current.next = n2;
                n2 = n2.next;
            } else {
                current.next = n1;
                n1 = n1.next;
            }
            current = current.next;
        }

        current.next = n1 != null ? n1 : n2;

        return dummy.next;
    }
}
