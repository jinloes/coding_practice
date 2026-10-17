package com.jinloes.practice;
public class Solution {
    public static class TreeNode {
        public int value; public TreeNode left; public TreeNode right;
        public TreeNode(int value) { this.value = value; }
        public TreeNode(int value, TreeNode left, TreeNode right) { this.value = value; this.left = left; this.right = right; }
    }
    public static TreeNode lowestCommonAncestor(TreeNode root,TreeNode first,TreeNode second){ if(root==null||root==first||root==second)return root; TreeNode l=lowestCommonAncestor(root.left,first,second), r=lowestCommonAncestor(root.right,first,second); if(l!=null&&r!=null)return root; return l!=null?l:r; }
}
