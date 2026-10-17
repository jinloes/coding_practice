package com.jinloes.practice;
import java.util.*;
public class Solution {
    public static class TreeNode {
        public int value; public TreeNode left; public TreeNode right;
        public TreeNode(int value) { this.value = value; }
        public TreeNode(int value, TreeNode left, TreeNode right) { this.value = value; this.left = left; this.right = right; }
    }
    public static List<Integer> boundaryValues(TreeNode root){ List<Integer>a=new ArrayList<>(); pre(root,a); return a; } private static void pre(TreeNode n,List<Integer>a){ if(n==null)return; a.add(n.value); pre(n.left,a); pre(n.right,a); }
}
