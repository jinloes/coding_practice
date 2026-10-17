package com.jinloes.practice;
import org.junit.jupiter.api.Test; import static org.assertj.core.api.Assertions.assertThat;
class CorrectnessTest {
    @Test
    void singleNodeAppearsOnce(){ assertThat(Solution.boundaryValues(tree(9))).containsExactly(9); }
    @Test
    void leftChainDoesNotDuplicateLeaves(){ assertThat(Solution.boundaryValues(tree(1,2,null,3,null,4))).containsExactly(1,2,3,4); }
    @Test
    void rightBoundaryIsBottomUp(){ assertThat(Solution.boundaryValues(tree(1,2,3,null,4,null,5,6))).containsExactly(1,2,4,6,5,3); }
    @Test
    void interiorLeavesAreIncludedLeftToRight(){ assertThat(Solution.boundaryValues(tree(10,5,20,null,7,15,null))).containsExactly(10,5,7,15,20); }
    private static Solution.TreeNode tree(Integer... levels) {
        if (levels.length == 0 || levels[0] == null) return null;
        Solution.TreeNode root = new Solution.TreeNode(levels[0]); java.util.ArrayDeque<Solution.TreeNode> q = new java.util.ArrayDeque<>(); q.add(root); int i = 1;
        while (!q.isEmpty() && i < levels.length) { Solution.TreeNode n = q.poll(); if (i < levels.length && levels[i] != null) { n.left = new Solution.TreeNode(levels[i]); q.add(n.left); } i++; if (i < levels.length && levels[i] != null) { n.right = new Solution.TreeNode(levels[i]); q.add(n.right); } i++; }
        return root;
    }
}
