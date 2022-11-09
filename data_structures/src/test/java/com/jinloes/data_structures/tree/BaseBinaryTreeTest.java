package com.jinloes.data_structures.tree;

import org.junit.jupiter.api.BeforeEach;

public class BaseBinaryTreeTest {
    protected BinaryTreeNode<Integer> root;
    protected BinaryTreeNode<Integer> child5;
    protected BinaryTreeNode<Integer> child4;
    protected BinaryTreeNode<Integer> child3;
    protected BinaryTreeNode<Integer> child2;

    @BeforeEach
    public void setUp() {
        /*
         * <pre>
         *     1
         *    2 3
         *   4 5
         * </pre>
         */
        child5 = new BinaryTreeNode<>(5);
        child4 = new BinaryTreeNode<>(4);
        child3 = new BinaryTreeNode<>(3);
        child2 = new BinaryTreeNode<>(2, child4, child5);
        root = new BinaryTreeNode<>(1, child2, child3);
    }
}
