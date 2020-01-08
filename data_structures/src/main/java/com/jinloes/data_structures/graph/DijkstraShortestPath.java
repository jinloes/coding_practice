package com.jinloes.data_structures.graph;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class DijkstraShortestPath {

    public static <T> List<T> getShortestPath(Graph<T, DefaultWeightedEdge> graph, T start, T end) {
        Set<T> vertices = new HashSet<>(graph.vertexSet());
        Map<T, Double> distMap = vertices.stream()
                .collect(Collectors.toMap(Function.identity(), val -> Double.MAX_VALUE));
        Map<T, T> previousMap = new HashMap<>();
        vertices.forEach(vertex -> previousMap.put(vertex, null));
        Set<T> visited = new HashSet<>();
        distMap.put(start, 0D);
        while (!vertices.isEmpty()) {
            T current = findMinimumNotVisited(visited, distMap)
                    .orElse(null);
            visited.add(current);
            vertices.remove(current);
            double curDist = distMap.get(current);
            graph.edgesOf(current)
                    .forEach(edge -> {
                        T target = Graphs.getOppositeVertex(graph, edge, current);
                        double edgeWeight = graph.getEdgeWeight(edge);
                        if (distMap.get(target) > edgeWeight + curDist) {
                            distMap.put(target, edgeWeight + curDist);
                            previousMap.put(target, current);
                        }
                    });

        }
        return buildPath(previousMap, end);
    }

    private static <T> List<T> buildPath(Map<T, T> previousMap, T end) {
        List<T> shortestPath = new ArrayList<>();
        T cur = end;
        if (!previousMap.containsKey(end)) {
            return shortestPath;
        }
        while (cur != null) {
            shortestPath.add(0, cur);
            cur = previousMap.get(cur);
        }
        return shortestPath;

    }

    private static <T> Optional<T> findMinimumNotVisited(Set<T> visited, Map<T, Double> distMap) {
        return distMap.entrySet()
                .stream()
                .filter(entry -> !visited.contains(entry.getKey()) && entry.getValue() < Long.MAX_VALUE)
                .min(Comparator.comparing(Map.Entry::getValue))
                .map(Map.Entry::getKey);

    }
}
