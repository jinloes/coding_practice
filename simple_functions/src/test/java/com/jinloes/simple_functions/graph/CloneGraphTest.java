package com.jinloes.simple_functions.graph;

import org.assertj.core.util.Lists;
import org.junit.Before;
import org.junit.Test;

public class CloneGraphTest {
    private CloneGraph cloneGraph;

    @Before
    public void setUp() throws Exception {
        cloneGraph = new CloneGraph();
    }

    @Test
    public void cloneGraph() {

        CloneGraph.Node v4 = new CloneGraph.Node(4);
        CloneGraph.Node v3 = new CloneGraph.Node(3, Lists.newArrayList(v4));
        CloneGraph.Node v2 = new CloneGraph.Node(2, Lists.newArrayList(v3));
        CloneGraph.Node v1 = new CloneGraph.Node(1, Lists.newArrayList(v2, v4));
        v2.neighbors.add(v1);
        v3.neighbors.add(v2);
        v4.neighbors.add(v1);
        v4.neighbors.add(v3);

        CloneGraph.Node result = cloneGraph.cloneGraph(v1);
    }
}