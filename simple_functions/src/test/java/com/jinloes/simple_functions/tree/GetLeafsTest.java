package com.jinloes.simple_functions.tree;

import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class GetLeafsTest {
    private GetLeafs getLeafs;

    @Before
    public void setUp() throws Exception {
        getLeafs = new GetLeafs();
    }

    @Test
    public void getLeafs() {
        BinaryTreeNode<Integer> root = new BinaryTreeNode<>(1,
                // left tree
                new BinaryTreeNode<>(2, new BinaryTreeNode<>(4), null),
                // right tree
                new BinaryTreeNode<>(3,
                        new BinaryTreeNode<>(5,
                                new BinaryTreeNode<>(6), new BinaryTreeNode<>(7)),
                        new BinaryTreeNode<>(8,
                                new BinaryTreeNode<>(9), new BinaryTreeNode<>(10))));

        assertThat(getLeafs.getLeafs(root)).containsExactly(4, 6, 7, 9, 10);
    }
}