package com.jinloes.practice;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ExamplesTest {
    @Test
    void sampleHasAValidOrder() {
        int[] groups = {-1,-1,1,0,0,1,0,-1};
        List<List<Integer>> before = List.of(List.of(), List.of(6), List.of(5), List.of(6), List.of(3,6), List.of(), List.of(), List.of());
        checkValid(8, 2, groups, before, Solution.sortItems(8, 2, groups, before));
    }

    @Test
    void cycleInsideGroupReturnsEmpty() {
        assertThat(Solution.sortItems(2, 1, new int[]{0,0}, List.of(List.of(1), List.of(0)))).isEmpty();
    }

    private static void checkValid(int n, int m, int[] groups, List<List<Integer>> before, int[] order) {
        assertThat(order).hasSize(n);
        assertThat(Arrays.stream(order).boxed()).containsExactlyInAnyOrderElementsOf(java.util.stream.IntStream.range(0, n).boxed().toList());
        Map<Integer, Integer> position = new HashMap<>();
        for (int i = 0; i < order.length; i++) position.put(order[i], i);
        for (int item = 0; item < n; item++) for (int prerequisite : before.get(item)) assertThat(position.get(prerequisite)).isLessThan(position.get(item));
        int[] normalized = groups.clone();
        int nextGroup = m;
        for (int i = 0; i < n; i++) if (normalized[i] == -1) normalized[i] = nextGroup++;
        Set<Integer> closed = new HashSet<>();
        int current = -1;
        for (int item : order) { int group = normalized[item]; if (group != current) { assertThat(closed).doesNotContain(group); if (current != -1) closed.add(current); current = group; } }
    }
}
