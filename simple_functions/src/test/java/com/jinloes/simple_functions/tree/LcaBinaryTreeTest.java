package com.jinloes.simple_functions.tree;


import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link LcaBinaryTree}.
 */
public class LcaBinaryTreeTest {
    private LcaBinaryTree lcaBinaryTree;

    @Before
    public void setUp() throws Exception {
        lcaBinaryTree = new LcaBinaryTree();
    }

    @Test
    public void testLCA() {
        BinaryTreeNode<Integer> threeRight = new BinaryTreeNode<>(5, null, null);
        BinaryTreeNode<Integer> threeLeft = new BinaryTreeNode<>(4, null, null);
        BinaryTreeNode<Integer> oneLeft = new BinaryTreeNode<>(2, null, null);
        BinaryTreeNode<Integer> oneRight = new BinaryTreeNode<>(3, threeLeft, threeRight);
        BinaryTreeNode<Integer> root = new BinaryTreeNode<>(1, oneLeft, oneRight);

        assertThat(lcaBinaryTree.lca(root, oneLeft, oneRight))
                .isEqualTo(root);
        assertThat(lcaBinaryTree.lca(root, oneLeft, threeLeft))
                .isEqualTo(root);
        assertThat(lcaBinaryTree.lca(root, threeLeft, threeRight))
                .isEqualTo(oneRight);
    }
}
