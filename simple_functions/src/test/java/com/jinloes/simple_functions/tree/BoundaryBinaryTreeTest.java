package com.jinloes.simple_functions.tree;

import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class BoundaryBinaryTreeTest {
    private BoundaryBinaryTree boundaryBinaryTree;

    @Before
    public void setUp() throws Exception {
        this.boundaryBinaryTree = new BoundaryBinaryTree();
    }

    @Test
    public void testBoundary() {
        BinaryTreeNode<Integer> root = new BinaryTreeNode<>(2,
                // left tree
                new BinaryTreeNode<>(8, new BinaryTreeNode<>(4), new BinaryTreeNode<>(12, new BinaryTreeNode<>(10), new BinaryTreeNode<>(14))),
                // right tree
                new BinaryTreeNode<>(22, null, new BinaryTreeNode<>(25)));

        assertThat(boundaryBinaryTree.getBoundary(root))
                .containsExactly(2, 8, 4, 10, 14, 22, 25);
    }
}