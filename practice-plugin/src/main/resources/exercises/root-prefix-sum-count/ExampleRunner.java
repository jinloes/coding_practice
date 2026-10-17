package com.jinloes.practice;
import static org.assertj.core.api.Assertions.assertThat;
public final class ExampleRunner { private static final String USAGE="Input: a level-order tree, then target=<sum>, for example: [5, 3, -1, 2, 1, null, 4] target=8"; private ExampleRunner(){} public static void main(String[] args){ if (args.length > 0) {runInput(String.join(" ",args).trim());return;} assertThat(Solution.countRootPrefixes(tree("[5, 3, -1, 2, 1, null, 4]"),8)).as("Example 1").isEqualTo(2); assertThat(Solution.countRootPrefixes(tree("[1, 2, 3]"),1)).as("Example 2").isEqualTo(1); System.out.println("Examples passed: 2"); } private static void runInput(String input) { try{int m=input.lastIndexOf("target="); if(m<0)throw new IllegalArgumentException(); System.out.println(Solution.countRootPrefixes(tree(input.substring(0,m).trim()),Integer.parseInt(input.substring(m+7).trim())));}catch(RuntimeException e){System.out.println("Could not read \""+input+"\". "+USAGE);} } 
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


