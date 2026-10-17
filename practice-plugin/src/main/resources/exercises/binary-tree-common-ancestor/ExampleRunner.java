package com.jinloes.practice;
import static org.assertj.core.api.Assertions.assertThat;
public final class ExampleRunner { private static final String USAGE="Input: two target values followed by the tree in level order with null for missing children, for example: 5 1 [3, 5, 1, 6, 2, 0, 8]"; private ExampleRunner(){} public static void main(String[] args){ if (args.length > 0) { runInput(String.join(" ",args).trim()); return; } Solution.TreeNode root=tree(3,5,1,6,2,0,8); assertThat(Solution.lowestCommonAncestor(root,root.left,root.right).value).as("Example 1").isEqualTo(3); assertThat(Solution.lowestCommonAncestor(root,root.left,root.left.right).value).as("Example 2").isEqualTo(5); assertThat(Solution.lowestCommonAncestor(null,new Solution.TreeNode(1),new Solution.TreeNode(2))).as("Example 3").isNull(); System.out.println("Examples passed: 3"); } private static void runInput(String input){ try{ String[] p=input.trim().split("\\s+",3); int a=Integer.parseInt(p[0]), b=Integer.parseInt(p[1]); Solution.TreeNode root=parseTree(p[2]), first=find(root,a), second=find(root,b), ans=Solution.lowestCommonAncestor(root,first,second); System.out.println("lowestCommonAncestor = "+(ans==null?"null":ans.value)); }catch(RuntimeException e){ System.out.println("Could not read input. "+USAGE); } }
    private static Solution.TreeNode tree(Integer... levels) {
        if (levels.length == 0 || levels[0] == null) return null;
        Solution.TreeNode root = new Solution.TreeNode(levels[0]); java.util.ArrayDeque<Solution.TreeNode> q = new java.util.ArrayDeque<>(); q.add(root); int i = 1;
        while (!q.isEmpty() && i < levels.length) { Solution.TreeNode n = q.poll(); if (i < levels.length && levels[i] != null) { n.left = new Solution.TreeNode(levels[i]); q.add(n.left); } i++; if (i < levels.length && levels[i] != null) { n.right = new Solution.TreeNode(levels[i]); q.add(n.right); } i++; }
        return root;
    }

    private static Solution.TreeNode parseTree(String text) {
        String cleaned = text.replace('[', ' ').replace(']', ' ').replace(',', ' ').trim(); if (cleaned.isEmpty()) return null;
        String[] tokens = cleaned.split("\\s+"); Integer[] levels = new Integer[tokens.length];
        for (int i = 0; i < tokens.length; i++) levels[i] = tokens[i].equals("null") ? null : Integer.valueOf(tokens[i]);
        return tree(levels);
    }
    private static Solution.TreeNode find(Solution.TreeNode n,int v){ if(n==null)return null; if(n.value==v)return n; Solution.TreeNode l=find(n.left,v); return l!=null?l:find(n.right,v); }
}
