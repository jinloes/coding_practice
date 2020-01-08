package com.jinloes.data_structures.tree;

import java.util.ArrayDeque;
import java.util.Objects;
import java.util.Queue;

/**
 * BFS adds each nodes children to a queue to visit in level order.
 */
public class BreadthFirstSearch {
    public static <T> boolean search(TreeNode<T> root, T toSearch) {
        Queue<TreeNode<?>> nodeQueue = new ArrayDeque<>();
        nodeQueue.add(root);

        while (!nodeQueue.isEmpty()) {
            TreeNode<?> current = nodeQueue.poll();
            if (Objects.equals(current.getValue(), toSearch)) {
                return true;
            }
            nodeQueue.addAll(current.getChildren());
        }

        return false;
    }
}
