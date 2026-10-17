package com.jinloes.practice;
import static org.assertj.core.api.Assertions.assertThat;
public final class ExampleRunner { private static final String USAGE="Input: target followed by the tree in level order with null for missing children, for example: 22 [5, 4, 8, 11, null, 13, 4, 7, 2]"; private ExampleRunner(){} public static void main(String[] args){ if (args.length > 0) { runInput(String.join(" ",args).trim()); return; } assertThat(Solution.hasPathSum(tree(5,4,8,11,null,13,4,7,2),22)).as("Example 1").isTrue(); assertThat(Solution.hasPathSum(tree(1,2,3),5)).as("Example 2").isFalse(); assertThat(Solution.hasPathSum(null,0)).as("Example 3").isFalse(); System.out.println("Examples passed: 3"); } private static void runInput(String input){ try{ String[] p=input.trim().split("\\s+",2); System.out.println("hasPathSum = "+Solution.hasPathSum(parseTree(p.length==1?"":p[1]),Integer.parseInt(p[0]))); }catch(RuntimeException e){ System.out.println("Could not read input. "+USAGE); } }
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
}
