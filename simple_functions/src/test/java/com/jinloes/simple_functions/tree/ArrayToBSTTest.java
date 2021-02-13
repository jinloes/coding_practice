package com.jinloes.simple_functions.tree;


import com.scalified.tree.TreeNode;
import com.scalified.tree.multinode.ArrayMultiTreeNode;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public class ArrayToBSTTest {
    private ArrayToBST arrayToBST;

    @Before
    public void setUp() throws Exception {
        arrayToBST = new ArrayToBST();
    }

    @Test
    public void toBST() {
        TreeNode<Integer> root = new ArrayMultiTreeNode<>(3);
        TreeNode<Integer> n1 = new ArrayMultiTreeNode<>(1);
        TreeNode<Integer> n2 = new ArrayMultiTreeNode<>(2);
        TreeNode<Integer> n3 = new ArrayMultiTreeNode<>(5);
        TreeNode<Integer> n4 = new ArrayMultiTreeNode<>(7);

        root.add(n1);
        root.add(n3);
        n1.add(n2);
        n3.add(n4);


        List<Integer> values = arrayToBST.toBST(new int[]{1, 2, 3, 5, 7})
                .preOrdered()
                .stream()
                .map(TreeNode::data)
                .collect(Collectors.toList());

        List<Integer> expected = root.preOrdered()
                .stream()
                .map(TreeNode::data)
                .collect(Collectors.toList());

        assertThat(values).isEqualTo(expected);
    }
}