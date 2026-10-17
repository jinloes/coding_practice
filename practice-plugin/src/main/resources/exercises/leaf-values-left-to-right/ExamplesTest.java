package com.jinloes.practice;
import org.junit.jupiter.api.Test; import static org.assertj.core.api.Assertions.assertThat;
class ExamplesTest {
    @Test
    void listsLeavesFromLeftToRight(){ assertThat(Solution.leafValues(tree(1,2,3,4,5,null,6))).containsExactly(4,5,6); }
    @Test
    void singleNodeIsALeaf(){ assertThat(Solution.leafValues(tree(7))).containsExactly(7); }
    @Test
    void emptyTreeHasNoLeaves(){ assertThat(Solution.leafValues(null)).isEmpty(); }
    private static Solution.TreeNode tree(Integer... levels) {
        if (levels.length == 0 || levels[0] == null) return null;
        Solution.TreeNode root = new Solution.TreeNode(levels[0]); java.util.ArrayDeque<Solution.TreeNode> q = new java.util.ArrayDeque<>(); q.add(root); int i = 1;
        while (!q.isEmpty() && i < levels.length) { Solution.TreeNode n = q.poll(); if (i < levels.length && levels[i] != null) { n.left = new Solution.TreeNode(levels[i]); q.add(n.left); } i++; if (i < levels.length && levels[i] != null) { n.right = new Solution.TreeNode(levels[i]); q.add(n.right); } i++; }
        return root;
    }
}
