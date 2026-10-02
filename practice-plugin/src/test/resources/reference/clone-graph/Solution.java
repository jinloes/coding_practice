package com.jinloes.practice;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Solution {
    public static class GraphNode {
        public int value;
        public List<GraphNode> neighbors;

        public GraphNode(int value) {
            this.value = value;
            this.neighbors = new ArrayList<>();
        }
    }

    public static GraphNode cloneGraph(GraphNode node) {
        if (node == null) {
            return null;
        }
        Map<GraphNode, GraphNode> copies = new HashMap<>();
        ArrayDeque<GraphNode> pending = new ArrayDeque<>();
        copies.put(node, new GraphNode(node.value));
        pending.add(node);
        while (!pending.isEmpty()) {
            GraphNode original = pending.remove();
            GraphNode copy = copies.get(original);
            for (GraphNode neighbor : original.neighbors) {
                GraphNode neighborCopy = copies.get(neighbor);
                if (neighborCopy == null) {
                    neighborCopy = new GraphNode(neighbor.value);
                    copies.put(neighbor, neighborCopy);
                    pending.add(neighbor);
                }
                copy.neighbors.add(neighborCopy);
            }
        }
        return copies.get(node);
    }
}
