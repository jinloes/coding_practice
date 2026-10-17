package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CorrectnessTest {
    @Test
    void handlesNullHead() { assertThat(Solution.cycleEntry(null)).isNull(); }
    @Test
    void detectsCycleAtHead() { Solution.Node[] nodes = nodes(1,2,3); nodes[2].next = nodes[0]; assertThat(Solution.cycleEntry(nodes[0])).isSameAs(nodes[0]); }
    @Test
    void detectsEntryAfterLongTail() { Solution.Node[] nodes = nodes(0,1,2,3,4,5,6,7); nodes[7].next = nodes[5]; assertThat(Solution.cycleEntry(nodes[0])).isSameAs(nodes[5]); }
    @Test
    void doesNotConfuseEqualValuesWithIdentity() { Solution.Node[] nodes = nodes(4,4,4,4); nodes[3].next = nodes[2]; assertThat(Solution.cycleEntry(nodes[0])).isSameAs(nodes[2]); }
    @Test
    void leavesAcyclicPointersUntouched() { Solution.Node[] nodes = nodes(1,2,3); Solution.cycleEntry(nodes[0]); assertThat(nodes[0].next).isSameAs(nodes[1]); assertThat(nodes[1].next).isSameAs(nodes[2]); assertThat(nodes[2].next).isNull(); }
    private static Solution.Node[] nodes(int... values) { Solution.Node[] nodes = new Solution.Node[values.length]; for (int i = 0; i < values.length; i++) nodes[i] = new Solution.Node(values[i], null); for (int i = 0; i + 1 < nodes.length; i++) nodes[i].next = nodes[i + 1]; return nodes; }
}
