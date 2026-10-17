package com.jinloes.practice;
import org.junit.jupiter.api.Test; import static org.assertj.core.api.Assertions.assertThat;
class CorrectnessTest {
    @Test
    void singleNodeIsSymmetric(){ assertThat(Solution.isSymmetric(tree(7))).isTrue(); }
    @Test
    void equalValuesInNonMirrorShapeAreNotEnough(){ assertThat(Solution.isSymmetric(tree(1,2,2,3,null,3,null))).isFalse(); }
    @Test
    void deepValueMismatchBreaksSymmetry(){ assertThat(Solution.isSymmetric(tree(1,2,2,3,4,5,3))).isFalse(); }
    @Test
    void deeperMirrorTreeIsSymmetric(){ assertThat(Solution.isSymmetric(tree(8,6,6,4,7,7,4,null,5,null,null,null,null,5))).isTrue(); }
    private static Solution.TreeNode tree(Integer... levels) {
        if (levels.length == 0 || levels[0] == null) return null;
        Solution.TreeNode root = new Solution.TreeNode(levels[0]); java.util.ArrayDeque<Solution.TreeNode> q = new java.util.ArrayDeque<>(); q.add(root); int i = 1;
        while (!q.isEmpty() && i < levels.length) { Solution.TreeNode n = q.poll(); if (i < levels.length && levels[i] != null) { n.left = new Solution.TreeNode(levels[i]); q.add(n.left); } i++; if (i < levels.length && levels[i] != null) { n.right = new Solution.TreeNode(levels[i]); q.add(n.right); } i++; }
        return root;
    }
}
