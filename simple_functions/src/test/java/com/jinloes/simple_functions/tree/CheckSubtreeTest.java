package com.jinloes.simple_functions.tree;

import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class CheckSubtreeTest {
    private CheckSubtree checkSubtree;

    @Before
    public void setUp() throws Exception {
        checkSubtree = new CheckSubtree();
    }

    @Test
    public void isSubtree() {
        BinaryTreeNode<Integer> root1 = new BinaryTreeNode<>(3);
        BinaryTreeNode<Integer> root2 = new BinaryTreeNode<>(1, new BinaryTreeNode<>(2),
                new BinaryTreeNode<>(3));

        assertThat(checkSubtree.isSubtree(root1, root2));
    }

    @Test
    public void isSubtreeLonger() {
        BinaryTreeNode<Integer> root1 = new BinaryTreeNode<>(3, new BinaryTreeNode<>(4), new BinaryTreeNode<>(5));
        BinaryTreeNode<Integer> root2 = new BinaryTreeNode<>(1, new BinaryTreeNode<>(2),
                new BinaryTreeNode<>(3, new BinaryTreeNode<>(4), new BinaryTreeNode<>(5)));

        assertThat(checkSubtree.isSubtree(root1, root2));
    }
}