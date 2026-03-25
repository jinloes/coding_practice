package com.jinloes.simple_functions;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
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

    public void addChild(T child) {
        addChild(new TreeNode<>(child));
    }

    public void addChild(TreeNode<T> child) {
        children.add(child);
    }
}