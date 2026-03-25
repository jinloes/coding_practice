package com.jinloes.simple_functions.tree;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TreeTraverser {

    public static <T> List<T> preOrder(TreeNode<T> root) {
        if (root == null) {
            return Collections.emptyList();
        }
        List<T> result = new ArrayList<>();
        preOrder(root, result);
        return result;
    }

    private static <T> void preOrder(TreeNode<T> node, List<T> result) {
        if (node == null) {
            return;
        }
        result.add(node.value);
        preOrder(node.left, result);
        preOrder(node.right, result);
    }

    public static <T> List<T> inOrder(TreeNode<T> root) {
        if (root == null) {
            return Collections.emptyList();
        }
        List<T> result = new ArrayList<>();
        inOrder(root, result);
        return result;
    }

    private static <T> void inOrder(TreeNode<T> node, List<T> result) {
        if (node == null) {
            return;
        }
        inOrder(node.left, result);
        result.add(node.value);
        inOrder(node.right, result);
    }

    public static <T> List<T> postOrder(TreeNode<T> root) {
        if (root == null) {
            return Collections.emptyList();
        }
        List<T> result = new ArrayList<>();
        postOrder(root, result);
        return result;
    }

    private static <T> void postOrder(TreeNode<T> node, List<T> result) {
        if (node == null) {
            return;
        }
        postOrder(node.left, result);
        postOrder(node.right, result);
        result.add(node.value);
    }
}