package com.jinloes.practice;

import java.util.List;

public class Solution {
    public static class TreeNode { public int value; public TreeNode left; public TreeNode right; public TreeNode(int value){this.value=value;} public TreeNode(int value, TreeNode left, TreeNode right){this.value=value;this.left=left;this.right=right;} }
    public record TraversalOrders(List<Integer> preorder, List<Integer> inorder, List<Integer> postorder) {}
    public static TraversalOrders traversalOrders(TreeNode root) { throw new UnsupportedOperationException("Implement traversalOrders"); }
}
