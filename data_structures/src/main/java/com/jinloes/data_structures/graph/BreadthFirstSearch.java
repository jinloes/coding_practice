package com.jinloes.data_structures.graph;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;

import java.util.*;
import java.util.stream.Collectors;

/**
 * BFS on a graph uses a queue to add all the adjacent vertices. Visited vertices get marked so there isn't an
 * infinite loop.
 */
public class BreadthFirstSearch {

    public static <T> boolean search(Graph<T, DefaultEdge> graph, T value) {
        Queue<T> childQueue = new ArrayDeque<>(graph.vertexSet());
        Set<T> visited = new HashSet<>();

        while (!childQueue.isEmpty()) {
            T child = childQueue.poll();
            if (Objects.equals(child, value)) {
                return true;
            }
            Set<T> childTargets = graph.edgesOf(child)
                    .stream()
                    .map(graph::getEdgeTarget)
                    .filter(target -> !visited.contains(target))
                    .peek(visited::add)
                    .collect(Collectors.toSet());
            childQueue.addAll(childTargets);
        }
        return false;

    }
}
