package com.jinloes.practice;

import java.util.LinkedList;
import java.util.Queue;

public class Solution {
    public static class Node {
        public int value;
        public Node left;
        public Node right;
        public Node next;
        public Node(int value) { this.value = value; }
        public Node(int value, Node left, Node right) { this.value = value; this.left = left; this.right = right; }
    }

    public static Node connect(Node root) {
        if (root == null) return null;
        Queue<Node> queue = new LinkedList<>();
        queue.add(root);
        while (!queue.isEmpty()) {
            int count = queue.size();
            Node previous = null;
            for (int i = 0; i < count; i++) {
                Node node = queue.remove();
                if (previous != null) previous.next = node;
                previous = node;
                if (node.left != null) queue.add(node.left);
                if (node.right != null) queue.add(node.right);
            }
            previous.next = null;
        }
        return root;
    }
}

