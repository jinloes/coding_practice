package com.jinloes.data_structures.linked_list;

/**
 * Given a singly linked list find the Mth to last element.
 */
public class MthToLast {
    public static <T> ListNode<T> mthToLast(ListNode<T> head, int mth) {
        int count = 0;
        ListNode<T> current = head;
        ListNode<T> mthNode = null;

        while (current != null) {
            if (count <= mth) {
                if (count == mth) {
                    mthNode = head;
                }
                count++;

            } else {
                mthNode = mthNode.next;
            }
            current = current.next;
        }

        return mthNode;
    }
}
