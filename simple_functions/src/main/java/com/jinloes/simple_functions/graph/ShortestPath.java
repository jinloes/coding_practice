package com.jinloes.simple_functions.graph;

import org.jgrapht.graph.DefaultDirectedWeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;

import java.util.*;

/**
 * Finds the shortest path in a graph from start to end.
 */
public class ShortestPath {
    /**
     * Executes a shortest path search using depth first search.
     * A major advantage of DFS is that you don't need to store every vertex at each level.
     * DFS is more advantageous if you know your solution is going to be at the bottom of the graph/tree so you don't
     * have to look at each level first.
     *
     * @param graph graph to search
     * @param start start vertex
     * @param end   end vertex
     * @return shortest path from start to end
     */
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
        while (!vertices.empty()) {
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
                } else if (targetPathWeight < pathWeight.get(target)) {
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
        if (startEdge == null) {
            return path;
        }
        path.add(0, graph.getEdgeTarget(startEdge));
        while (!graph.getEdgeSource(startEdge).equals(start)) {
            path.add(0, graph.getEdgeSource(startEdge));
            startEdge = pathEdge.get(graph.getEdgeSource(startEdge));
        }
        path.add(0, graph.getEdgeSource(startEdge));
        return path;
    }

    /**
     * Performs a breadth first search to find the shortest path from one point to another in a graph.
     * A major downside to breadth first search is that you need to store all the vertices at a level.
     * BFS would find a solution to a problem if the answer is close to the root.
     *
     * @param graph graph
     * @param start start vertex
     * @param end   end vertex
     * @return the shortest path from start to finish
     */
    public static List<String> shortestPathBfs(DefaultDirectedWeightedGraph<String, DefaultWeightedEdge> graph,
                                               String start, String end) {
        Map<String, Double> pathWeightMap = new HashMap<>();
        Map<String, String> pathSourceMap = new HashMap<>();
        List<String> path = new ArrayList<>();
        if (graph.vertexSet().isEmpty()) {
            return path;
        }
        Queue<String> vertices = new ArrayDeque<>();
        vertices.add(start);
        pathWeightMap.put(start, 0.0);
        while (!vertices.isEmpty()) {
            String vertex = vertices.poll();
            for (DefaultWeightedEdge edge : graph.outgoingEdgesOf(vertex)) {
                String target = graph.getEdgeTarget(edge);
                double pathWeight = pathWeightMap.get(vertex) + graph.getEdgeWeight(edge);
                if (!pathWeightMap.containsKey(target)) {
                    pathWeightMap.put(target, pathWeight);
                    pathSourceMap.put(target, vertex);
                    vertices.add(target);
                } else if (pathWeight < pathWeightMap.get(target)) {
                    pathWeightMap.put(target, pathWeight);
                    pathSourceMap.put(target, vertex);
                }
            }
        }

        return buildBfsPath(start, end, pathSourceMap);
    }

    private static List<String> buildBfsPath(String start, String end, Map<String, String> pathSourceMap) {
        List<String> path = new ArrayList<>();
        if (!pathSourceMap.containsKey(end)) {
            // There was no path to end from start
            return path;
        }
        String pathPosition = end;
        path.add(0, pathPosition);
        while (!Objects.equals(pathPosition, start)) {
            pathPosition = pathSourceMap.get(pathPosition);
            path.add(0, pathPosition);
        }
        return path;
    }
}
