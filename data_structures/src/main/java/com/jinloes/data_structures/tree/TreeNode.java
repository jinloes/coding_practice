package com.jinloes.data_structures.tree;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TreeNode<V> {
    private final V value;
    private final List<TreeNode<V>> children;

    public TreeNode(V value) {
        this(value, null);
    }

    public TreeNode(V value, List<TreeNode<V>> children) {
        this.value = value;
        this.children = Optional.ofNullable(children)
                .orElseGet(ArrayList::new);
    }

    public void addChild(TreeNode<V> child) {
        children.add(child);
    }

    public List<TreeNode<V>> getChildren() {
        return children;
    }

    public V getValue() {
        return value;
    }
}
