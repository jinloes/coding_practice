package com.jinloes.simple_functions.tree;

/**
 * Write a function to check if a binary tree is a BST.
 */
public class BstVerifier {

    public boolean isBST(BinaryTreeNode<Integer> root) {
        if (root == null) {
            return true;
        }

        if ((root.getLeft() != null && root.getLeft().getValue() > root.getValue())
                || (root.getRight() != null && root.getRight().getValue() < root.getValue())) {
            return false;
        }

        return isBST(root.getLeft()) && isBST(root.getRight());
    }


}
