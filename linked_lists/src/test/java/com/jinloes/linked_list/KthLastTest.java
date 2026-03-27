package com.jinloes.linked_list;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class KthLastTest {
    private KthLast kthLast;

    @BeforeEach
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