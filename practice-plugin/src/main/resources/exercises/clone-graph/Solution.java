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

        @Override
        public String toString() {
            return "GraphNode(value=" + value + ")";
        }
    }

    public static GraphNode cloneGraph(GraphNode node) {
        throw new UnsupportedOperationException("Implement cloneGraph");
    }
}
