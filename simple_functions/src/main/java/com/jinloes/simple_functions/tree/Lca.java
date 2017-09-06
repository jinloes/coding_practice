package com.jinloes.simple_functions.tree;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implements the lowest common ancestor algorithm on a binary search tree.
 * <p>
 * In graph theory and computer science, the lowest common ancestor (Lca) of two nodes v and w in a tree or
 * directed acyclic graph (DAG) T is the lowest (i.e. deepest) node that has both v and w as descendants,
 * where we define each node to be a descendant of itself (so if v has a direct connection from w,
 * w is the lowest common ancestor).
 */
public class Lca {

    /**
     * Creates paths to nodes then iterates over the paths until a different node is found.
     *
     * @param root   root
     * @param first  to find Lca of
     * @param second node to find Lca of
     * @return {@link Optional<Node<Integer>>}
     */
    public static Optional<Node<Integer>> find(Node<Integer> root, Node<Integer> first, Node<Integer> second) {
        if (root == null) {
            return Optional.empty();
        }
        List<Node<Integer>> firstPath = new ArrayList<>();
        List<Node<Integer>> secondPath = new ArrayList<>();

        if (!createPath(root, first.getValue(), firstPath) || !createPath(root, second.getValue(), secondPath)) {
            return Optional.empty();
        }

        int i = 0;
        for (; i < firstPath.size(); i++) {
            if (!firstPath.get(i).equals(secondPath.get(i))) {
                break;
            }
        }
        return Optional.of(firstPath.get(i - 1));
    }

    private static boolean createPath(Node<Integer> root, Integer value, List<Node<Integer>> path) {
        if (root == null) {
            return false;
        }
        path.add(root);
        if (root.getValue().equals(value)) {
            return true;
        }
        if (root.getLeft() != null && createPath(root.getLeft(), value, path)) {
            return true;
        }
        if (root.getRight() != null && createPath(root.getRight(), value, path)) {
            return true;
        }
        path.remove(path.size() - 1);
        return false;
    }

    /**
     * Finds the Lca using a traversal. Assumes that both nodes exist in the tree.
     *
     * @param root   root
     * @param first  node to find Lca of
     * @param second node to find Lca of
     * @return {@link Optional<Node<Integer>>}
     */
    public static Optional<Node<Integer>> findUsingTraversal(Node<Integer> root, Node<Integer> first,
                                                             Node<Integer> second) {
        if (root == null) {
            return Optional.empty();
        }
        if (root.getValue().equals(first.getValue()) || root.getValue().equals(second.getValue())) {
            return Optional.of(root);
        }
        Optional<Node<Integer>> leftLca = findUsingTraversal(root.getLeft(), first, second);
        Optional<Node<Integer>> rightLca = findUsingTraversal(root.getRight(), first, second);
        if (leftLca.isPresent() && rightLca.isPresent()) {
            return Optional.of(root);
        }
        return leftLca.isPresent() ? leftLca : rightLca;
    }
}
