package com.jinloes.simple_functions.tree;

import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class BstVerifierTest {
    private BstVerifier bstVerifier;

    @Before
    public void setUp() throws Exception {
        bstVerifier = new BstVerifier();
    }

    @Test
    public void testVerify() {
        BinaryTreeNode<Integer> root = new BinaryTreeNode<>(4, new BinaryTreeNode<>(2, new BinaryTreeNode<>(1), new BinaryTreeNode<>(3)),
                new BinaryTreeNode<>(5));


        assertThat(bstVerifier.isBST(root)).isTrue();
    }

    @Test
    public void testVerifyFalse() {
        BinaryTreeNode<Integer> threeLeft = new BinaryTreeNode<>(4, null, null);
        BinaryTreeNode<Integer> oneLeft = new BinaryTreeNode<>(2, null, null);
        BinaryTreeNode<Integer> oneRight = new BinaryTreeNode<>(6, threeLeft, null);
        BinaryTreeNode<Integer> root = new BinaryTreeNode<>(1, oneLeft, oneRight);

        assertThat(bstVerifier.isBST(root)).isFalse();
    }
}
