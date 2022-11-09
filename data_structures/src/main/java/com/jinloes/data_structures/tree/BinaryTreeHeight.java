package com.jinloes.data_structures.tree;

/**
 * Return the binary of a binary tree.
 */
public class BinaryTreeHeight {
    public static <T> int height(BinaryTreeNode<T> root) {
        if(root == null) {
            return 0;
        }

        return 1 + Math.max(BinaryTreeHeight.height(root.left), BinaryTreeHeight.height((root.right)));
    }
}
