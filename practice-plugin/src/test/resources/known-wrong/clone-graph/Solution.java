package com.jinloes.practice;

import java.util.ArrayList;
import java.util.List;

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
        GraphNode copy = new GraphNode(node.value);
        copy.neighbors.addAll(node.neighbors);
        return copy;
    }
}
