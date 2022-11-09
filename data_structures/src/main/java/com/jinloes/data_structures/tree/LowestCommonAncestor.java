package com.jinloes.data_structures.tree;

import java.util.Optional;

/**
 * Given the values of two nodes in a binary search tree, find the lowest
 * (nearest) common ancestor. You may assume that both values exist in the
 * tree.
 */
public class LowestCommonAncestor {
    public static <T> BinaryTreeNode<T> find(BinaryTreeNode<T> root, BinaryTreeNode<T> node1, BinaryTreeNode<T> node2) {
        if (root == null) {
            return null;
        }

        if (root.value == node1.value) {
            return root;
        }

        if (root.value == node2.value) {
            return node2;
        }

        BinaryTreeNode<T> leftExists = find(root.left, node1, node2);
        BinaryTreeNode<T> rightExists = find(root.right, node1, node2);

        if (leftExists != null && rightExists != null) {
            return root;
        } else if (leftExists != null || rightExists != null) {
            return Optional.ofNullable(leftExists).orElse(rightExists);
        }

        return null;
    }
}
