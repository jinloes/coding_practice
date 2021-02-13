package com.jinloes.simple_functions.tree;


import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class BSTSuccessorTest {
    private BSTSuccessor bstSuccessor;

    @Before
    public void setUp() throws Exception {
        bstSuccessor = new BSTSuccessor();
    }

    @Test
    public void getSuccessor() {
        BinaryTreeNode<Integer> n1 = new BinaryTreeNode<>(1);
        BinaryTreeNode<Integer> n2 = new BinaryTreeNode<>(2);
        BinaryTreeNode<Integer> n3 = new BinaryTreeNode<>(3);
        BinaryTreeNode<Integer> n4 = new BinaryTreeNode<>(4);
        BinaryTreeNode<Integer> n5 = new BinaryTreeNode<>(5);
        BinaryTreeNode<Integer> n5_2 = new BinaryTreeNode<>(5);
        BinaryTreeNode<Integer> n9 = new BinaryTreeNode<>(9);

        n4.setLeft(n2);
        n4.setRight(n5);

        n2.setParent(n4);
        n2.setLeft(n1);
        n2.setRight(n3);

        n1.setParent(n2);

        n3.setParent(n2);

        n5.setParent(n4);
        n5.setLeft(n5_2);
        n5.setRight(n9);

        n5_2.setParent(n5);

        n9.setParent(n5);


        assertThat(bstSuccessor.getSuccessor(n1)).isEqualTo(n2);
        assertThat(bstSuccessor.getSuccessor(n3)).isEqualTo(n4);
        assertThat(bstSuccessor.getSuccessor(n5)).isEqualTo(n9);
        assertThat(bstSuccessor.getSuccessor(n9)).isNull();
        assertThat(bstSuccessor.getSuccessor(n4)).isEqualTo(n5_2);
    }
}