package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

class CorrectnessTest {
    @Test
    void emptyListHasNoCycle() {
        assertThat(hasCycle(null)).as("hasCycle(null)").isFalse();
    }

    @Test
    void singleNodeWithoutALinkHasNoCycle() {
        assertThat(hasCycle(build(new int[]{7}, -1))).as("hasCycle of the one-node list [7]").isFalse();
    }

    @Test
    void singleNodeLinkedToItselfIsACycle() {
        assertThat(hasCycle(build(new int[]{7}, 0))).as("hasCycle of one node linked to itself").isTrue();
    }

    @Test
    void twoNodesLinkedBackToTheHeadAreACycle() {
        assertThat(hasCycle(build(new int[]{1, 2}, 0))).as("hasCycle of [1, 2] with the tail linked to the head").isTrue();
    }

    @Test
    void twoNodesWithoutALinkHaveNoCycle() {
        assertThat(hasCycle(build(new int[]{1, 2}, -1))).as("hasCycle of the acyclic list [1, 2]").isFalse();
    }

    @Test
    void tailLinkedToItselfIsACycle() {
        assertThat(hasCycle(build(new int[]{1, 2, 3, 4}, 3)))
                .as("hasCycle of [1, 2, 3, 4] with the tail linked to itself").isTrue();
    }

    @Test
    void cycleStartingInTheMiddleIsFound() {
        assertThat(hasCycle(build(new int[]{1, 2, 3, 4, 5, 6, 7}, 3)))
                .as("hasCycle of seven nodes with the tail linked to index 3").isTrue();
    }

    @Test
    void equalValuesDoNotCountAsACycle() {
        assertThat(hasCycle(build(new int[]{4, 4, 4, 4, 4}, -1)))
                .as("hasCycle of the acyclic list [4, 4, 4, 4, 4]").isFalse();
    }

    @Test
    void equalValuesInACycleAreStillACycle() {
        assertThat(hasCycle(build(new int[]{4, 4, 4, 4, 4}, 2)))
                .as("hasCycle of five equal values with the tail linked to index 2").isTrue();
    }

    @Test
    void longAcyclicListHasNoCycle() {
        int[] values = new int[100000];
        for (int i = 0; i < values.length; i++) {
            values[i] = i - 50000;
        }
        assertThat(hasCycle(build(values, -1))).as("hasCycle of a 100000-node acyclic list").isFalse();
    }

    @Test
    void longListWithACycleNearTheTailIsFound() {
        int[] values = new int[100000];
        assertThat(hasCycle(build(values, 99990)))
                .as("hasCycle of 100000 nodes with the tail linked to index 99990").isTrue();
    }

    private static boolean hasCycle(Solution.Node head) {
        try {
            return Solution.hasCycle(head);
        } catch (Throwable thrown) {
            return fail("hasCycle threw %s".formatted(thrown), thrown);
        }
    }

    private static Solution.Node build(int[] values, int tailLinksTo) {
        Solution.Node[] nodes = new Solution.Node[values.length];
        for (int i = values.length - 1; i >= 0; i--) {
            nodes[i] = new Solution.Node(values[i], i + 1 < values.length ? nodes[i + 1] : null);
        }
        if (tailLinksTo >= 0) {
            nodes[values.length - 1].next = nodes[tailLinksTo];
        }
        return nodes[0];
    }
}
