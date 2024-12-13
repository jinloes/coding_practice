package com.jinloes.data_structures.graph;

import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class DijkstraShortestPathTest {
    private SimpleWeightedGraph<Integer, DefaultWeightedEdge> weightedGraph;

    @BeforeEach
    public void setUp() {
        weightedGraph = new SimpleWeightedGraph<>(DefaultWeightedEdge.class);

        Integer v0 = 0;
        Integer v1 = 1;
        Integer v2 = 2;
        Integer v3 = 3;
        Integer v4 = 4;
        Integer v5 = 5;
        Integer v6 = 6;
        Integer v7 = 7;
        Integer v8 = 8;

        weightedGraph.addVertex(v0);
        weightedGraph.addVertex(v1);
        weightedGraph.addVertex(v2);
        weightedGraph.addVertex(v3);
        weightedGraph.addVertex(v4);
        weightedGraph.addVertex(v5);
        weightedGraph.addVertex(v6);
        weightedGraph.addVertex(v7);
        weightedGraph.addVertex(v8);

        Graphs.addEdgeWithVertices(weightedGraph, v0, v1, 4);
        Graphs.addEdgeWithVertices(weightedGraph, v0, v7, 8);
        Graphs.addEdgeWithVertices(weightedGraph, v1, v2, 8);
        Graphs.addEdgeWithVertices(weightedGraph, v1, v7, 11);
        Graphs.addEdgeWithVertices(weightedGraph, v2, v3, 7);
        Graphs.addEdgeWithVertices(weightedGraph, v2, v5, 4);
        Graphs.addEdgeWithVertices(weightedGraph, v2, v8, 2);
        Graphs.addEdgeWithVertices(weightedGraph, v3, v4, 9);
        Graphs.addEdgeWithVertices(weightedGraph, v3, v5, 14);
        Graphs.addEdgeWithVertices(weightedGraph, v4, v5, 10);
        Graphs.addEdgeWithVertices(weightedGraph, v5, v6, 2);
        Graphs.addEdgeWithVertices(weightedGraph, v6, v7, 1);
        Graphs.addEdgeWithVertices(weightedGraph, v6, v8, 6);
        Graphs.addEdgeWithVertices(weightedGraph, v7, v8, 7);

        System.out.println(weightedGraph);
    }

    @Test
    public void testGetShortestPath() {
        assertThat(DijkstraShortestPath.getShortestPath(weightedGraph, 0, 4))
                .containsExactly(0, 7, 6, 5, 4);

        assertThat(DijkstraShortestPath.getShortestPath(weightedGraph, 0, 100))
                .isEmpty();

    }
}