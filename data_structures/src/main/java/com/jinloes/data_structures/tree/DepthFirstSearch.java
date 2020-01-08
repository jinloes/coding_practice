package com.jinloes.data_structures.tree;

import java.util.List;
import java.util.Objects;

/**
 * DFS can be done with a post order traversal.
 */
public class DepthFirstSearch {

    public static <T> boolean search(TreeNode<T> root, T value) {
        List<TreeNode<T>> children = root.getChildren();
        if (children.size() > 0) {
            boolean found = search(children.get(0), value);
            if (found) {
                return true;
            }
        }
        if (children.size() > 1) {
            boolean found = search(children.get(1), value);
            if (found) {
                return true;
            }
        }
        return Objects.equals(root.getValue(), value);
    }
}
