package com.jinloes.simple_functions;

import org.junit.Test;

public class TreeFromPreorderAndInorderTest {

    @Test
    public void testBuildTree() {
        int[] inorder = new int[]{1, 2};
        int[] preorder = new int[]{2, 1};

        TreeNode2 root = new TreeFromPreorderAndInorder().buildTree(inorder, preorder);

        System.out.println(root);
    }
}