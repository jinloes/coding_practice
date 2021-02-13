package com.jinloes.simple_functions.linked_list;

import com.jinloes.simple_functions.ListNode;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class KthLastTest {
    private KthLast kthLast;

    @Before
    public void setUp() throws Exception {
        kthLast = new KthLast();
    }

    @Test
    public void kthLast() {
        ListNode head = new ListNode(1, new ListNode(2, new ListNode(3, new ListNode(4))));
        assertThat(kthLast.kthLast(head, 2)).isEqualTo(new ListNode(3, new ListNode(4)));
    }

    @Test
    public void kthLast2() {
        ListNode head = new ListNode(1, new ListNode(2, new ListNode(3, new ListNode(4))));
        assertThat(kthLast.kthLast(head, 3)).isEqualTo(new ListNode(2, new ListNode(3, new ListNode(4))));
    }

    @Test
    public void kthLastNull() {
        assertThat(kthLast.kthLast(null, 3)).isNull();
    }

    @Test
    public void kthLastSmaller() {
        ListNode head = new ListNode(1, new ListNode(2, new ListNode(3, new ListNode(4))));
        assertThat(kthLast.kthLast(head, 6)).isNull();
    }
}