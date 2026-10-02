package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ExamplesTest {
    @Test
    void mergesThreeSortedLists() {
        Solution.Node[] lists = {chain(1, 4, 5), chain(1, 3, 4), chain(2, 6)};
        assertThat(values(Solution.mergeKLists(lists)))
                .as("mergeKLists of [1, 4, 5], [1, 3, 4], [2, 6]")
                .containsExactly(1, 1, 2, 3, 4, 4, 5, 6);
    }

    @Test
    void mergingNoListsReturnsNull() {
        assertThat(Solution.mergeKLists(new Solution.Node[0])).as("mergeKLists of an empty array").isNull();
    }

    @Test
    void skipsEmptyLists() {
        Solution.Node[] lists = {null, chain(-2, 7), null};
        assertThat(values(Solution.mergeKLists(lists)))
                .as("mergeKLists of null, [-2, 7], null")
                .containsExactly(-2, 7);
    }

    private static Solution.Node chain(int... values) {
        Solution.Node head = null;
        for (int i = values.length - 1; i >= 0; i--) {
            head = new Solution.Node(values[i], head);
        }
        return head;
    }

    private static List<Integer> values(Solution.Node head) {
        List<Integer> values = new ArrayList<>();
        for (Solution.Node node = head; node != null && values.size() < 1000; node = node.next) {
            values.add(node.value);
        }
        return values;
    }
}
