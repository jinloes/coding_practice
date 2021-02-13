package com.jinloes.simple_functions.linked_list;


import com.jinloes.simple_functions.ListNode;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class RemoveDuplicatesTest {
    private RemoveDuplicates removeDuplicates;
    private ListNode head;

    @Before
    public void setUp() throws Exception {
        removeDuplicates = new RemoveDuplicates();
        head = new ListNode(1, new ListNode(2, new ListNode(3, new ListNode(3, new ListNode(4)))));
    }

    @Test
    public void removeDuplicates() {
        assertThat(removeDuplicates.removeDuplicates(head))
                .isEqualTo(new ListNode(1, new ListNode(2, new ListNode(3, new ListNode(4)))));
    }

    @Test
    public void removeDuplicatesSingle() {
        assertThat(removeDuplicates.removeDuplicates(new ListNode(1)))
                .isEqualTo(new ListNode(1));
    }

    @Test
    public void removeDuplicatesDouble() {
        assertThat(removeDuplicates.removeDuplicates(new ListNode(1, new ListNode(1))))
                .isEqualTo(new ListNode(1));
    }

    @Test
    public void removeDuplicatesEnd() {
        assertThat(removeDuplicates.removeDuplicates(
                new ListNode(1, new ListNode(2, new ListNode(3, new ListNode(3, new ListNode(4, new ListNode(4, new ListNode(4)))))))))
                .isEqualTo(new ListNode(1, new ListNode(2, new ListNode(3, new ListNode(4)))));
    }
}