package com.jinloes.practice;

import java.util.HashMap;
import java.util.Map;

public class Solution {
    public static class TreeNode {
        public int value;
        public TreeNode left;
        public TreeNode right;

        public TreeNode(int value) {
            this.value = value;
        }

        public TreeNode(int value, TreeNode left, TreeNode right) {
            this.value = value;
            this.left = left;
            this.right = right;
        }
    }

    public static TreeNode buildTree(int[] preorder, int[] inorder) {
        Map<Integer, Integer> positions = new HashMap<>();
        for (int i = 0; i < inorder.length; i++) {
            positions.put(inorder[i], i);
        }
        return build(preorder, 0, 0, inorder.length - 1, positions);
    }

    private static TreeNode build(int[] preorder, int preStart, int inStart, int inEnd, Map<Integer, Integer> positions) {
        if (inStart > inEnd) {
            return null;
        }
        TreeNode node = new TreeNode(preorder[preStart]);
        int mid = positions.get(preorder[preStart]);
        int leftSize = mid - inStart;
        node.left = build(preorder, preStart + 1, inStart, mid - 1, positions);
        node.right = build(preorder, preStart + 1 + leftSize, mid + 1, inEnd, positions);
        return node;
    }
}
