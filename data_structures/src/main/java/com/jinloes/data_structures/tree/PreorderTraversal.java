package com.jinloes.data_structures.tree;

import com.google.common.collect.Lists;

import java.util.List;

/**
 * Preorder visits tree nodes in root, left, right order.
 */
public class PreorderTraversal {
    public static <T> List<T> traverse(TreeNode<T> root) {
        List<T> traversal = Lists.newArrayList(root.getValue());
        List<TreeNode<T>> children = root.getChildren();
        if (children.size() > 0) {
            List<T> childTraversal = traverse(root.getChildren().get(0));
            traversal.addAll(childTraversal);
        }
        if (children.size() > 1) {
            List<T> childTraversal = traverse(root.getChildren().get(1));
            traversal.addAll(childTraversal);
        }
        return traversal;
    }
}
