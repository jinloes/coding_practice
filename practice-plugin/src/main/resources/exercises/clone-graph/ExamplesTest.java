package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import java.util.ArrayDeque;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class ExamplesTest {
    @Test
    void nullInputReturnsNull() {
        assertThat(Solution.cloneGraph(null)).isNull();
    }

    @Test
    void copiesASingleIsolatedNode() {
        Solution.GraphNode original = new Solution.GraphNode(7);
        Solution.GraphNode copy = Solution.cloneGraph(original);
        assertDeepCopy(original, copy);
        assertThat(copy.neighbors).isEmpty();
    }

    @Test
    void copiesATriangle() {
        Solution.GraphNode original = build(3, new int[]{1, 2}, new int[]{2, 3}, new int[]{3, 1});
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
