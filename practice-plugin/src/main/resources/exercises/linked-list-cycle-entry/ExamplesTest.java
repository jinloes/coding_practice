package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ExamplesTest {
    @Test
    void returnsEntryInsideLongerCycle() { Solution.Node[] nodes = nodes(1,2,3,4); nodes[3].next = nodes[1]; assertThat(Solution.cycleEntry(nodes[0])).isSameAs(nodes[1]); }
    @Test
    void returnsNullForAcyclicList() { Solution.Node[] nodes = nodes(5,6); assertThat(Solution.cycleEntry(nodes[0])).isNull(); }
    @Test
    void returnsSelfCycleNode() { Solution.Node node = new Solution.Node(9, null); node.next = node; assertThat(Solution.cycleEntry(node)).isSameAs(node); }
    private static Solution.Node[] nodes(int... values) { Solution.Node[] nodes = new Solution.Node[values.length]; for (int i = 0; i < values.length; i++) nodes[i] = new Solution.Node(values[i], null); for (int i = 0; i + 1 < nodes.length; i++) nodes[i].next = nodes[i + 1]; return nodes; }
}
