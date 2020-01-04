package com.jinloes.data_structures.tree;

import com.google.common.collect.Lists;

import java.util.List;

/**
 * Postorder visits tree nodes in Left, Right, Root order
 */
public class PostorderTraversal {
    public static <T> List<T> traverse(TreeNode<T> root) {
        List<T> traversal = Lists.newArrayList();
        List<TreeNode<T>> children = root.getChildren();
        if (children.size() > 0) {
            List<T> childTraversal = traverse(root.getChildren().get(0));
            traversal.addAll(childTraversal);
        }
        if (children.size() > 1) {
            List<T> childTraversal = traverse(root.getChildren().get(1));
            traversal.addAll(childTraversal);
        }
        traversal.add(root.getValue());
        return traversal;
    }
}
