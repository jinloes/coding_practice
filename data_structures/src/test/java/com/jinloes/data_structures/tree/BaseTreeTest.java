package com.jinloes.data_structures.tree;

import com.google.common.collect.Lists;
import org.junit.jupiter.api.BeforeAll;

public class BaseTreeTest {
    protected TreeNode<Integer> root;
    protected TreeNode<String> nAryRoot;

    @BeforeAll
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

        /*
         * <pre>
         *      A
         *    B   C
         *  D E F
         * </pre>
         */
        TreeNode<String> childF = new TreeNode<>("F");
        TreeNode<String> childE = new TreeNode<>("E");
        TreeNode<String> childD = new TreeNode<>("D");
        TreeNode<String> childC = new TreeNode<>("C");
        TreeNode<String> childB = new TreeNode<>("B", Lists.newArrayList(childD, childE, childF));
        nAryRoot = new TreeNode<>("A", Lists.newArrayList(childB, childC));
    }
}
