package com.jinloes.practice;
public class Solution {
    public static class TreeNode {
        public int value; public TreeNode left; public TreeNode right;
        public TreeNode(int value) { this.value = value; }
        public TreeNode(int value, TreeNode left, TreeNode right) { this.value = value; this.left = left; this.right = right; }
    }
    public static TreeNode lowestCommonAncestor(TreeNode root,TreeNode first,TreeNode second){ Search s=find(root,first,second); return s.a&&s.b?s.node:null; } private static Search find(TreeNode n,TreeNode f,TreeNode s){ if(n==null)return new Search(null,false,false); Search l=find(n.left,f,s); if(l.node!=null&&l.a&&l.b)return l; Search r=find(n.right,f,s); if(r.node!=null&&r.a&&r.b)return r; boolean a=l.a||r.a||n==f, b=l.b||r.b||n==s; return new Search(a&&b?n:null,a,b); } private static final class Search{ final TreeNode node; final boolean a,b; Search(TreeNode node,boolean a,boolean b){this.node=node;this.a=a;this.b=b;} }
}
