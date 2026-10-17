package com.jinloes.practice;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CorrectnessTest {
    @Test
    void independentItemsCanStayInAnyGroupBlocks() {
        int[] groups = {0, 0, 1, 1};
        int[] order = Solution.sortItems(4, 2, groups, List.of(List.of(), List.of(), List.of(), List.of()));
        checkValid(4, 2, groups, List.of(List.of(), List.of(), List.of(), List.of()), order);
        assertThat(groups).containsExactly(0, 0, 1, 1);
    }

    @Test
    void dependencyBetweenGroupsOrdersWholeBlocks() {
        int[] groups = {0, 0, 1, 1};
        List<List<Integer>> before = List.of(List.of(), List.of(), List.of(1), List.of());
        checkValid(4, 2, groups, before, Solution.sortItems(4, 2, groups, before));
    }

    @Test
    void groupCycleReturnsEmpty() {
        assertThat(Solution.sortItems(2, 2, new int[]{0,1}, List.of(List.of(1), List.of(0)))).isEmpty();
    }

    @Test
    void ungroupedItemsBecomeTheirOwnBlocks() {
        int[] groups = {-1, 0, -1, 0};
        List<List<Integer>> before = List.of(List.of(), List.of(0), List.of(1), List.of());
        checkValid(4, 1, groups, before, Solution.sortItems(4, 1, groups, before));
    }

    @Test
    void largerChainWithGroupsIsValid() {
        int n = 30;
        int[] groups = new int[n];
        java.util.ArrayList<java.util.ArrayList<Integer>> mutable = new java.util.ArrayList<>();
        for (int i = 0; i < n; i++) { groups[i] = i / 3; mutable.add(new java.util.ArrayList<>()); if (i > 0) mutable.get(i).add(i - 1); }
        List<List<Integer>> before = mutable.stream().map(list -> (List<Integer>) List.copyOf(list)).toList();
        checkValid(n, 10, groups, before, Solution.sortItems(n, 10, groups, before));
    }

    static void checkValid(int n, int m, int[] groups, List<List<Integer>> before, int[] order) {
        assertThat(order).as("order").hasSize(n);
        assertThat(Arrays.stream(order).boxed()).containsExactlyInAnyOrderElementsOf(java.util.stream.IntStream.range(0, n).boxed().toList());
        Map<Integer, Integer> position = new HashMap<>();
        for (int i = 0; i < order.length; i++) position.put(order[i], i);
        for (int item = 0; item < n; item++) for (int prerequisite : before.get(item)) assertThat(position.get(prerequisite)).as("prerequisite " + prerequisite + " before " + item).isLessThan(position.get(item));
        int[] normalized = groups.clone();
        int nextGroup = m;
        for (int i = 0; i < n; i++) if (normalized[i] == -1) normalized[i] = nextGroup++;
        Set<Integer> closed = new HashSet<>();
        int current = -1;
        for (int item : order) { int group = normalized[item]; if (group != current) { assertThat(closed).as("group " + group + " contiguous in " + Arrays.toString(order)).doesNotContain(group); if (current != -1) closed.add(current); current = group; } }
    }
}
