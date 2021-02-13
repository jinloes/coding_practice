package com.jinloes.simple_functions.tree;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultEdge;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Given a directed graph, find if there is a route between two nodes.
 */
public class FindPath {
    public boolean pathExists(Graph<Integer, DefaultEdge> graph, Integer v1, Integer v2) {
        Set<Integer> visited = new HashSet<>();

        return pathExists(graph, v1, v2, visited);
    }

    private boolean pathExists(Graph<Integer, DefaultEdge> graph, Integer v1, Integer v2, Set<Integer> visited) {
        if (Objects.equals(v1, v2)) {
            return true;
        }

        List<Integer> neighbors = Graphs.neighborListOf(graph, v1);

        for (Integer neighbor : neighbors) {
            if (!visited.contains(neighbor)) {
                visited.add(neighbor);
                if (pathExists(graph, neighbor, v2, visited)) {
                    return true;
                }
            }
        }

        return false;
    }
}
