package com.jinloes.simple_functions;

import com.google.common.base.MoreObjects;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TreeNode<T> {
  private final T value;
  private final List<TreeNode<T>> children;

  public TreeNode(T value) {
    this(value, new ArrayList<>());
  }

  public TreeNode(T value, List<TreeNode<T>> children) {
    this.value = value;
    this.children = children;
  }

  public T getValue() {
    return value;
  }

  public List<TreeNode<T>> getChildren() {
    return children;
  }

  public void addChild(T child) {
    addChild(new TreeNode<>(child));
  }

  @Override public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    TreeNode<?> treeNode = (TreeNode<?>) o;
    return Objects.equals(value, treeNode.value) && Objects.equals(children, treeNode.children);
  }

  @Override public int hashCode() {
    return Objects.hash(value, children);
  }

  public void addChild(TreeNode<T> child) {
    children.add(child);
  }

  @Override public String toString() {
    return MoreObjects.toStringHelper(this).add("value", value).add("children", children).toString();
  }
}
