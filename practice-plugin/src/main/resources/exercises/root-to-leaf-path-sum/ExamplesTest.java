package com.jinloes.practice;
import org.junit.jupiter.api.Test; import static org.assertj.core.api.Assertions.assertThat;
class ExamplesTest {
    @Test
    void findsExistingPath(){ assertThat(Solution.hasPathSum(tree(5,4,8,11,null,13,4,7,2),22)).isTrue(); }
    @Test
    void rejectsMissingPath(){ assertThat(Solution.hasPathSum(tree(1,2,3),5)).isFalse(); }
    @Test
    void emptyTreeHasNoPath(){ assertThat(Solution.hasPathSum(null,0)).isFalse(); }
    private static Solution.TreeNode tree(Integer... levels) {
        if (levels.length == 0 || levels[0] == null) return null;
        Solution.TreeNode root = new Solution.TreeNode(levels[0]); java.util.ArrayDeque<Solution.TreeNode> q = new java.util.ArrayDeque<>(); q.add(root); int i = 1;
        while (!q.isEmpty() && i < levels.length) { Solution.TreeNode n = q.poll(); if (i < levels.length && levels[i] != null) { n.left = new Solution.TreeNode(levels[i]); q.add(n.left); } i++; if (i < levels.length && levels[i] != null) { n.right = new Solution.TreeNode(levels[i]); q.add(n.right); } i++; }
        return root;
    }
}
