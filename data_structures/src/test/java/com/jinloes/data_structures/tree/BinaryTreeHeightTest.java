package com.jinloes.data_structures.tree;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BinaryTreeHeightTest {
    private BinaryTreeNode<Integer> root;
    private BinaryTreeNode<Integer> rl;
    private BinaryTreeNode<Integer> rlr;
    private BinaryTreeNode<Integer> rr;

    @BeforeEach
    void setUp() {
        rlr = new BinaryTreeNode<>(4);
        rl = new BinaryTreeNode<>(2, rlr, null);
        rr = new BinaryTreeNode<>(3);
        root = new BinaryTreeNode<>(1, rl, rr);

    }

    @Test
    void height() {
        assertThat(BinaryTreeHeight.height(null)).isEqualTo(0);
        assertThat(BinaryTreeHeight.height(rl)).isEqualTo(2);
        assertThat(BinaryTreeHeight.height(rr)).isEqualTo(1);
        assertThat(BinaryTreeHeight.height(root)).isEqualTo(3);
    }
}