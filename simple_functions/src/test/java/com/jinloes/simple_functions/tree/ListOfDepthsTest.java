package com.jinloes.simple_functions.tree;


import com.scalified.tree.TreeNode;
import com.scalified.tree.multinode.ArrayMultiTreeNode;
import org.assertj.core.util.Lists;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;


public class ListOfDepthsTest {
    private ListOfDepths listOfDepths;

    @Before
    public void setUp() throws Exception {
        listOfDepths = new ListOfDepths();
    }

    @Test
    public void nodesAtDepth() {
        TreeNode<Integer> root = new ArrayMultiTreeNode<>(3);
        TreeNode<Integer> n1 = new ArrayMultiTreeNode<>(1);
        TreeNode<Integer> n2 = new ArrayMultiTreeNode<>(2);
        TreeNode<Integer> n3 = new ArrayMultiTreeNode<>(5);
        TreeNode<Integer> n4 = new ArrayMultiTreeNode<>(7);

        root.add(n1);
        root.add(n3);
        n1.add(n2);
        n3.add(n4);

        List<List<Integer>> expected = Lists.newArrayList(
                Lists.newArrayList(3),
                Lists.newArrayList(1, 5),
                Lists.newArrayList(2, 7)
        );

        List<List<Integer>> result = listOfDepths.nodesAtDepth(root)
                .stream()
                .map(treeNodes -> treeNodes.stream()
                        .map(TreeNode::data)
                        .collect(Collectors.toList()))
                .collect(Collectors.toList());

        assertThat(result).isEqualTo(expected);
    }
}