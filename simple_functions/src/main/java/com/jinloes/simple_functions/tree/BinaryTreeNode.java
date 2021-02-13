package com.jinloes.simple_functions.tree;

import com.google.common.base.MoreObjects;

import java.util.Objects;

public class BinaryTreeNode<T> {
    private final T value;
    private BinaryTreeNode<T> left;
    private BinaryTreeNode<T> right;
    private BinaryTreeNode<T> parent;

    public BinaryTreeNode(T value) {
        this(value, null, null, null);
    }

    public BinaryTreeNode(T value, BinaryTreeNode<T> left, BinaryTreeNode<T> right) {
        this(value, left, right, null);
    }

    public BinaryTreeNode(T value, BinaryTreeNode<T> left, BinaryTreeNode<T> right, BinaryTreeNode<T> parent) {
        this.value = value;
        this.left = left;
        this.right = right;
        this.parent = parent;
    }

    public T getValue() {
        return value;
    }

    public BinaryTreeNode<T> getLeft() {
        return left;
    }


    public BinaryTreeNode<T> getRight() {
        return right;
    }

    public BinaryTreeNode<T> getParent() {
        return parent;
    }

    public void setLeft(BinaryTreeNode<T> left) {
        this.left = left;
    }

    public void setRight(BinaryTreeNode<T> right) {
        this.right = right;
    }

    public void setParent(BinaryTreeNode<T> parent) {
        this.parent = parent;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BinaryTreeNode<?> node = (BinaryTreeNode<?>) o;
        return Objects.equals(value, node.value) &&
                Objects.equals(left, node.left) &&
                Objects.equals(right, node.right);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value, left, right);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("value", value)
                .add("left", left)
                .add("right", right)
                .toString();
    }
}
