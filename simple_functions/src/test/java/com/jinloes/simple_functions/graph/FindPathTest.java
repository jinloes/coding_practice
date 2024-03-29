package com.jinloes.simple_functions.graph;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class FindPathTest {
    @Test
    public void test() {
        Graph graph = new Graph(4);
        graph.addEdge(0, 1);
        graph.addEdge(0, 2);
        graph.addEdge(1, 2);
        graph.addEdge(2, 0);
        graph.addEdge(2, 3);
        graph.addEdge(3, 3);

        assertThat(FindPath.find(graph, 2, 1)).containsExactly(2, 0, 1);
    }
}