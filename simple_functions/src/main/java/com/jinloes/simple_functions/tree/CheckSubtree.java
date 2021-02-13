package com.jinloes.simple_functions.tree;

import java.util.Objects;

/**
 * Check if one tree is a subtree of another
 */
public class CheckSubtree {

    public boolean isSubtree(BinaryTreeNode<Integer> subtree, BinaryTreeNode<Integer> tree) {
        if (tree == null) {
            return false;
        }

        if (Objects.equals(tree.getValue(), subtree.getValue()) && isEqual(subtree, tree)) {
            return true;
        } else {
            return isSubtree(subtree, tree.getLeft()) || isSubtree(subtree, tree.getRight());
        }
    }

    private boolean isEqual(BinaryTreeNode<Integer> n1, BinaryTreeNode<Integer> n2) {
        if (n1 == null && n2 == null) {
            return true;
        } else if (n1 != null && n2 != null) {
            return Objects.equals(n1.getValue(), n2.getValue())
                    && isEqual(n1.getLeft(), n2.getLeft())
                    && isEqual(n1.getRight(), n2.getRight());
        } else {
            return false;
        }
    }

}
