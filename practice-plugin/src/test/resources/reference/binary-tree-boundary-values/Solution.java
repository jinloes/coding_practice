package com.jinloes.practice;
import java.util.*;
public class Solution {
    public static class TreeNode {
        public int value; public TreeNode left; public TreeNode right;
        public TreeNode(int value) { this.value = value; }
        public TreeNode(int value, TreeNode left, TreeNode right) { this.value = value; this.left = left; this.right = right; }
    }
    public static List<Integer> boundaryValues(TreeNode root){ List<Integer> v=new ArrayList<>(); if(root==null)return v; v.add(root.value); if(leaf(root))return v; TreeNode c=root.left; while(c!=null){ if(!leaf(c))v.add(c.value); c=c.left!=null?c.left:c.right; } leaves(root,v); List<Integer> r=new ArrayList<>(); c=root.right; while(c!=null){ if(!leaf(c))r.add(c.value); c=c.right!=null?c.right:c.left; } Collections.reverse(r); v.addAll(r); return v; } private static void leaves(TreeNode n,List<Integer>v){ if(n==null)return; if(leaf(n)){v.add(n.value);return;} leaves(n.left,v); leaves(n.right,v); } private static boolean leaf(TreeNode n){ return n.left==null&&n.right==null; }
}
