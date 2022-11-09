package com.jinloes.data_structures.tree;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * Perform a preorder traversal on a binary tree.
 */
public class PreorderTraversal {
    public static <T> List<T> traverse(BinaryTreeNode<T> root) {
        List<T> values = new ArrayList<>();
        if (root == null) {
            return values;
        }
        traverseHelper(root, values);
        return values;
    }

    public static <T> void traverseHelper(BinaryTreeNode<T> root, List<T> values) {
        if (root == null) {
            return;
        }

        values.add(root.value);
        traverseHelper(root.left, values);
        traverseHelper(root.right, values);
    }

    public static <T> List<T> traverseWithoutRecursion(BinaryTreeNode<T> root) {
        List<T> result = new ArrayList<>();
        if (root == null) {
            return result;
        }
        Stack<BinaryTreeNode<T>> stack = new Stack<>();
        stack.add(root);
        BinaryTreeNode<T> current;

        while (!stack.isEmpty()) {
            current = stack.pop();

            if (current.right != null) {
                stack.add(current.right);
            }
            if (current.left != null) {
                stack.add(current.left);
            }
            result.add(current.value);
        }

        return result;
    }
}
