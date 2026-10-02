package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import java.util.ArrayDeque;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class CorrectnessTest {
    @Test
    void copiesAPath() {
        Solution.GraphNode original = build(5, new int[]{1, 2}, new int[]{2, 3}, new int[]{3, 4}, new int[]{4, 5});
        assertDeepCopy(original, Solution.cloneGraph(original));
    }

    @Test
    void copiesAStarStartingFromTheHub() {
        Solution.GraphNode original = build(6, new int[]{1, 2}, new int[]{1, 3}, new int[]{1, 4},
                new int[]{1, 5}, new int[]{1, 6});
        assertDeepCopy(original, Solution.cloneGraph(original));
    }

    @Test
    void copiesAGraphWhenStartedFromALeaf() {
        Solution.GraphNode original = build(4, new int[]{1, 2}, new int[]{2, 3}, new int[]{2, 4});
        Solution.GraphNode leaf = original.neighbors.get(0).neighbors.get(1);
        assertDeepCopy(leaf, Solution.cloneGraph(leaf));
    }

    @Test
    void copiesDenseCyclicGraph() {
        Solution.GraphNode original = build(5, new int[]{1, 2}, new int[]{1, 3}, new int[]{1, 4}, new int[]{1, 5},
                new int[]{2, 3}, new int[]{2, 4}, new int[]{2, 5}, new int[]{3, 4}, new int[]{3, 5}, new int[]{4, 5});
        assertDeepCopy(original, Solution.cloneGraph(original));
    }

    @Test
    void copiesSelfLoopsAndParallelEdges() {
        Solution.GraphNode original = build(2, new int[]{1, 1}, new int[]{1, 2}, new int[]{1, 2});
        assertDeepCopy(original, Solution.cloneGraph(original));
    }

    @Test
    void doesNotChangeTheOriginalGraph() {
        Solution.GraphNode original = build(3, new int[]{1, 2}, new int[]{2, 3});
        Solution.GraphNode second = original.neighbors.get(0);
        Solution.cloneGraph(original);
        assertThat(original.value).isEqualTo(1);
        assertThat(original.neighbors).hasSize(1).containsExactly(second);
        assertThat(second.neighbors).hasSize(2).first().isSameAs(original);
    }

    @Test
    void copiesLongRingWithoutRecursionTrouble() {
        int count = 20_000;
        int[][] edges = new int[count][];
        for (int i = 0; i < count; i++) {
            edges[i] = new int[]{i + 1, i + 1 == count ? 1 : i + 2};
        }
        Solution.GraphNode original = build(count, edges);
        assertDeepCopy(original, Solution.cloneGraph(original));
    }

    private static Solution.GraphNode build(int count, int[]... edges) {
        Solution.GraphNode[] nodes = new Solution.GraphNode[count + 1];
        for (int i = 1; i <= count; i++) {
            nodes[i] = new Solution.GraphNode(i);
        }
        for (int[] edge : edges) {
            nodes[edge[0]].neighbors.add(nodes[edge[1]]);
            if (edge[0] != edge[1]) {
                nodes[edge[1]].neighbors.add(nodes[edge[0]]);
            }
        }
        return nodes[1];
    }

    private static void assertDeepCopy(Solution.GraphNode original, Solution.GraphNode copy) {
        Set<Solution.GraphNode> originals = Collections.newSetFromMap(new IdentityHashMap<>());
        ArrayDeque<Solution.GraphNode> walk = new ArrayDeque<>();
        originals.add(original);
        walk.add(original);
        while (!walk.isEmpty()) {
            for (Solution.GraphNode next : walk.remove().neighbors) {
                if (originals.add(next)) {
                    walk.add(next);
                }
            }
        }
        assertThat(copy).as("copy of a non-null graph").isNotNull();
        Map<Solution.GraphNode, Solution.GraphNode> pairs = new IdentityHashMap<>();
        Set<Solution.GraphNode> copies = Collections.newSetFromMap(new IdentityHashMap<>());
        pairs.put(original, copy);
        copies.add(copy);
        walk.add(original);
        while (!walk.isEmpty()) {
            Solution.GraphNode source = walk.remove();
            Solution.GraphNode clone = pairs.get(source);
            assertThat(originals.contains(clone)).as("node %s reuses an original node", source.value).isFalse();
            assertThat(clone.value).isEqualTo(source.value);
            assertThat(clone.neighbors).as("neighbors of %s", source.value).hasSameSizeAs(source.neighbors);
            for (int i = 0; i < source.neighbors.size(); i++) {
                Solution.GraphNode sourceNeighbor = source.neighbors.get(i);
                Solution.GraphNode cloneNeighbor = clone.neighbors.get(i);
                Solution.GraphNode known = pairs.get(sourceNeighbor);
                if (known == null) {
                    assertThat(originals.contains(cloneNeighbor)).as("neighbor reuses an original node").isFalse();
                    assertThat(copies.add(cloneNeighbor)).as("one copy per original node").isTrue();
                    pairs.put(sourceNeighbor, cloneNeighbor);
                    walk.add(sourceNeighbor);
                } else {
                    assertThat(cloneNeighbor).as("neighbor %s must map to a single copy", sourceNeighbor.value)
                            .isSameAs(known);
                }
            }
        }
    }
}
