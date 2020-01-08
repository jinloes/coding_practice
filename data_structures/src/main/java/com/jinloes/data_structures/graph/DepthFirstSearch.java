package com.jinloes.data_structures.graph;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * DFS visits all the children before visiting the root. 
 */
public class DepthFirstSearch {

    public static <T> boolean search(Graph<T, DefaultEdge> graph, T value) {
        Set<T> visited = new HashSet<>();
        Set<T> vertices = graph.vertexSet();
        for (T vertex : vertices) {
            if (searchInternal(visited, graph, vertex, value)) {
                return true;
            }
        }
        return false;
    }

    private static <T> boolean searchInternal(Set<T> visited, Graph<T, DefaultEdge> graph, T current, T value) {
        if (Objects.equals(current, value)) {
            return true;
        }
        Set<T> toVisit = graph.edgesOf(current)
                .stream()
                .map(graph::getEdgeTarget)
                .filter(target -> !visited.contains(target))
                .peek(visited::add)
                .collect(Collectors.toSet());
        for (T vertex : toVisit) {
            if (searchInternal(visited, graph, vertex, value)) {
                return true;
            }
        }
        return false;
    }
}
