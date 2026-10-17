package com.jinloes.practice;
import org.junit.jupiter.api.Test; import static org.assertj.core.api.Assertions.assertThat;
class CorrectnessTest {
    @Test
    void kZeroReturnsNoValues(){ assertThat(Solution.kLargest(tree(2,1,3),0)).isEmpty(); }
    @Test
    void handlesLeftLeaningSearchTree(){ assertThat(Solution.kLargest(tree(10,5,null,2,7),4)).containsExactly(10,7,5,2); }
    @Test
    void handlesRightLeaningSearchTree(){ assertThat(Solution.kLargest(tree(1,null,2,null,3,null,4),2)).containsExactly(4,3); }
    @Test
    void handlesNegativeValues(){ assertThat(Solution.kLargest(tree(0,-10,9,-20,-5),3)).containsExactly(9,0,-5); }
    private static Solution.TreeNode tree(Integer... levels) {
        if (levels.length == 0 || levels[0] == null) return null;
        Solution.TreeNode root = new Solution.TreeNode(levels[0]); java.util.ArrayDeque<Solution.TreeNode> q = new java.util.ArrayDeque<>(); q.add(root); int i = 1;
        while (!q.isEmpty() && i < levels.length) { Solution.TreeNode n = q.poll(); if (i < levels.length && levels[i] != null) { n.left = new Solution.TreeNode(levels[i]); q.add(n.left); } i++; if (i < levels.length && levels[i] != null) { n.right = new Solution.TreeNode(levels[i]); q.add(n.right); } i++; }
        return root;
    }
}
