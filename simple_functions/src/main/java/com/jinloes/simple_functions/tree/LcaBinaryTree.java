package com.jinloes.simple_functions.tree;

import java.util.Objects;

/**
 * Implements the lowest common ancestor algorithm on a binary tree.
 * <p>
 * In graph theory and computer science, the lowest common ancestor (Lca) of two nodes v and w in a tree or
 * directed acyclic graph (DAG) T is the lowest (i.e. deepest) node that has both v and w as descendants,
 * where we define each node to be a descendant of itself (so if v has a direct connection from w,
 * w is the lowest common ancestor).
 * <p>
 * Assumes both nodes are present in the tree.
 */
public class LcaBinaryTree {

    public BinaryTreeNode<Integer> lca(BinaryTreeNode<Integer> root, BinaryTreeNode<Integer> first,
                                       BinaryTreeNode<Integer> second) {
        if (root == null) {
            return null;
        }

        if (Objects.equals(root.getValue(), first.getValue()) || Objects.equals(root.getValue(), second.getValue())) {
            return root;
        }

        BinaryTreeNode<Integer> left = lca(root.getLeft(), first, second);
        BinaryTreeNode<Integer> right = lca(root.getRight(), first, second);

        if (left != null && right != null) {
            return root;
        }
        if (left != null) {
            return left;
        } else {
            return right;
        }
    }

}
