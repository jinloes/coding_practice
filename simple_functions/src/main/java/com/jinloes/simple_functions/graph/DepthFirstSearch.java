package com.jinloes.simple_functions.graph;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DepthFirstSearch {
    public static List<Integer> execute(Graph graph, int vertex) {
        Set<Integer> visited = new HashSet<>();
        List<Integer> path = new ArrayList<>();
        searchInternal(graph, vertex, path, visited);
        return path;
    }

    private static void searchInternal(Graph graph, int vertex, List<Integer> path, Set<Integer> visited) {
        path.add(vertex);
        visited.add(vertex);
        graph.getAdjacencyList(vertex)
                .stream()
                .filter(neighbor -> !visited.contains(neighbor))
                .forEach(neighbor -> searchInternal(graph, neighbor, path, visited));
    }
}
