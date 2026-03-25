package com.jinloes.simple_functions.graph;

import org.jgrapht.graph.DefaultDirectedWeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ShortestPathTest {

    @Test
    void findShortestPath_simpleGraph() {
        DefaultDirectedWeightedGraph<String, DefaultWeightedEdge> graph = new DefaultDirectedWeightedGraph<>(DefaultWeightedEdge.class);
        graph.addVertex("A");
        graph.addVertex("B");
        graph.setEdgeWeight(graph.addEdge("A", "B"), 5);

        assertThat(ShortestPath.findShortestPath(graph, "A", "B")).isEqualTo(List.of("A", "B"));
    }

    @Test
    void findShortestPath_complexGraph() {
        DefaultDirectedWeightedGraph<String, DefaultWeightedEdge> graph = new DefaultDirectedWeightedGraph<>(DefaultWeightedEdge.class);
        graph.addVertex("A");
        graph.addVertex("B");
        graph.addVertex("C");
        graph.addVertex("D");
        graph.setEdgeWeight(graph.addEdge("A", "B"), 5);
        graph.setEdgeWeight(graph.addEdge("A", "C"), 2);
        graph.setEdgeWeight(graph.addEdge("B", "D"), 3);
        graph.setEdgeWeight(graph.addEdge("C", "D"), 4);

        assertThat(ShortestPath.findShortestPath(graph, "A", "D")).isEqualTo(List.of("A", "C", "D"));
    }

    @Test
    void findShortestPath_complexGraph_alternateWeights() {
        DefaultDirectedWeightedGraph<String, DefaultWeightedEdge> graph = new DefaultDirectedWeightedGraph<>(DefaultWeightedEdge.class);
        graph.addVertex("A");
        graph.addVertex("B");
        graph.addVertex("C");
        graph.addVertex("D");
        graph.setEdgeWeight(graph.addEdge("A", "B"), 2);
        graph.setEdgeWeight(graph.addEdge("A", "C"), 5);
        graph.setEdgeWeight(graph.addEdge("B", "D"), 4);
        graph.setEdgeWeight(graph.addEdge("C", "D"), 3);

        assertThat(ShortestPath.findShortestPath(graph, "A", "D")).isEqualTo(List.of("A", "B", "D"));
    }

    @Test
    void findShortestPath_veryComplexGraph() {
        DefaultDirectedWeightedGraph<String, DefaultWeightedEdge> graph = new DefaultDirectedWeightedGraph<>(DefaultWeightedEdge.class);
        graph.addVertex("A");
        graph.addVertex("B");
        graph.addVertex("C");
        graph.addVertex("D");
        graph.addVertex("E");
        graph.addVertex("F");
        graph.addVertex("G");
        graph.setEdgeWeight(graph.addEdge("A", "B"), 5);
        graph.setEdgeWeight(graph.addEdge("A", "C"), 3);
        graph.setEdgeWeight(graph.addEdge("B", "C"), 1);
        graph.setEdgeWeight(graph.addEdge("B", "D"), 5);
        graph.setEdgeWeight(graph.addEdge("C", "B"), 1);
        graph.setEdgeWeight(graph.addEdge("D", "E"), 2);
        graph.setEdgeWeight(graph.addEdge("D", "F"), 4);
        graph.setEdgeWeight(graph.addEdge("E", "G"), 3);
        graph.setEdgeWeight(graph.addEdge("F", "G"), 2);
        graph.setEdgeWeight(graph.addEdge("C", "G"), 15);

        assertThat(ShortestPath.findShortestPath(graph, "A", "G"))
            .isEqualTo(List.of("A", "C", "B", "D", "E", "G"));
    }

    @Test
    void findShortestPath_emptyGraph() {
        DefaultDirectedWeightedGraph<String, DefaultWeightedEdge> graph = new DefaultDirectedWeightedGraph<>(DefaultWeightedEdge.class);
        assertThat(ShortestPath.findShortestPath(graph, "A", "B")).isEmpty();
    }

    @Test
    void findShortestPath_noPath() {
        DefaultDirectedWeightedGraph<String, DefaultWeightedEdge> graph = new DefaultDirectedWeightedGraph<>(DefaultWeightedEdge.class);
        graph.addVertex("A");
        graph.addVertex("B");
        assertThat(ShortestPath.findShortestPath(graph, "A", "B")).isEmpty();
    }

    @Test
    void shortestPathBfs_simpleGraph() {
        DefaultDirectedWeightedGraph<String, DefaultWeightedEdge> graph = new DefaultDirectedWeightedGraph<>(DefaultWeightedEdge.class);
        graph.addVertex("A");
        graph.addVertex("B");
        graph.setEdgeWeight(graph.addEdge("A", "B"), 5);

        assertThat(ShortestPath.shortestPathBfs(graph, "A", "B")).isEqualTo(List.of("A", "B"));
    }
}