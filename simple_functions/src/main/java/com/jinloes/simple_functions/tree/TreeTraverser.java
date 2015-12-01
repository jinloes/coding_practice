package com.jinloes.simple_functions.tree;

import javax.swing.tree.DefaultMutableTreeNode;
import java.util.ArrayList;
import java.util.List;

/**
 * Implements tree traversals.
 */
public class TreeTraverser {
    /**
     * Performs a preorder traversal.
     * The algorithm is as follows.
     * 1. display node
     * 2. display left subtree
     * 3. display right subtree
     *
     * @param root tree root
     * @return list of the node values in preorder
     */
    public static List<String> preOrder(DefaultMutableTreeNode root) {
        List<String> path = new ArrayList<>();
        if (root == null) {
            return path;
        }
        path.add(root.getUserObject().toString());
        if (!root.isLeaf()) {
            preOrderInternal(getLeft(root), path);
            preOrderInternal(getRight(root), path);
        }
        return path;
    }

    private static void preOrderInternal(DefaultMutableTreeNode node, List<String> path) {
        if (node == null) {
            return;
        }
        path.add(node.getUserObject().toString());
        preOrderInternal(getLeft(node), path);
        preOrderInternal(getRight(node), path);
    }

    private static DefaultMutableTreeNode getLeft(DefaultMutableTreeNode node) {
        try {
            return (DefaultMutableTreeNode) node.getChildAt(0);
        } catch (ArrayIndexOutOfBoundsException e) {
            return null;
        }
    }

    private static DefaultMutableTreeNode getRight(DefaultMutableTreeNode node) {
        try {
            return (DefaultMutableTreeNode) node.getChildAt(1);
        } catch (ArrayIndexOutOfBoundsException e) {
            return null;
        }
    }

    public static List<String> inOrder(DefaultMutableTreeNode root) {
        List<String> path = new ArrayList<>();
        if (root == null) {
            return path;
        }
        inOrderInternal(getLeft(root), path);
        path.add(root.getUserObject().toString());
        inOrderInternal(getRight(root), path);
        return path;
    }

    private static void inOrderInternal(DefaultMutableTreeNode node, List<String> path) {
        if (node == null) {
            return;
        }
        inOrderInternal(getLeft(node), path);
        path.add(node.getUserObject().toString());
        inOrderInternal(getRight(node), path);
    }

    public static List<String> postOrder(DefaultMutableTreeNode root) {
        List<String> path = new ArrayList<>();
        if (root == null) {
            return path;
        }
        postOrderInternal(getLeft(root), path);
        postOrderInternal(getRight(root), path);
        path.add(root.getUserObject().toString());
        return path;
    }

    private static void postOrderInternal(DefaultMutableTreeNode node, List<String> path) {
        if (node == null) {
            return;
        }
        postOrderInternal(getLeft(node), path);
        postOrderInternal(getRight(node), path);
        path.add(node.getUserObject().toString());
    }
}
