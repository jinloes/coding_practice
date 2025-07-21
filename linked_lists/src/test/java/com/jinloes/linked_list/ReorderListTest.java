package com.jinloes.linked_list;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class ReorderListTest {
    private ReorderList reorderList;

    @BeforeEach
    void setUp() {
        this.reorderList = new ReorderList();
    }

    @ParameterizedTest
    @MethodSource("reorderListArgs")
    void reorderList(List<Integer> input, List<Integer> expected) {
        ListNode inputHead = createLinkedList(input);
        ListNode expectedHead = createLinkedList(expected);

        reorderList.reorderList(inputHead);

        assertThat(listNodeToList(inputHead))
                .isEqualTo(listNodeToList(expectedHead));
    }

    private ListNode createLinkedList(List<Integer> input) {
        ListNode dummy = new ListNode();
        ListNode current = dummy;

        for (Integer num : input) {
            current.next = new ListNode(num);
            current = current.next;
        }
        return dummy.next;
    }

    private List<Integer> listNodeToList(ListNode input) {
        ListNode current = input;
        List<Integer> result = new ArrayList<>();

        while (current != null) {
            result.add(current.val);
            current = current.next;
        }

        return result;
    }

    private static Stream<Arguments> reorderListArgs() {
        return Stream.of(
                Arguments.of(List.of(2, 4, 6, 8), List.of(2, 8, 4, 6))
        );
    }

}