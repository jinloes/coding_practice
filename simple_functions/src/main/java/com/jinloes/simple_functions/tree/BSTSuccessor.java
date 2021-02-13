package com.jinloes.simple_functions.tree;

import java.util.Objects;

public class BSTSuccessor {

    public BinaryTreeNode<Integer> getSuccessor(BinaryTreeNode<Integer> current) {
        if (current == null) {
            return null;
        }

        if (current.getRight() != null) {
            BinaryTreeNode<Integer> tmp = current.getRight();
            while (tmp.getLeft() != null) {
                tmp = tmp.getLeft();
            }
            return tmp;
        } else {
            BinaryTreeNode<Integer> parent = current.getParent();
            BinaryTreeNode<Integer> child = current;
            while (parent != null && !Objects.equals(parent.getLeft(), child)) {
                child = parent;
                parent = parent.getParent();
            }
            return parent;
        }
    }
}
