package com.jinloes.simple_functions.tree;

import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class CheckBalancedTest {
    private CheckBalanced checkBalanced;

    @Before
    public void setUp() throws Exception {
        checkBalanced = new CheckBalanced();
    }

    @Test
    public void isBalancedFalse() {
        BinaryTreeNode<Integer> root = new BinaryTreeNode<>(1,
                new BinaryTreeNode<>(2, new BinaryTreeNode<>(3), null),
                null
        );

        assertThat(checkBalanced.isBalanced(root)).isFalse();
    }

    @Test
    public void isBalancedTrue() {
        BinaryTreeNode<Integer> root = new BinaryTreeNode<>(1,
                new BinaryTreeNode<>(2, new BinaryTreeNode<>(3), null),
                new BinaryTreeNode<>(4)
        );

        assertThat(checkBalanced.isBalanced(root)).isTrue();
    }
}