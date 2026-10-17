package com.jinloes.practice;
public class Solution {
    public static class TreeNode {
        public int value; public TreeNode left; public TreeNode right;
        public TreeNode(int value) { this.value = value; }
        public TreeNode(int value, TreeNode left, TreeNode right) { this.value = value; this.left = left; this.right = right; }
    }
    public static boolean hasPathSum(TreeNode root,int target){ return pref(root,target); } private static boolean pref(TreeNode n,int r){ if(n==null)return r==0; return r==n.value||pref(n.left,r-n.value)||pref(n.right,r-n.value); }
}
