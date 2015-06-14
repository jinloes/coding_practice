package com.jinloes.simple_functions;

import org.jgrapht.graph.DefaultDirectedWeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;

import java.util.*;

/**
 * Finds the shortest path in a graph from start to end.
 */
public class ShortestPath {
    public static List<String> findShortestPath(DefaultDirectedWeightedGraph<String, DefaultWeightedEdge> graph, String start,
                                                String end) {
        Map<String, Double> pathWeight = new HashMap<>();
        Map<String, DefaultWeightedEdge> pathEdge = new HashMap<>();
        List<String> path = new ArrayList<>();
        if (graph.vertexSet().isEmpty() || !graph.containsVertex(start) || !graph.containsVertex(end)) {
            return path;
        }
        Stack<String> vertices = new Stack<>();
        pathWeight.put(start, 0.0);
        vertices.push(start);
        while(!vertices.empty()) {
            String vertex = vertices.pop();
            Set<DefaultWeightedEdge> edges = graph.outgoingEdgesOf(vertex);
            for (DefaultWeightedEdge edge : edges) {
                String target = graph.getEdgeTarget(edge);
                double weight = graph.getEdgeWeight(edge);
                double targetPathWeight = weight + pathWeight.get(vertex);
                if (!pathWeight.containsKey(target)) {
                    pathWeight.put(target, targetPathWeight);
                    pathEdge.put(target, edge);
                    vertices.push(target);
                } else if(targetPathWeight < pathWeight.get(target)) {
                    pathWeight.put(target, targetPathWeight);
                    pathEdge.put(target, edge);
                }
            }
        }
        return buildPath(graph, start, end, pathEdge);
    }

    private static List<String> buildPath(DefaultDirectedWeightedGraph<String, DefaultWeightedEdge> graph, String start,
                                  String end, Map<String, DefaultWeightedEdge> pathEdge) {
        List<String> path = new ArrayList<>();
        DefaultWeightedEdge startEdge = pathEdge.get(end);
        // If the start edge is null it meant there was no path to the target vertex
        if(startEdge == null) {
            return path;
        }
        path.add(0, graph.getEdgeTarget(startEdge));
        while(!graph.getEdgeSource(startEdge).equals(start)) {
            path.add(0, graph.getEdgeSource(startEdge));
            startEdge = pathEdge.get(graph.getEdgeSource(startEdge));
        }
        path.add(0, graph.getEdgeSource(startEdge));
        return path;
    }
}
