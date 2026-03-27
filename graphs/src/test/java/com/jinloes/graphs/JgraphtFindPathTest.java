package com.jinloes.graphs;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class JgraphtFindPathTest {
    private JgraphtFindPath findPath;

    @BeforeEach
    void setUp() {
        findPath = new JgraphtFindPath();
    }

    @Test
    void pathExists() {
        Graph<Integer, DefaultEdge> graph = new SimpleGraph<>(DefaultEdge.class);
        graph.addVertex(1);
        graph.addVertex(2);
        graph.addVertex(3);
        graph.addVertex(4);
        graph.addVertex(5);
        graph.addEdge(1, 2);
        graph.addEdge(1, 3);
        graph.addEdge(3, 5);

        assertThat(findPath.pathExists(graph, 1, 2)).isTrue();
        assertThat(findPath.pathExists(graph, 1, 4)).isFalse();
        assertThat(findPath.pathExists(graph, 1, 5)).isTrue();
    }
}
