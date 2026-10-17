package com.jinloes.practice;
import static org.assertj.core.api.Assertions.assertThat;
public final class ExampleRunner { private static final String USAGE="Input: a level-order tree, then targetValue=<value>, for example: [4, 2, 6, 1, 3, 5, 7] targetValue=3"; private ExampleRunner(){} public static void main(String[] args){ if (args.length > 0) {runInput(String.join(" ",args).trim());return;} Solution.TreeNode root=tree("[4, 2, 6, 1, 3, 5, 7]"); assertThat(Solution.inorderSuccessor(root,find(root,3)).value).as("Example 1").isEqualTo(4); assertThat(Solution.inorderSuccessor(root,find(root,7))).as("Example 2").isNull(); System.out.println("Examples passed: 2"); } private static void runInput(String input) { try{int m=input.lastIndexOf("targetValue="); if(m<0)throw new IllegalArgumentException(); Solution.TreeNode root=tree(input.substring(0,m).trim()); Solution.TreeNode ans=Solution.inorderSuccessor(root,find(root,Integer.parseInt(input.substring(m+12).trim()))); System.out.println(ans==null?"null":ans.value);}catch(RuntimeException e){System.out.println("Could not read \""+input+"\". "+USAGE);} } private static Solution.TreeNode find(Solution.TreeNode n,int v){if(n==null)return null; if(n.value==v)return n; Solution.TreeNode l=find(n.left,v); return l!=null?l:find(n.right,v);} 
    private static Solution.TreeNode tree(String text) {
        String trimmed = text.trim();
        if (trimmed.equals("null")) return null;
        if (!trimmed.startsWith("[") || !trimmed.endsWith("]")) throw new IllegalArgumentException("tree");
        String body = trimmed.substring(1, trimmed.length() - 1).trim();
        if (body.isEmpty()) return null;
        String[] parts = body.split(",");
        Solution.TreeNode[] nodes = new Solution.TreeNode[parts.length];
        for (int i = 0; i < parts.length; i++) { String p = parts[i].trim(); if (!p.equals("null")) nodes[i] = new Solution.TreeNode(Integer.parseInt(p)); }
        for (int i = 0, child = 1; i < nodes.length && child < nodes.length; i++) { if (nodes[i] == null) continue; nodes[i].left = nodes[child++]; if (child < nodes.length) nodes[i].right = nodes[child++]; }
        return nodes[0];
    }
 }

