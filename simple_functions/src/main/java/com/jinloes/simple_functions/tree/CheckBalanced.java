package com.jinloes.simple_functions.tree;

/**
 * Check if a binary tree is balanced, heights of 2 subtrees never differe by more than one.
 */
public class CheckBalanced {

    public boolean isBalanced(BinaryTreeNode<Integer> root) {
        int lHeight = getHeight(root.getLeft());
        int rHeight = getHeight(root.getRight());

        return Math.abs(lHeight - rHeight) <= 1;
    }

    private int getHeight(BinaryTreeNode<Integer> root) {
        if (root == null) {
            return 0;
        }

        int maxChildHeight = Math.max(getHeight(root.getLeft()), getHeight(root.getRight()));

        return 1 + maxChildHeight;
    }


}
