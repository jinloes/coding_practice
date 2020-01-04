package com.jinloes.data_structures.tree;

import com.google.common.collect.Lists;
import org.junit.Before;

public class BaseTraversalTest {
    protected TreeNode<Integer> root;

    @Before
    public void setUp() {

        /*
         * <pre>
         *     1
         *    2 3
         *   4 5
         * </pre>
         */
        TreeNode<Integer> child5 = new TreeNode<>(5);
        TreeNode<Integer> child4 = new TreeNode<>(4);
        TreeNode<Integer> child3 = new TreeNode<>(3);
        TreeNode<Integer> child2 = new TreeNode<>(2, Lists.newArrayList(child4, child5));
        root = new TreeNode<>(1, Lists.newArrayList(child2, child3));
    }
}
