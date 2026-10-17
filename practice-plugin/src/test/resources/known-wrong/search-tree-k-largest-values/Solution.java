package com.jinloes.practice;
import java.util.*;
public class Solution {
    public static class TreeNode {
        public int value; public TreeNode left; public TreeNode right;
        public TreeNode(int value) { this.value = value; }
        public TreeNode(int value, TreeNode left, TreeNode right) { this.value = value; this.left = left; this.right = right; }
    }
    public static List<Integer> kLargest(TreeNode root,int k){ List<Integer> a=new ArrayList<>(); inorder(root,a,k); return a; } private static void inorder(TreeNode n,List<Integer>a,int k){ if(n==null||a.size()==k)return; inorder(n.left,a,k); if(a.size()<k)a.add(n.value); inorder(n.right,a,k); }
}
