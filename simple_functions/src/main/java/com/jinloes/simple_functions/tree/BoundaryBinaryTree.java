package com.jinloes.simple_functions.tree;

import java.util.ArrayList;
import java.util.List;

/**
 * Given a binary tree, print boundary nodes of the binary tree Anti-Clockwise starting from the root.
 * The boundary includes left boundary, leaves, and right boundary in order without duplicate nodes.
 * (The values of the nodes may still be duplicates.)
 * <p>
 * The left boundary is defined as the path from the root to the left-most node.
 * The right boundary is defined as the path from the root to the right-most node.
 * If the root doesn’t have left subtree or right subtree, then the root itself is left boundary or right boundary.
 * Note this definition only applies to the input binary tree, and not apply to any subtrees.
 * <p>
 * The left-most node is defined as a leaf node you could reach when you always firstly travel to the left subtree
 * if it exists. If not, travel to the right subtree. Repeat until you reach a leaf node.
 * <p>
 * The right-most node is also defined in the same way with left and right exchanged.
 * For example, boundary traversal of the following tree is “20 8 4 10 14 25 22”
 */
public class BoundaryBinaryTree {

    public List<Integer> getBoundary(BinaryTreeNode<Integer> root) {
        List<Integer> boundary = new ArrayList<>();

        if (root == null) {
            return boundary;
        }

        boundary.add(root.getValue());

        printLeftSubtree(root.getLeft(), boundary);
        printLeftSubtreeLeafs(root.getLeft(), boundary);

        printRightSubtree(root.getRight(), boundary);
        printRightSubtreeLeafs(root.getRight(), boundary);

        return boundary;
    }

    private void printLeftSubtree(BinaryTreeNode<Integer> root, List<Integer> boundary) {
        if (root == null || (root.getLeft() == null && root.getRight() == null)) {
            return;
        }

        boundary.add(root.getValue());

        printLeftSubtree(root.getLeft(), boundary);
    }

    private void printLeftSubtreeLeafs(BinaryTreeNode<Integer> root, List<Integer> boundary) {
        if (root == null) {
            return;
        }

        if (root.getLeft() == null && root.getRight() == null) {
            boundary.add(root.getValue());
        }

        printLeftSubtreeLeafs(root.getLeft(), boundary);
        printLeftSubtreeLeafs(root.getRight(), boundary);
    }

    private void printRightSubtree(BinaryTreeNode<Integer> root, List<Integer> boundary) {
        if (root == null || (root.getLeft() == null && root.getRight() == null)) {
            return;
        }

        printRightSubtree(root.getRight(), boundary);

        boundary.add(root.getValue());
    }

    private void printRightSubtreeLeafs(BinaryTreeNode<Integer> root, List<Integer> boundary) {
        if (root == null) {
            return;
        }

        if (root.getLeft() == null && root.getRight() == null) {
            boundary.add(root.getValue());
        }

        printRightSubtreeLeafs(root.getRight(), boundary);
        printRightSubtreeLeafs(root.getLeft(), boundary);
    }
}

