package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ExamplesTest {
    @Test
    void acyclicListHasNoCycle() {
        Solution.Node head = new Solution.Node(1, new Solution.Node(2, new Solution.Node(3, null)));
        assertThat(Solution.hasCycle(head)).as("hasCycle of the acyclic list [1, 2, 3]").isFalse();
    }

    @Test
    void tailLinkedBackToTheSecondNodeIsACycle() {
        Solution.Node first = new Solution.Node(3, null);
        Solution.Node second = new Solution.Node(2, null);
        Solution.Node third = new Solution.Node(0, null);
        Solution.Node fourth = new Solution.Node(-4, null);
        first.next = second;
        second.next = third;
        third.next = fourth;
        fourth.next = second;
        assertThat(Solution.hasCycle(first)).as("hasCycle of [3, 2, 0, -4] with the tail linked to index 1").isTrue();
    }

    @Test
    void nodeLinkedToItselfIsACycle() {
        Solution.Node node = new Solution.Node(5, null);
        node.next = node;
        assertThat(Solution.hasCycle(node)).as("hasCycle of a single node linked to itself").isTrue();
    }
}
