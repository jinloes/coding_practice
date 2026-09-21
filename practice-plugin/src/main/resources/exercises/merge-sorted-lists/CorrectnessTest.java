package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

class CorrectnessTest {
    @Test
    void returnsTheOtherListWhenOneIsEmpty() {
        assertMerged(new int[0], new int[]{2, 4}, 2, 4);
        assertMerged(new int[]{2, 4}, new int[0], 2, 4);
    }

    @Test
    void appendsWhenTheRangesDoNotOverlap() {
        assertMerged(new int[]{1, 2, 3}, new int[]{4, 5, 6}, 1, 2, 3, 4, 5, 6);
        assertMerged(new int[]{4, 5, 6}, new int[]{1, 2, 3}, 1, 2, 3, 4, 5, 6);
    }

    @Test
    void handlesDuplicateValuesAcrossBothLists() {
        assertMerged(new int[]{2, 2, 2}, new int[]{2, 2}, 2, 2, 2, 2, 2);
    }

    @Test
    void handlesListsOfVeryDifferentLengths() {
        assertMerged(new int[]{5}, new int[]{1, 2, 3, 4, 6, 7}, 1, 2, 3, 4, 5, 6, 7);
    }

    @Test
    void handlesNegativeValues() {
        assertMerged(new int[]{-9, -4, 0}, new int[]{-5, -4, 8}, -9, -5, -4, -4, 0, 8);
    }

    @Test
    void terminatesTheMergedList() {
        Solution.Node merged = merge(list(1, 3), list(2, 4), call(new int[]{1, 3}, new int[]{2, 4}));
        assertThat(merged).as("merge([1, 3], [2, 4]) must return a non-empty list").isNotNull();
        Solution.Node last = merged;
        for (int guard = 0; last.next != null && guard < 10; guard++) {
            last = last.next;
        }
        assertThat(last.next).as("merge([1, 3], [2, 4]) must terminate the merged list").isNull();
    }

    @Test
    void reusesExactlyTheSuppliedNodes() {
        int[] left = {1, 4, 9};
        int[] right = {2, 4, 5};
        Solution.Node first = list(left);
        Solution.Node second = list(right);
        Set<Solution.Node> supplied = identitySet(nodes(first, left.length), nodes(second, right.length));

        List<Solution.Node> merged = nodes(merge(first, second, call(left, right)), left.length + right.length);

        assertThat(identitySet(merged, List.of()))
                .as("merge(%s, %s) must splice exactly the nodes it was given",
                        Arrays.toString(left), Arrays.toString(right))
                .isEqualTo(supplied);
    }

    @Test
    void mergesLongLists() {
        int[] evens = new int[2_000];
        int[] odds = new int[2_000];
        int[] expected = new int[evens.length + odds.length];
        for (int i = 0; i < evens.length; i++) {
            evens[i] = i * 2;
            odds[i] = i * 2 + 1;
            expected[i * 2] = i * 2;
            expected[i * 2 + 1] = i * 2 + 1;
        }
        assertMerged(evens, odds, expected);
    }

    private static void assertMerged(int[] left, int[] right, int... expected) {
        String call = call(left, right);
        List<Integer> values = values(merge(list(left), list(right), call), expected.length + 1);
        assertThat(values).as("%s must return the merged values in order", call)
                .isEqualTo(Arrays.stream(expected).boxed().toList());
    }

    private static Solution.Node merge(Solution.Node first, Solution.Node second, String call) {
        try {
            return Solution.merge(first, second);
        } catch (Throwable thrown) {
            return fail("%s threw %s".formatted(call, thrown), thrown);
        }
    }

    private static Solution.Node list(int... values) {
        Solution.Node head = null;
        for (int index = values.length - 1; index >= 0; index--) {
            head = new Solution.Node(values[index], head);
        }
        return head;
    }

    private static List<Integer> values(Solution.Node head, int limit) {
        List<Integer> values = new ArrayList<>();
        for (Solution.Node node = head; node != null && values.size() < limit; node = node.next) {
            values.add(node.value);
        }
        return values;
    }

    private static List<Solution.Node> nodes(Solution.Node head, int limit) {
        List<Solution.Node> nodes = new ArrayList<>();
        for (Solution.Node node = head; node != null && nodes.size() < limit; node = node.next) {
            nodes.add(node);
        }
        return nodes;
    }

    private static Set<Solution.Node> identitySet(List<Solution.Node> first, List<Solution.Node> second) {
        Set<Solution.Node> identities = Collections.newSetFromMap(new IdentityHashMap<>());
        identities.addAll(first);
        identities.addAll(second);
        return identities;
    }

    private static String call(int[] left, int[] right) {
        return "merge(%s, %s)".formatted(render(left), render(right));
    }

    private static String render(int[] values) {
        return values.length <= 12
                ? Arrays.toString(values)
                : "[" + values[0] + ", " + values[1] + ", ... (" + values.length + " nodes)]";
    }
}
