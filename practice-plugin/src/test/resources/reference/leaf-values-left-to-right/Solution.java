package com.jinloes.practice;
import java.util.*;
public class Solution {
    public static class TreeNode {
        public int value; public TreeNode left; public TreeNode right;
        public TreeNode(int value) { this.value = value; }
        public TreeNode(int value, TreeNode left, TreeNode right) { this.value = value; this.left = left; this.right = right; }
    }
    public static List<Integer> leafValues(TreeNode root){ List<Integer>a=new ArrayList<>(); collect(root,a); return a; } private static void collect(TreeNode n,List<Integer>a){ if(n==null)return; if(n.left==null&&n.right==null){a.add(n.value);return;} collect(n.left,a); collect(n.right,a); }
}
