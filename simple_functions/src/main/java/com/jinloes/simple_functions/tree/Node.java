package com.jinloes.simple_functions.tree;

import com.google.common.base.MoreObjects;

import java.util.Objects;

public class Node<T> {
    private final T value;
    private final Node<T> left;
    private final Node<T> right;

    public Node(T value, Node<T> left, Node<T> right) {
        this.value = value;
        this.left = left;
        this.right = right;
    }

    public T getValue() {
        return value;
    }

    public Node<T> getLeft() {
        return left;
    }


    public Node<T> getRight() {
        return right;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Node<?> node = (Node<?>) o;
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
