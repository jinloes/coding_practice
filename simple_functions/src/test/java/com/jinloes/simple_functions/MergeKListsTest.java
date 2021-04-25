package com.jinloes.simple_functions;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class MergeKListsTest {
    private MergeKLists mergeKLists;

    @Before
    public void setUp() throws Exception {
        mergeKLists = new MergeKLists();
    }

    @Test
    public void merge() {
        ListNode l1 = new ListNode(1);
        l1.next = new ListNode(4);
        l1.next.next = new ListNode(5);

        ListNode l2 = new ListNode(1);
        l2.next = new ListNode(3);
        l2.next.next = new ListNode(4);

        ListNode l3 = new ListNode(2);
        l3.next = new ListNode(6);

        ListNode head = mergeKLists.merge(new ListNode[]{l1, l2, l3});

        List<Integer> vals = new ArrayList<>();

        ListNode current = head;
        while (current != null) {
            vals.add(current.val);
            current = current.next;
        }

        assertThat(vals).containsExactly(1, 1, 2, 3, 4, 4, 5, 6);
    }

    @Test
    public void mergeEmpty() {
        ListNode head = mergeKLists.merge(new ListNode[1]);

        assertThat(head).isNull();
    }
}