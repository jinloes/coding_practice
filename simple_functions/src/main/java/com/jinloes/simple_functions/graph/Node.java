package com.jinloes.simple_functions.graph;

import java.util.Objects;

/**
 * Models a tree node.
 */
public class Node {
    private Node left;
    private Node right;
    private Node next;
    private Node previous;
    private String value;

    public Node(Node left, Node right, String value) {
        this.left = left;
        this.right = right;
        this.value = value;
    }

    public Node getLeft() {
        return left;
    }

    public void setLeft(Node left) {
        this.left = left;
    }

    public Node getRight() {
        return right;
    }

    public void setRight(Node right) {
        this.right = right;
    }

    public Node getNext() {
        return next;
    }

    public void setNext(Node next) {
        this.next = next;
    }

    public Node getPrevious() {
        return previous;
    }

    public void setPrevious(Node previous) {
        this.previous = previous;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Node node = (Node) o;
        return Objects.equals(left, node.left) &&
                Objects.equals(right, node.right) &&
                Objects.equals(next, node.next) &&
                Objects.equals(previous, node.previous) &&
                Objects.equals(value, node.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(left, right, next, previous, value);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Node{");
        sb.append("left=").append(left != null ? left.getValue() : null);
        sb.append(", right=").append(right != null ? right.getValue() : null);
        sb.append(", next=").append(next != null ? next.getValue() : null);
        sb.append(", previous=").append(previous != null ? previous.getValue() : null);
        sb.append(", value='").append(value).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
