package com.jinloes.simple_functions.graph;

import java.util.*;

public class BreadthFirstSearch {

    public static List<Integer> execute(Graph graph, int vertex) {
        Set<Integer> visited = new HashSet<>();
        Queue<Integer> toVisit = new ArrayDeque<>();
        List<Integer> path = new ArrayList<>();
        toVisit.add(vertex);
        while (!toVisit.isEmpty()) {
            Integer currentVertex = toVisit.poll();
            path.add(currentVertex);
            visited.add(currentVertex);
            List<Integer> adjacencyList = graph.getAdjacencyList(currentVertex);
            adjacencyList.stream()
                    .filter(current -> !visited.contains(current))
                    .forEach(toVisit::add);
        }
        return path;
    }
}
