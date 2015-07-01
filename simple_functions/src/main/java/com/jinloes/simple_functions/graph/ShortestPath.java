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
        Map<String, List<String>> shortestPathMap = new HashMap<>();
        List<String> path = new ArrayList<>();
        if (graph.vertexSet().isEmpty() || !graph.containsVertex(start) || !graph.containsVertex(end)) {
            return path;
        }
        Stack<String> vertices = new Stack<>();
        pathWeight.put(start, 0.0);
        List<String> shortestPath = new ArrayList<>();
        shortestPath.add(start);
        shortestPathMap.put(start, shortestPath);
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
                    shortestPath = shortestPathMap.get(vertex);
                    List<String> currentShortestPath = new ArrayList<>(shortestPath);
                    currentShortestPath.add(target);
                    shortestPathMap.put(target, currentShortestPath);
                    vertices.push(target);
                } else if (targetPathWeight < pathWeight.get(target)) {
                    pathWeight.put(target, targetPathWeight);
                    shortestPath = shortestPathMap.get(vertex);
                    List<String> currentShortestPath = new ArrayList<>(shortestPath);
                    currentShortestPath.add(target);
                    shortestPathMap.put(target, currentShortestPath);
                }
            }
        }
        // If the start edge is null it meant there was no path to the target vertex
        if (!shortestPathMap.containsKey(end)) {
            return new ArrayList<>();
        }
        return shortestPathMap.get(end);
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
        Map<String, List<String>> pathSourceMap = new HashMap<>();
        List<String> path = new ArrayList<>();
        if (graph.vertexSet().isEmpty()) {
            return path;
        }
        Queue<String> vertices = new ArrayDeque<>();
        vertices.add(start);
        pathWeightMap.put(start, 0.0);
        List<String> shortestPath = new ArrayList<>();
        shortestPath.add(start);
        pathSourceMap.put(start, shortestPath);
        while (!vertices.isEmpty()) {
            String vertex = vertices.poll();
            for (DefaultWeightedEdge edge : graph.outgoingEdgesOf(vertex)) {
                String target = graph.getEdgeTarget(edge);
                double pathWeight = pathWeightMap.get(vertex) + graph.getEdgeWeight(edge);
                if (!pathWeightMap.containsKey(target)) {
                    pathWeightMap.put(target, pathWeight);
                    List<String> sourceShortestPath = pathSourceMap.get(vertex);
                    shortestPath = new ArrayList<>(sourceShortestPath);
                    shortestPath.add(target);
                    pathSourceMap.put(target, shortestPath);
                    vertices.add(target);
                } else if (pathWeight < pathWeightMap.get(target)) {
                    pathWeightMap.put(target, pathWeight);
                    List<String> sourceShortestPath = pathSourceMap.get(vertex);
                    shortestPath = new ArrayList<>(sourceShortestPath);
                    shortestPath.add(target);
                    pathSourceMap.put(target, shortestPath);
                }
            }
        }

        if (!pathSourceMap.containsKey(end)) {
            // There was no path to end from start
            return new ArrayList<>();
        }
        return pathSourceMap.get(end);
    }
}
