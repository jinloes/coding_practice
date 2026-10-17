package com.jinloes.practice;
import java.util.*;
public class Solution {
    public static class TreeNode {
        public int value; public TreeNode left; public TreeNode right;
        public TreeNode(int value) { this.value = value; }
        public TreeNode(int value, TreeNode left, TreeNode right) { this.value = value; this.left = left; this.right = right; }
    }
    public static List<Integer> kLargest(TreeNode root,int k){ List<Integer> ans=new ArrayList<>(); Deque<TreeNode> st=new ArrayDeque<>(); TreeNode c=root; while((c!=null||!st.isEmpty())&&ans.size()<k){ while(c!=null){ st.push(c); c=c.right; } c=st.pop(); ans.add(c.value); c=c.left; } return ans; }
}
