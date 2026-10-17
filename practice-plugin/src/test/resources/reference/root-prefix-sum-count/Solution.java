package com.jinloes.practice;

public class Solution { public static class TreeNode { public int value; public TreeNode left; public TreeNode right; public TreeNode(int value){this.value=value;} public TreeNode(int value, TreeNode left, TreeNode right){this.value=value;this.left=left;this.right=right;} } public static int countRootPrefixes(TreeNode root,int target){return count(root,0L,target);} private static int count(TreeNode n,long s,int t){if(n==null)return 0; long x=s+n.value; return (x==t?1:0)+count(n.left,x,t)+count(n.right,x,t);} }
