package com.jinloes.simple_functions.graph;

import java.util.*;

/**
 * Finds a path between 2 nodes.
 */
public class FindPath {
    public static List<Integer> find(Graph graph, int start, int end) {
        Set<Integer> visited = new HashSet<>();
        Stack<Integer> path = new Stack<>();
        path.push(start);
        if (findInternal(graph, start, end, path, visited)) {
            return path;
        }
        return Collections.emptyList();
    }

    private static boolean findInternal(Graph graph, int current, int end, Stack<Integer> path, Set<Integer> visited) {
        if (current == end) {
            return true;
        }
        for (Integer neighbor : graph.getAdjacencyList(current)) {
            if (visited.contains(neighbor)) {
                continue;
            }
            path.push(neighbor);
            if (findInternal(graph, neighbor, end, path, visited)) {
                return true;
            }
            path.pop();
        }
        return false;
    }
}