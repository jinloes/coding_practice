package com.jinloes.practice;
public class Solution {
    public static class TreeNode {
        public int value; public TreeNode left; public TreeNode right;
        public TreeNode(int value) { this.value = value; }
        public TreeNode(int value, TreeNode left, TreeNode right) { this.value = value; this.left = left; this.right = right; }
    }
    public static boolean hasPathSum(TreeNode root,int target){ if(root==null)return false; int r=target-root.value; if(root.left==null&&root.right==null)return r==0; return hasPathSum(root.left,r)||hasPathSum(root.right,r); }
}
