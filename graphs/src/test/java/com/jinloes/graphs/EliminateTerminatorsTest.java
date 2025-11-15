package com.jinloes.graphs;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EliminateTerminatorsTest {

    @Test
    void smallDAGProducesValidOrder() {
        int n = 4;
        int[][] deps = {
                {0, 1},
                {0, 2},
                {1, 3},
                {2, 3}
        };

        List<Integer> order = EliminateTerminators.topologicalSort(n, deps);
        assertThat(order).hasSize(n);

        Map<Integer, Integer> idx = indexMap(order);
        for (int[] pair : deps) {
            assertThat(idx.get(pair[0])).isLessThan(idx.get(pair[1]));
        }
    }

    @Test
    void smallCycleResultsInEmptyList() {
        int n = 3;
        int[][] deps = {
                {0, 1},
                {1, 2},
                {2, 0}
        };
        List<Integer> order = EliminateTerminators.topologicalSort(n, deps);
        assertThat(order).isEmpty();
    }

    @Test
    void nullAndEmptyDepsReturnNaturalOrder() {
        int n = 3;
        List<Integer> orderNull = EliminateTerminators.topologicalSort(n, null);
        assertThat(orderNull).containsExactly(0, 1, 2);

        int[][] empty = new int[0][];
        List<Integer> orderEmpty = EliminateTerminators.topologicalSort(n, empty);
        assertThat(orderEmpty).containsExactly(0, 1, 2);
    }

    @Test
    void malformedAndNullPairsAreIgnored() {
        int n = 4;
        int[][] deps = {
                {0, 1},
                null,
                new int[0],
                new int[]{2}, // length < 2 should be ignored
                {1, 2},
                {2, 3}
        };

        List<Integer> order = EliminateTerminators.topologicalSort(n, deps);
        assertThat(order).hasSize(n);
        Map<Integer, Integer> idx = indexMap(order);
        assertThat(idx.get(0)).isLessThan(idx.get(1));
        assertThat(idx.get(1)).isLessThan(idx.get(2));
        assertThat(idx.get(2)).isLessThan(idx.get(3));
    }

    @Test
    void invalidNAndOutOfRangePairsThrow() {
        assertThatThrownBy(() -> EliminateTerminators.topologicalSort(0, null))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> EliminateTerminators.topologicalSort(-5, null))
                .isInstanceOf(IllegalArgumentException.class);

        int n = 3;
        int[][] outOfRange = {{0, 4}};
        assertThatThrownBy(() -> EliminateTerminators.topologicalSort(n, outOfRange))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void largeGraphTopologicalSort() {
        int n = 5000;
        List<int[]> depsList = new ArrayList<>(n);

        // chain 0 -> 1 -> 2 -> ... -> n-1
        for (int i = 0; i < n - 1; i++) {
            depsList.add(new int[]{i, i + 1});
        }

        // occasional skip edges to increase complexity
        for (int i = 0; i + 100 < n; i += 100) {
            depsList.add(new int[]{i, i + 100});
        }

        int[][] deps = new int[depsList.size()][];
        for (int i = 0; i < depsList.size(); i++) {
            deps[i] = depsList.get(i);
        }

        List<Integer> order = EliminateTerminators.topologicalSort(n, deps);
        assertThat(order).hasSize(n);

        Map<Integer, Integer> idx = indexMap(order);
        for (int[] pair : deps) {
            assertThat(idx.get(pair[0])).isLessThan(idx.get(pair[1]));
        }
    }

    private Map<Integer, Integer> indexMap(List<Integer> order) {
        Map<Integer, Integer> idx = new HashMap<>(order.size());
        for (int i = 0; i < order.size(); i++) {
            idx.put(order.get(i), i);
        }
        return idx;
    }
}