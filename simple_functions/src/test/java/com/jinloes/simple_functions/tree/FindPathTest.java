package com.jinloes.simple_functions.tree;


import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class FindPathTest {
    private FindPath findPath;

    @Before
    public void setUp() throws Exception {
        findPath = new FindPath();
    }

    @Test
    public void pathExists() {
        Graph<Integer, DefaultEdge> graph = new SimpleGraph<>(DefaultEdge.class);

        graph.addVertex(1);
        graph.addVertex(2);
        graph.addVertex(3);
        graph.addVertex(4); // no path
        graph.addVertex(5); // no path
        graph.addEdge(1, 2);
        graph.addEdge(1, 3);
        graph.addEdge(3, 5);

        assertThat(findPath.pathExists(graph, 1, 2)).isTrue();
        assertThat(findPath.pathExists(graph, 1, 4)).isFalse();
        assertThat(findPath.pathExists(graph, 1, 5)).isTrue();
    }
}