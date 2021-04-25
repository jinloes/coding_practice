package com.jinloes.simple_functions.graph;

import com.google.common.base.MoreObjects;

import java.util.*;

/**
 * Deep clone a graph.
 */
public class CloneGraph {
    public Node cloneGraph(Node node) {
        if (node == null) {
            return null;
        }

        Queue<Node> queue = new ArrayDeque<>();
        queue.add(node);
        Set<Node> visited = new HashSet<>();
        Node head = null;
        Map<Node, Node> clones = new HashMap<>();

        while (!queue.isEmpty()) {
            Node current = queue.poll();
            if (visited.contains(current)) {
                continue;
            }
            Node clone = clones.compute(current, (n, nClone) -> {
                if (nClone == null) {
                    return new Node(n.val);
                }
                return nClone;
            });

            if (head == null) {
                head = clone;
            }

            for (Node neighbor : current.neighbors) {
                Node neighborClone = clones.compute(neighbor, (n, nClone) -> {
                    if (nClone == null) {
                        return new Node(n.val);
                    }
                    return nClone;
                });

                clone.neighbors.add(neighborClone);
                if (!visited.contains(neighbor)) {
                    queue.add(neighbor);
                }
            }

            visited.add(current);
        }
        return head;
    }

    static class Node {
        public int val;
        public List<Node> neighbors;

        public Node() {
            val = 0;
            neighbors = new ArrayList<>();
        }

        public Node(int _val) {
            val = _val;
            neighbors = new ArrayList<>();
        }

        public Node(int _val, List<Node> _neighbors) {
            val = _val;
            neighbors = _neighbors;
        }

        @Override
        public String toString() {
            return MoreObjects.toStringHelper(this)
                    .add("val", val)
                    .toString();
        }
    }
}
