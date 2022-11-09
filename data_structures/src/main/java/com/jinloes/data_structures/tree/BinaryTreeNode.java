package com.jinloes.data_structures.tree;

/**
 * Binary tree node.
 * <p>
 * Getters and setters omitted for simplicity
 *
 * @param <T>
 */
public class BinaryTreeNode<T> {
    T value;
    BinaryTreeNode<T> left;
    BinaryTreeNode<T> right;

    public BinaryTreeNode(T value, BinaryTreeNode<T> left, BinaryTreeNode<T> right) {
        this.value = value;
        this.left = left;
        this.right = right;
    }

    public BinaryTreeNode(T val) {
        this(val, null, null);
    }
}
