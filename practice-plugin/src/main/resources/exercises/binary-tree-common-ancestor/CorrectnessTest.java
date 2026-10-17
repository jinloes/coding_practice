package com.jinloes.practice;
import org.junit.jupiter.api.Test; import static org.assertj.core.api.Assertions.assertThat;
class CorrectnessTest {
    @Test
    void findsAncestorDeepInOneSubtree(){ Solution.TreeNode root=tree(1,2,3,4,5,null,null,6,7); assertThat(Solution.lowestCommonAncestor(root,root.left.left.left,root.left.left.right)).isSameAs(root.left.left); }
    @Test
    void returnsNullWhenFirstNodeIsMissing(){ Solution.TreeNode root=tree(1,2,3); assertThat(Solution.lowestCommonAncestor(root,new Solution.TreeNode(2),root.right)).isNull(); }
    @Test
    void returnsNullWhenSecondNodeIsMissing(){ Solution.TreeNode root=tree(1,2,3); assertThat(Solution.lowestCommonAncestor(root,root.left,new Solution.TreeNode(3))).isNull(); }
    @Test
    void usesIdentityWhenValuesRepeat(){ Solution.TreeNode root=tree(1,2,2,3,null,3); assertThat(Solution.lowestCommonAncestor(root,root.left.left,root.right.left)).isSameAs(root); }
    private static Solution.TreeNode tree(Integer... levels) {
        if (levels.length == 0 || levels[0] == null) return null;
        Solution.TreeNode root = new Solution.TreeNode(levels[0]); java.util.ArrayDeque<Solution.TreeNode> q = new java.util.ArrayDeque<>(); q.add(root); int i = 1;
        while (!q.isEmpty() && i < levels.length) { Solution.TreeNode n = q.poll(); if (i < levels.length && levels[i] != null) { n.left = new Solution.TreeNode(levels[i]); q.add(n.left); } i++; if (i < levels.length && levels[i] != null) { n.right = new Solution.TreeNode(levels[i]); q.add(n.right); } i++; }
        return root;
    }
}
