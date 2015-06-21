package com.jinloes.simple_functions.graph;

import java.util.ArrayDeque;
import java.util.Queue;

/**
 * Connects nodes at each level of a graph
 */
public class ConnectNodes {
    public static void connectNodes(Node root) {
        Queue<Node> queue1 = new ArrayDeque<>();
        Queue<Node> queue2 = new ArrayDeque<>();
        queue1.add(root);
        boolean queue1Parent = true;
        while (!queue1.isEmpty() || !queue2.isEmpty()) {
            if (queue1Parent) {
                processQueue(queue1, queue2);
                queue1Parent = false;
            } else {
                processQueue(queue2, queue1);
                queue1Parent = true;
            }
        }
    }

    private static void processQueue(Queue<Node> parentQueue, Queue<Node> childQueue) {
        while (!parentQueue.isEmpty()) {
            Node node = parentQueue.poll();
            Node next = parentQueue.peek();
            if (next != null) {
                setPointers(node, next);
            }
            addChildren(childQueue, node);
        }
    }

    private static void setPointers(Node node, Node next) {
        next.setPrevious(node);
        node.setNext(next);
    }

    private static void addChildren(Queue<Node> queue, Node node) {
        Node left = node.getLeft();
        Node right = node.getRight();
        if (left != null) {
            queue.add(left);
        }
        if (right != null) {
            queue.add(right);
        }
    }
}
